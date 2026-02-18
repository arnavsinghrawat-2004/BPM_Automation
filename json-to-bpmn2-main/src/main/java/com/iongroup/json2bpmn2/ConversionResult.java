package com.iongroup.json2bpmn2;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Result of UI JSON → BPMN conversion
 */
public final class ConversionResult {

    private final JsonNode flowableJson;
    private final String bpmnXml;

    public ConversionResult(JsonNode flowableJson, String bpmnXml) {
        this.flowableJson = flowableJson;
        this.bpmnXml = bpmnXml;
    }

    public JsonNode getFlowableJson() {
        return flowableJson;
    }

    public String getBpmnXml() {
        return bpmnXml;
    }
}
