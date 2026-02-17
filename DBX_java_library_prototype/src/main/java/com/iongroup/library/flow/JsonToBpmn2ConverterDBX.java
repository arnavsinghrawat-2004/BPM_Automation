package com.iongroup.library.flow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.bpmn.model.UserTask;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.Process;

import org.flowable.editor.language.json.converter.BpmnJsonConverter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * DBX internal version of JsonToBpmn2Converter (keeps behavior identical)
 */
public class JsonToBpmn2ConverterDBX {
    private static final String DISPATCHER_CLASS =
        "com.iongroup.library.adapter.flowable.OperationDispatcherDelegate";

    /**
     * Convert a Flowable editor JSON string to BPMN 2.0 XML bytes
     */
    public static byte[] convertJsonToBpmn(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode editorRoot = mapper.readTree(json);

        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        BpmnModel model = jsonConverter.convertToBpmnModel(editorRoot);

        if (model == null || model.getProcesses().isEmpty()) {
            throw new IllegalStateException("No BPMN processes generated from JSON");
        }

        enrichTasks(editorRoot, model);

        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] xmlBytes = xmlConverter.convertToXML(model);

        // Normalize XML to remove exporterVersion (Flowable may add version attribute)
        String xml = new String(xmlBytes, StandardCharsets.UTF_8);
        xml = xml.replaceAll("\\s+exporterVersion=\"[^\"]+\"", "");
        return xml.getBytes(StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------------

    private static void enrichTasks(JsonNode editorRoot, BpmnModel model) {

        Map<String, JsonNode> idToProps = new HashMap<>();
        collectTaskProperties(editorRoot, idToProps);

        java.util.Set<String> reservedKeys = java.util.Set.of(
            "name",
            "delegationId",
            "delegationType",
            "selectedFields",
            "requiredFields"
        );

        for (Process process : model.getProcesses()) {
            for (FlowElement fe : process.getFlowElements()) {

                JsonNode props = idToProps.get(fe.getId());
                if (props == null) continue;

                String label = text(props.get("name"));
                String delegationId = text(props.get("delegationId"));

                if (fe instanceof ServiceTask) {
                    ServiceTask st = (ServiceTask) fe;

                    // mirror json-to-bpmn2 behavior: route service tasks to dispatcher class
                    st.setImplementationType("class");
                    st.setImplementation(DISPATCHER_CLASS);

                    if (label != null) st.setName(label);

                    addExt(st, "delegationId", delegationId);
                    addExt(st, "delegationType", text(props.get("delegationType")));
                    addExt(st, "selectedFields", text(props.get("selectedFields")));
                    addExt(st, "requiredFields", text(props.get("requiredFields")));

                    Iterator<String> it = props.fieldNames();
                    while (it.hasNext()) {
                        String key = it.next();
                        if (!reservedKeys.contains(key)) {
                            addExt(st, key, text(props.get(key)));
                        }
                    }
                } else if (fe instanceof UserTask) {
                    UserTask ut = (UserTask) fe;
                    if (label != null) ut.setName(label);

                    addExt(ut, "delegationId", delegationId);
                    addExt(ut, "selectedFields", text(props.get("selectedFields")));
                    addExt(ut, "requiredFields", text(props.get("requiredFields")));
                }
            }
        }
    }

    private static void collectTaskProperties(JsonNode node, Map<String, JsonNode> out) {
        if (node == null) return;

        JsonNode stencil = node.get("stencil");
        if (stencil != null) {
            String type = stencil.path("id").asText();
            if ("ServiceTask".equals(type) || "UserTask".equals(type)) {
                String id = node.path("properties").path("overrideid").asText(node.path("resourceId").asText(null));
                if (id != null && node.has("properties")) {
                    out.put(id, node.get("properties"));
                }
            }
        }

        JsonNode children = node.get("childShapes");
        if (children != null && children.isArray()) {
            for (JsonNode c : children) collectTaskProperties(c, out);
        }
    }

    private static void addExt(BaseElement el, String key, String val) {
        if (val == null || val.isBlank()) return;

        ExtensionElement ext = new ExtensionElement();
        ext.setNamespace("http://flowable.org/bpmn");
        ext.setNamespacePrefix("flowable");
        ext.setName(key);
        ext.setElementText(val);

        el.addExtensionElement(ext);
    }

    private static String text(JsonNode n) {
        return (n == null || n.isNull()) ? null : n.asText();
    }
}
