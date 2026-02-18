package com.example.flow;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;

import java.nio.charset.StandardCharsets;

/**
 * SINGLE ENTRY POINT for UI JSON → BPMN 2.0 XML
 *
 * This is the ONLY class the Spring backend should ever call.
 */
public final class UiJsonToBpmn2Facade {

    private static final ObjectMapper M = new ObjectMapper();

    private UiJsonToBpmn2Facade() {
        // utility class
    }

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
}
