package com.iongroup.library.flow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;

import java.nio.charset.StandardCharsets;

/**
 * DBX library entry point: convert UI JSON -> Flowable JSON -> BPMN XML
 */
public class UiJsonToBpmnConverter {

    private static final ObjectMapper M = new ObjectMapper();

    public static class ConversionResult {
        public final byte[] bpmnXml;
        public final ObjectNode flowableJson;
        public final BpmnModel bpmnModel;

        public ConversionResult(byte[] bpmnXml, ObjectNode flowableJson, BpmnModel bpmnModel) {
            this.bpmnXml = bpmnXml;
            this.flowableJson = flowableJson;
            this.bpmnModel = bpmnModel;
        }
    }

    public static ConversionResult convert(String uiJsonStr, String configJsonStr) throws Exception {
        JsonNode uiNode = M.readTree(uiJsonStr);

        UiToFlowableConverterDBX.ConverterConfig cfg = UiToFlowableConverterDBX.ConverterConfig.defaultConfig();
        if (configJsonStr != null && !configJsonStr.isBlank()) {
            JsonNode cfgNode = M.readTree(configJsonStr);
            cfg.applyFromJson(cfgNode);
        }

        ObjectNode flowableJson = UiToFlowableConverterDBX.convert(uiNode, cfg);

        // Convert to BpmnModel using Flowable converter
        org.flowable.editor.language.json.converter.BpmnJsonConverter jsonConverter = new org.flowable.editor.language.json.converter.BpmnJsonConverter();
        BpmnModel model = jsonConverter.convertToBpmnModel(flowableJson);

        if (model == null || model.getProcesses().isEmpty()) {
            throw new IllegalStateException("No BPMN processes generated from Flowable JSON");
        }

        // Generate XML bytes (enrichment performed by JsonToBpmn2ConverterDBX)
        String flowableStr = M.writeValueAsString(flowableJson);
        byte[] xml = JsonToBpmn2ConverterDBX.convertJsonToBpmn(flowableStr);

        return new ConversionResult(xml, flowableJson, model);
    }
}
