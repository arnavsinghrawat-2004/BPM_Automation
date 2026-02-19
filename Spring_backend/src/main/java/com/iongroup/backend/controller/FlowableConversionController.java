package com.iongroup.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.iongroup.backend.model.FlowableConversionResponse;
import com.iongroup.json2bpmn2.UiJsonToBpmn2Facade;
import com.iongroup.json2bpmn2.ConversionResult;
import com.iongroup.backend.runtime.FlowableRuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/flowable")
public class FlowableConversionController {

    private final FlowableRuntimeService runtimeService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(FlowableConversionController.class);

    public FlowableConversionController(FlowableRuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/convert-and-execute")
    public ResponseEntity<FlowableConversionResponse> convertAndExecute(
            @RequestBody Map<String, Object> uiJsonMap) {

        try {
            logger.info("Starting conversion process for UI JSON");

            // Convert incoming Map to JSON string
            String uiJsonStr = objectMapper.writeValueAsString(uiJsonMap);
            logger.debug("Received UI JSON (string length={}): {}", uiJsonStr.length(), uiJsonStr);

            // Use DBX library helper to convert UI JSON -> Flowable JSON -> BPMN
            JsonNode uiJson = objectMapper.readTree(uiJsonStr);
            UiJsonToBpmn2Facade.ConversionResult conv = UiJsonToBpmn2Facade.convert(uiJson);
            ObjectNode flowableJson = conv.getFlowableJson();
            String bpmnXml = conv.getBpmnXml();
            logger.debug("Conversion completed by library: BPMN length={}", bpmnXml.length());

            // Save BPMN XML to a file in backend_converted/
            String savedPath = null;
            try {
                Path outDir = Path.of("backend_converted");
                Files.createDirectories(outDir);
                String fname = "converted-" + System.currentTimeMillis() + ".bpmn20.xml";
                Path outPath = outDir.resolve(fname);
                Files.write(outPath, bpmnXml.getBytes(StandardCharsets.UTF_8));
                savedPath = outPath.toAbsolutePath().toString();
                logger.info("Saved converted BPMN to {}", savedPath);
            } catch (Exception writeErr) {
                logger.warn("Failed to persist BPMN file locally: {}", writeErr.getMessage());
            }

            String message = "Successfully converted UI JSON to BPMN."
                    + (savedPath != null ? " Saved to: " + savedPath : "");

            // Deploy the BPMN to Flowable
            String resourceName = "converted-" + System.currentTimeMillis() + ".bpmn20.xml";
            runtimeService.deployBpmn(resourceName, new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));
            logger.info("Deployed BPMN to Flowable");

            // Extract process key from flowableJson
            JsonNode processNode = flowableJson.get("process");
            if (processNode == null || processNode.get("id") == null) {
                logger.error("flowableJson does not contain process id");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new FlowableConversionResponse(false,
                                "flowableJson does not contain process id", null, null, null));
            }

            String processKey = processNode.get("id").asText();
            logger.info("Starting process with key: {}", processKey);

            // Start the process
            ProcessInstance processInstance = runtimeService.startProcess(processKey);
            logger.info("Started process instance: {}", processInstance.getId());

            // Create execution result
            Map<String, Object> executionResult = Map.of(
                    "processInstanceId", processInstance.getId(),
                    "processVariables", processInstance.getProcessVariables());

            FlowableConversionResponse response = new FlowableConversionResponse(
                    true,
                    message,
                    bpmnXml,
                    flowableJson,
                    executionResult);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error during BPMN conversion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FlowableConversionResponse(
                            false,
                            "Error: " + e.getMessage(),
                            null,
                            null,
                            null));
        }
    }

    // Process execution removed: controller only converts and saves BPMN files now.

    // Enrichment helper removed: DBX library produces enriched BPMN XML.
}
