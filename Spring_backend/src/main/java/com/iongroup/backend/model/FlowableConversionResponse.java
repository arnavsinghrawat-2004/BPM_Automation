package com.iongroup.backend.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;

/**
 * API response model for Flowable conversion operations.
 */
public class FlowableConversionResponse {
    private boolean success;
    private String message;
    private String bpmnXml;
    private Map<String, Object> flowableJson;
    private Map<String, Object> executionResult;

    public FlowableConversionResponse() {}

    // public FlowableConversionResponse(boolean success, String message, String bpmnXml, 
    //                                   ObjectNode flowableJsonNode, Map<String, Object> executionResult) {
    //     this.success = success;
    //     this.message = message;
    //     this.bpmnXml = bpmnXml;
    //     // Convert ObjectNode to Map for proper JSON serialization
    //     ObjectMapper mapper = new ObjectMapper();
    //     this.flowableJson = mapper.convertValue(flowableJsonNode, Map.class);
    //     this.executionResult = executionResult;
    // }
    public FlowableConversionResponse(
            JsonNode flowableJsonNode,
            String bpmnXml
    ) {
        this.success = true;
        this.message = "Conversion successful";
        this.bpmnXml = bpmnXml;

        ObjectMapper mapper = new ObjectMapper();
        this.flowableJson = mapper.convertValue(flowableJsonNode, Map.class);

        this.executionResult = null;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBpmnXml() {
        return bpmnXml;
    }

    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml;
    }

    public Map<String, Object> getFlowableJson() {
        return flowableJson;
    }

    public void setFlowableJson(Map<String, Object> flowableJson) {
        this.flowableJson = flowableJson;
    }

    public Map<String, Object> getExecutionResult() {
        return executionResult;
    }

    public void setExecutionResult(Map<String, Object> executionResult) {
        this.executionResult = executionResult;
    }
}
