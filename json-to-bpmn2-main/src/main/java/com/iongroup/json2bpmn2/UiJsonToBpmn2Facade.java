package com.iongroup.json2bpmn2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;

import java.nio.charset.StandardCharsets;

/**
 * SINGLE ENTRY POINT for UI JSON → BPMN 2.0 XML
 *
 * Pipeline:
 * 1. UI JSON (React-Flow format) → Flowable Modeler JSON
 * 2. Flowable JSON → BPMN Model + enrichment
 * 3. BPMN Model → BPMN 2.0 XML
 *
 * This is the ONLY class the Spring backend should ever call.
 */
public final class UiJsonToBpmn2Facade {

    private static final ObjectMapper M = new ObjectMapper();

    private UiJsonToBpmn2Facade() {
        // utility class
    }

    /**
     * Convert UI JSON all the way to BPMN 2.0 XML
     * @param uiJson Your React-Flow style UI JSON
     * @return ConversionResult with both intermediate Flowable JSON and final BPMN XML
     */
    public static ConversionResult convert(JsonNode uiJson) {

        // 1️⃣ UI JSON → Flowable Modeler JSON
        ObjectNode flowableJson = UiToFlowableConverter.convert(
            uiJson,
            UiToFlowableConverter.ConverterConfig.defaultConfig()
        );

        // 2️⃣ Flowable JSON → BPMN model (+ enrichment inside)
        BpmnModel model = JsonToBpmn2Converter.convertAndEnrich(flowableJson);

        // 3️⃣ BPMN model → BPMN 2.0 XML
        BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
        byte[] xmlBytes = xmlConverter.convertToXML(model);

        String bpmnXml = new String(xmlBytes, StandardCharsets.UTF_8);

        return new ConversionResult(flowableJson, bpmnXml);
    }

    /**
     * Holds the conversion result
     */
    public static class ConversionResult {
        private final ObjectNode flowableJson;
        private final String bpmnXml;

        public ConversionResult(ObjectNode flowableJson, String bpmnXml) {
            this.flowableJson = flowableJson;
            this.bpmnXml = bpmnXml;
        }

        public ObjectNode getFlowableJson() {
            return flowableJson;
        }

        public String getBpmnXml() {
            return bpmnXml;
        }

        /**
         * Pretty-print the intermediate Flowable JSON (useful for debugging)
         */
        public String getFlowableJsonPretty() {
            try {
                return M.writerWithDefaultPrettyPrinter().writeValueAsString(flowableJson);
            } catch (Exception e) {
                return flowableJson.toString();
            }
        }
    }
}