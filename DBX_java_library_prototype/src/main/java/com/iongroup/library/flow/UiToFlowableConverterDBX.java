package com.iongroup.library.flow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * DBX internal UI -> Flowable editor JSON converter (mirror of existing logic)
 */
public class UiToFlowableConverterDBX {

    private static final ObjectMapper M = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private static void copyIfPresent(JsonNode src, ObjectNode dst, String field) {
        JsonNode v = src.get(field);
        if (v != null && !v.isNull()) {
            dst.put(field, v.asText());
        }
    }

    // -------------------------------
    // Core conversion
    // -------------------------------
    public static ObjectNode convert(JsonNode uiJson, ConverterConfig cfg) {

        ArrayNode uiNodes = arrayOrEmpty(uiJson.get("nodes"));
        ArrayNode uiEdges = arrayOrEmpty(uiJson.get("edges"));

        Map<String, List<JsonNode>> outEdgesByNode = new HashMap<>();
        Map<String, List<JsonNode>> inEdgesByNode = new HashMap<>();

        for (JsonNode e : uiEdges) {
            String s = text(e, "source");
            String t = text(e, "target");
            if (s != null) outEdgesByNode.computeIfAbsent(s, k -> new ArrayList<>()).add(e);
            if (t != null) inEdgesByNode.computeIfAbsent(t, k -> new ArrayList<>()).add(e);
        }

        Map<String, ObjectNode> idToShape = new LinkedHashMap<>();
        List<String> orderedNodeIds = new ArrayList<>();

        for (JsonNode n : uiNodes) {

            String origId = text(n, "id");
            if (origId == null) continue;

            JsonNode data = n.path("data");

            String nodeType = text(data, "nodeType");
            String label = text(data, "label");
            if (label == null || label.isEmpty()) {
                label = nodeType != null ? capitalize(nodeType) : "Node";
            }

            NodeTypeConfig mapping = cfg.nodeTypeMap.getOrDefault(
                nvl(nodeType).toLowerCase(Locale.ROOT),
                NodeTypeConfig.of("ServiceTask", obj("name", label))
            );

            ObjectNode properties = M.createObjectNode();
            properties.setAll(mapping.properties);

            copyIfPresent(data, properties, "delegationId");
            copyIfPresent(data, properties, "delegationName");
            copyIfPresent(data, properties, "delegationType");

            if ("user".equalsIgnoreCase(nodeType)) {
                JsonNode selected = data.get("selectedFields");
                if (selected != null && selected.isArray() && selected.size() > 0) {
                    List<String> fields = new ArrayList<>();
                    selected.forEach(f -> fields.add(f.asText()));
                    properties.put("requiredFields", String.join(",", fields));
                }
            }

            JsonNode customFields = data.get("customFields");
            if (customFields != null && customFields.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> it = customFields.fields();
                while (it.hasNext()) {
                    Map.Entry<String, JsonNode> e = it.next();
                    String uiKey = e.getKey();
                    String value = e.getValue().asText();
                    String flowableKey;
                    if ("AMOUNT".equals(uiKey)) {
                        flowableKey = "requestedAmountLimit";
                    } else {
                        flowableKey = uiKey;
                    }
                    properties.put(flowableKey, value);
                }
            }

            if (properties.get("name") == null && !"start".equalsIgnoreCase(nodeType) && !"end".equalsIgnoreCase(nodeType)) {
                properties.put("name", label);
            }

            if ("parallel".equalsIgnoreCase(nodeType)) {
                int indeg = inEdgesByNode.getOrDefault(origId, Collections.emptyList()).size();
                int outdeg = outEdgesByNode.getOrDefault(origId, Collections.emptyList()).size();
                if (outdeg > 1 && indeg <= 1) {
                    properties.put("name", "Parallel Split");
                } else if (indeg > 1 && outdeg <= 1) {
                    properties.put("name", "Parallel Join");
                }
            }

            if (cfg.nodeOverrides.containsKey(origId)) {
                deepMerge(properties, cfg.nodeOverrides.get(origId));
            }

            Bounds b = boundsFromUi(n);
            ObjectNode bounds = M.createObjectNode();
            bounds.set("upperLeft", point(b.ulx, b.uly));
            bounds.set("lowerRight", point(b.lrx, b.lry));

            String resourceId = sanitize(origId);

            ObjectNode shape = M.createObjectNode();
            shape.put("resourceId", resourceId);
            shape.set("properties", properties);
            shape.set("stencil", obj("id", mapping.stencilId));
            shape.set("childShapes", M.createArrayNode());
            shape.set("bounds", bounds);
            shape.set("outgoing", M.createArrayNode());
            shape.set("incoming", M.createArrayNode());

            idToShape.put(origId, shape);
            orderedNodeIds.add(origId);
        }

        List<ObjectNode> flowShapes = new ArrayList<>();

        for (JsonNode e : uiEdges) {
            String eid = text(e, "id");
            String sourceId = text(e, "source");
            String targetId = text(e, "target");
            if (sourceId == null || targetId == null) continue;

            ObjectNode srcShape = idToShape.get(sourceId);
            ObjectNode tgtShape = idToShape.get(targetId);
            if (srcShape == null || tgtShape == null) continue;

            String flowRid = sanitize(eid != null ? eid : "flow_" + sourceId + "__" + targetId);

            double[] sc = centerOfBounds(srcShape.path("bounds"));
            double[] tc = centerOfBounds(tgtShape.path("bounds"));

            ObjectNode flowBounds = M.createObjectNode();
            flowBounds.set("upperLeft", point(
                Math.min(sc[0], tc[0]) - cfg.flowBoundsPadding,
                Math.min(sc[1], tc[1]) - cfg.flowBoundsPadding
            ));
            flowBounds.set("lowerRight", point(
                Math.max(sc[0], tc[0]) + cfg.flowBoundsPadding,
                Math.max(sc[1], tc[1]) + cfg.flowBoundsPadding
            ));

            ObjectNode flowShape = M.createObjectNode();
            flowShape.put("resourceId", flowRid);
            flowShape.set("properties", obj("overrideid", flowRid));
            flowShape.set("stencil", obj("id", "SequenceFlow"));
            flowShape.set("childShapes", M.createArrayNode());
            flowShape.set("bounds", flowBounds);

            flowShape.set("dockers", arrayOf(point(15, 15), point(15, 15)));

            flowShape.set("outgoing", arrayOf(obj("resourceId", tgtShape.get("resourceId"))));
            flowShape.set("incoming", arrayOf(obj("resourceId", srcShape.get("resourceId"))));
            flowShape.set("target", obj("resourceId", tgtShape.get("resourceId")));

            ((ArrayNode) srcShape.withArray("outgoing")).add(obj("resourceId", flowRid));
            ((ArrayNode) tgtShape.withArray("incoming")).add(obj("resourceId", flowRid));

            flowShapes.add(flowShape);
        }

        ObjectNode canvas = M.createObjectNode();
        canvas.put("resourceId", "canvas");
        canvas.set("stencil", obj("id", "BPMNDiagram"));
        canvas.set("properties", obj(
            "process_id", cfg.process.processId,
            "name", cfg.process.name,
            "process_namespace", cfg.process.namespace
        ));

        ArrayNode childShapes = M.createArrayNode();
        orderedNodeIds.forEach(id -> childShapes.add(idToShape.get(id)));
        flowShapes.forEach(childShapes::add);

        canvas.set("childShapes", childShapes);
        canvas.set("bounds", obj(
            "upperLeft", point(0, 0),
            "lowerRight", point(1700, 1200)
        ));

        canvas.set("stencilset", obj(
            "namespace", "http://b3mn.org/stencilset/bpmn2.0#",
            "url", "stencilsets/bpmn2.0/bpmn2.0.json"
        ));
        canvas.set("ssextensions", M.createArrayNode());

        return canvas;
    }

    // -------------------------------
    // Config types & utilities
    // -------------------------------

    public static class ConverterConfig {
        public ProcessConfig process = new ProcessConfig();
        public Map<String, NodeTypeConfig> nodeTypeMap = new LinkedHashMap<>();
        public Map<String, ObjectNode> nodeOverrides = new LinkedHashMap<>();
        public Map<String, ObjectNode> flowOverrides = new LinkedHashMap<>();
        public int flowBoundsPadding = 10;

        public static ConverterConfig defaultConfig() {
            ConverterConfig c = new ConverterConfig();
            c.process = new ProcessConfig("ConvertedProcess", "Converted Process", "http://flowable.org/test");

            c.nodeTypeMap.put("start", NodeTypeConfig.of("StartNoneEvent", obj("overrideid", "startEvent")));
            c.nodeTypeMap.put("end", NodeTypeConfig.of("EndNoneEvent", obj("overrideid", "endEvent")));
            c.nodeTypeMap.put("script", NodeTypeConfig.of("ScriptTask", obj(
                    "name", "Script Task",
                    "scriptformat", "groovy",
                    "script", "// your script here"
            )));
            c.nodeTypeMap.put("service", NodeTypeConfig.of("ServiceTask", obj(
                    "name", "Service Task",
                    "asynchronousdefinition", "false",
                    "exclusive", "true",

                    // 🔒 SINGLE DISPATCHER — NEVER CHANGE
                    "servicetaskclass",
                    "com.iongroup.library.adapter.flowable.OperationDispatcherDelegate"
            )));

            c.nodeTypeMap.put("user", NodeTypeConfig.of("UserTask", obj(
                    "name", "User Task",
                    "assignee", "${initiator}",
                    "candidateGroups", "",
                    "priority", "50"
            )));
            c.nodeTypeMap.put("parallel", NodeTypeConfig.of("ParallelGateway", obj(
                    "name", ""
            )));
            return c;
        }

        public void applyFromJson(JsonNode root) {
            if (root == null || root.isMissingNode()) return;
            JsonNode p = root.get("process");
            if (p != null) {
                this.process.processId = textOrDefault(p, "process_id", this.process.processId);
                this.process.name = textOrDefault(p, "name", this.process.name);
                this.process.namespace = textOrDefault(p, "process_namespace", this.process.namespace);
            }

            if (root.has("nodeTypeMap")) {
                JsonNode ntm = root.get("nodeTypeMap");
                Iterator<String> it = ntm.fieldNames();
                while (it.hasNext()) {
                    String key = it.next();
                    JsonNode spec = ntm.get(key);
                    String stencil = textOrDefault(spec, "stencil", null);
                    ObjectNode props = spec.has("properties") && spec.get("properties").isObject()
                            ? (ObjectNode) spec.get("properties") : M.createObjectNode();
                    if (stencil != null) {
                        this.nodeTypeMap.put(key.toLowerCase(Locale.ROOT), NodeTypeConfig.of(stencil, props));
                    }
                }
            }

            if (root.has("nodeOverrides")) {
                JsonNode no = root.get("nodeOverrides");
                Iterator<String> it = no.fieldNames();
                while (it.hasNext()) {
                    String nodeId = it.next();
                    ObjectNode props = M.createObjectNode();
                    JsonNode pnode = no.get(nodeId).get("properties");
                    if (pnode != null && pnode.isObject()) props.setAll((ObjectNode) pnode);
                    this.nodeOverrides.put(nodeId, props);
                }
            }

            if (root.has("flowOverrides")) {
                JsonNode fo = root.get("flowOverrides");
                Iterator<String> it = fo.fieldNames();
                while (it.hasNext()) {
                    String flowId = it.next();
                    ObjectNode props = M.createObjectNode();
                    JsonNode pnode = fo.get(flowId).get("properties");
                    if (pnode != null && pnode.isObject()) props.setAll((ObjectNode) pnode);
                    this.flowOverrides.put(flowId, props);
                }
            }

            if (root.has("flowBoundsPadding")) {
                this.flowBoundsPadding = root.get("flowBoundsPadding").asInt(this.flowBoundsPadding);
            }
        }
    }

    public static class ProcessConfig {
        public String processId;
        public String name;
        public String namespace;

        public ProcessConfig() {}

        public ProcessConfig(String processId, String name, String namespace) {
            this.processId = processId;
            this.name = name;
            this.namespace = namespace;
        }
    }

    public static class NodeTypeConfig {
        public String stencilId;
        public ObjectNode properties;

        public static NodeTypeConfig of(String stencilId, ObjectNode properties) {
            NodeTypeConfig c = new NodeTypeConfig();
            c.stencilId = stencilId;
            c.properties = properties != null ? properties : M.createObjectNode();
            return c;
        }
    }

    static class Bounds { double ulx, uly, lrx, lry; Bounds(double ulx, double uly, double lrx, double lry) { this.ulx = ulx; this.uly = uly; this.lrx = lrx; this.lry = lry; }}

    private static Bounds boundsFromUi(JsonNode n) {
        JsonNode pos = n.has("positionAbsolute") ? n.get("positionAbsolute") : n.get("position");
        double x = pos != null ? pos.path("x").asDouble(0) : 0;
        double y = pos != null ? pos.path("y").asDouble(0) : 0;
        double w = n.path("width").asDouble(100);
        double h = n.path("height").asDouble(60);
        return new Bounds(x, y, x + w, y + h);
    }

    private static double[] centerOfBounds(JsonNode bounds) {
        JsonNode ul = bounds.get("upperLeft");
        JsonNode lr = bounds.get("lowerRight");
        double cx = (ul.path("x").asDouble() + lr.path("x").asDouble()) / 2.0;
        double cy = (ul.path("y").asDouble() + lr.path("y").asDouble()) / 2.0;
        return new double[]{cx, cy};
    }

    private static ObjectNode obj(Object... kv) {
        ObjectNode o = M.createObjectNode();
        for (int i = 0; i < kv.length; i += 2) {
            String k = String.valueOf(kv[i]);
            Object v = kv[i + 1];
            if (v instanceof String) o.put(k, (String) v);
            else if (v instanceof Integer) o.put(k, (Integer) v);
            else if (v instanceof Long) o.put(k, (Long) v);
            else if (v instanceof Double) o.put(k, (Double) v);
            else if (v instanceof Boolean) o.put(k, (Boolean) v);
            else if (v instanceof JsonNode) o.set(k, (JsonNode) v);
            else if (v == null) o.putNull(k);
            else o.putPOJO(k, v);
        }
        return o;
    }

    private static ArrayNode arrayOf(JsonNode... nodes) {
        ArrayNode arr = M.createArrayNode();
        for (JsonNode n : nodes) arr.add(n);
        return arr;
    }

    private static ObjectNode point(double x, double y) {
        ObjectNode p = M.createObjectNode();
        p.put("x", x);
        p.put("y", y);
        return p;
    }

    private static String text(JsonNode n, String field) {
        if (n == null || n.isMissingNode()) return null;
        JsonNode v = n.get(field);
        return v != null && !v.isNull() ? v.asText() : null;
    }

    private static String textOrDefault(JsonNode n, String field, String def) {
        String t = text(n, field);
        return t != null ? t : def;
    }

    private static ArrayNode arrayOrEmpty(JsonNode n) {
        return (n != null && n.isArray()) ? (ArrayNode) n : M.createArrayNode();
    }

    private static String nvl(String s) { return s == null ? "" : s; }

    private static String capitalize(String s) { if (s == null || s.isEmpty()) return s; return Character.toUpperCase(s.charAt(0)) + s.substring(1); }

    private static String sanitize(String id) {
        if (id == null) return "id";
        StringBuilder sb = new StringBuilder();
        for (char c : id.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == '_' || c == '-') sb.append(c);
            else sb.append('_');
        }
        return sb.toString();
    }

    private static void deepMerge(ObjectNode a, ObjectNode b) {
        Iterator<Map.Entry<String, JsonNode>> fields = b.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> e = fields.next();
            String k = e.getKey();
            JsonNode v = e.getValue();
            if (v != null && v.isObject() && a.has(k) && a.get(k).isObject()) {
                deepMerge((ObjectNode) a.get(k), (ObjectNode) v);
            } else {
                a.set(k, v);
            }
        }
    }

    private static Map<String, String> mapOf(String k, String v) { Map<String, String> m = new LinkedHashMap<>(); m.put(k, v); return m; }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    m.put(key, args[++i]);
                } else {
                    m.put(key, "true");
                }
            }
        }
        return m;
    }
}
