package com.iongroup.json2bpmn2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Converts UI JSON → Flowable BPMN 2.0 (.bpmn20.xml)
 * Fully controlled conversion (NO Flowable defaults leaking in).
 */
public class JsonToBpmn2Converter {

    private static final String PROCESS_ID = "ComplexProcess";
    private static final String PROCESS_NAME = "Complex Loan Process";

    private static final String DISPATCHER_CLASS =
        "com.iongroup.library.adapter.flowable.OperationDispatcherDelegate";

    /* ====================================================================== */
    /*  PUBLIC API (used by Spring backend)                                   */
    /* ====================================================================== */
    public static String convert(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode editorRoot = mapper.readTree(json);

            BpmnModel model = convertAndEnrich(editorRoot);

            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] xmlBytes = xmlConverter.convertToXML(model);

            return new String(xmlBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to BPMN 2.0 XML", e);
        }
    }

    public static BpmnModel convertAndEnrich(JsonNode editorRoot) {

        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        BpmnModel model = jsonConverter.convertToBpmnModel(editorRoot);

        if (model == null || model.getProcesses().isEmpty()) {
            throw new IllegalStateException("No BPMN processes generated from JSON");
        }

        // 🔒 Force main process identity (Flowable otherwise injects defaults)
        org.flowable.bpmn.model.Process main = model.getMainProcess();
        main.setId(PROCESS_ID);
        main.setName(PROCESS_NAME);

        enrichTasks(editorRoot, model);

        return model;
    }

    /* ====================================================================== */
    /*  CLI entry point (optional)                                             */
    /* ====================================================================== */

    public static void main(String[] args) throws Exception {

        if (args.length == 0 || args.length > 2) {
            System.err.println(
                "Usage: java -jar json2bpmn2.jar <input.json> [output.bpmn20.xml]"
            );
            System.exit(2);
        }

        Path in = Path.of(args[0]);
        if (!Files.exists(in)) {
            System.err.println("Input file does not exist: " + in);
            System.exit(2);
        }

        Path out = (args.length == 2)
            ? Path.of(args[1])
            : in.resolveSibling(stripExtension(in.getFileName().toString()) + ".bpmn20.xml");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode editorRoot = mapper.readTree(
            Files.readString(in, StandardCharsets.UTF_8)
        );

        BpmnModel model = convertAndEnrich(editorRoot);

        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] xml = xmlConverter.convertToXML(model);

        if (out.getParent() != null) Files.createDirectories(out.getParent());
        Files.write(out, xml);

        System.out.println("✔ BPMN generated: " + out.toAbsolutePath());
    }

    /* ====================================================================== */
    /*  TASK ENRICHMENT                                                        */
    /* ====================================================================== */

    private static void enrichTasks(JsonNode editorRoot, BpmnModel model) {

        Map<String, JsonNode> idToProps = new HashMap<>();
        collectTaskProperties(editorRoot, idToProps);

        Set<String> reservedKeys = Set.of(
            "name",
            "delegationId",
            "delegationType",
            "selectedFields",
            "requiredFields",
            "customFields"
        );

        for (org.flowable.bpmn.model.Process process : model.getProcesses()) {
            for (FlowElement fe : process.getFlowElements()) {

                JsonNode props = idToProps.get(fe.getId());
                if (props == null) continue;

                String label = text(props.get("name"));
                String delegationId = text(props.get("delegationId"));

                /* ===================== SERVICE TASK ===================== */

                if (fe instanceof ServiceTask st) {

                    st.setImplementationType("class");
                    st.setImplementation(DISPATCHER_CLASS);

                    if (label != null) st.setName(label);

                    addExt(st, "delegationId", delegationId);
                    addExt(st, "delegationType", text(props.get("delegationType")));
                    addExt(st, "selectedFields", stringify(props.get("selectedFields")));
                    addExt(st, "requiredFields", stringify(props.get("requiredFields")));

                    // 🔥 Flatten customFields { AMOUNT: 1000 }
                    JsonNode customFields = props.get("customFields");
                    if (customFields != null && customFields.isObject()) {
                        customFields.fieldNames().forEachRemaining(k ->
                            addExt(st, k, text(customFields.get(k)))
                        );
                    }

                    // Copy any remaining simple properties
                    props.fieldNames().forEachRemaining(key -> {
                        if (!reservedKeys.contains(key)) {
                            addExt(st, key, text(props.get(key)));
                        }
                    });
                }

                /* ======================= USER TASK ======================= */

                else if (fe instanceof UserTask ut) {

                    if (label != null) ut.setName(label);

                    addExt(ut, "delegationId", delegationId);
                    addExt(ut, "selectedFields", stringify(props.get("selectedFields")));
                    addExt(ut, "requiredFields", stringify(props.get("requiredFields")));
                }
            }
        }
    }

    /* ====================================================================== */
    /*  JSON WALKER                                                            */
    /* ====================================================================== */

    private static void collectTaskProperties(JsonNode node, Map<String, JsonNode> out) {
        if (node == null) return;

        JsonNode stencil = node.get("stencil");
        if (stencil != null) {
            String type = stencil.path("id").asText();
            if ("ServiceTask".equals(type) || "UserTask".equals(type)) {

                String id =
                    node.path("properties")
                        .path("overrideid")
                        .asText(node.path("resourceId").asText(null));

                if (id != null && node.has("properties")) {
                    out.put(id, node.get("properties"));
                }
            }
        }

        JsonNode children = node.get("childShapes");
        if (children != null && children.isArray()) {
            for (JsonNode c : children) {
                collectTaskProperties(c, out);
            }
        }
    }

    /* ====================================================================== */
    /*  HELPERS                                                                */
    /* ====================================================================== */

    private static void addExt(BaseElement el, String key, String val) {
        if (val == null || val.isBlank()) return;

        ExtensionElement ext = new ExtensionElement();
        ext.setNamespace("http://flowable.org/bpmn");
        ext.setNamespacePrefix("flowable");
        ext.setName(key);
        ext.setElementText(val);

        el.addExtensionElement(ext);
    }

    private static String stringify(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isArray()) {
            List<String> vals = new ArrayList<>();
            node.forEach(n -> vals.add(n.asText()));
            return String.join(",", vals);
        }
        return node.asText();
    }

    private static String text(JsonNode n) {
        return (n == null || n.isNull()) ? null : n.asText();
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return (dot >= 0) ? name.substring(0, dot) : name;
    }
}
