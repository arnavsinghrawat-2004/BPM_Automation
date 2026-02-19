package com.iongroup.library.flowable_service;

import com.iongroup.json2bpmn2.UiJsonToBpmn2Facade;
import com.iongroup.json2bpmn2.UiJsonToBpmn2Facade.ConversionResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UiJsonToBpmnService {

    private final ObjectMapper mapper = new ObjectMapper();

    public ConversionResult convert(String uiJson) {
        try {
            JsonNode root = mapper.readTree(uiJson);
            return UiJsonToBpmn2Facade.convert(root);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert UI JSON to BPMN XML", e);
        }
    }
}