package com.iongroup.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iongroup.backend.model.FlowableConversionResponse;
import com.iongroup.library.flow.UiJsonToBpmnConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for converting UI flow graphs to BPMN and executing them
 */
@RestController
@RequestMapping("/api/flowable")
public class FlowableConversionController {

    private static final Logger logger = LoggerFactory.getLogger(FlowableConversionController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Endpoint to convert UI JSON graph to BPMN and execute it
     * POST /api/flowable/convert-and-execute
     * Request body: UI graph JSON
     */
    @PostMapping("/convert-and-execute")
    public ResponseEntity<FlowableConversionResponse> convertAndExecute(
            @RequestBody Map<String, Object> uiJsonMap) {

        try {
            logger.info("Starting conversion process for UI JSON");

            // Convert incoming Map to JSON string
            String uiJsonStr = objectMapper.writeValueAsString(uiJsonMap);
            logger.debug("Received UI JSON (string length={}): {}", uiJsonStr.length(), uiJsonStr);

            // Use DBX library helper to convert UI JSON -> Flowable JSON -> BPMN
            UiJsonToBpmnConverter.ConversionResult conv = UiJsonToBpmnConverter.convert(uiJsonStr, null);
            ObjectNode flowableJson = conv.flowableJson;
            byte[] bpmnXml = conv.bpmnXml;
            logger.debug("Conversion completed by library: BPMN bytes={}", bpmnXml.length);

            // Save BPMN XML to a file in backend_converted/
            String savedPath = null;
            try {
                Path outDir = Path.of("backend_converted");
                Files.createDirectories(outDir);
                String fname = "converted-" + System.currentTimeMillis() + ".bpmn20.xml";
                Path outPath = outDir.resolve(fname);
                Files.write(outPath, bpmnXml);
                savedPath = outPath.toAbsolutePath().toString();
                logger.info("Saved converted BPMN to {}", savedPath);
            } catch (Exception writeErr) {
                logger.warn("Failed to persist BPMN file locally: {}", writeErr.getMessage());
            }

            String message = "Successfully converted UI JSON to BPMN." + (savedPath != null ? " Saved to: " + savedPath : "");

            FlowableConversionResponse response = new FlowableConversionResponse(
                    true,
                    message,
                    new String(bpmnXml, StandardCharsets.UTF_8),
                    flowableJson,
                    null
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error during BPMN conversion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new FlowableConversionResponse(
                            false,
                            "Error: " + e.getMessage(),
                            null,
                            null,
                            null
                    ));
        }
    }

    // Process execution removed: controller only converts and saves BPMN files now.

    // Enrichment helper removed: DBX library produces enriched BPMN XML.
}
