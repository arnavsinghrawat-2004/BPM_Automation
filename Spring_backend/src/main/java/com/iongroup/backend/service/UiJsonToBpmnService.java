package com.iongroup.backend.service;

import com.iongroup.json2bpmn2.JsonToBpmn2Converter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class UiJsonToBpmnService {

    private final ObjectMapper mapper = new ObjectMapper();

    public String convert(String uiJson) {
        try {
            JsonNode root = mapper.readTree(uiJson);
            BpmnModel model = JsonToBpmn2Converter.convertAndEnrich(root);
            byte[] xmlBytes = new BpmnXMLConverter().convertToXML(model);
            return new String(xmlBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert UI JSON to BPMN XML", e);
        }
    }
}
