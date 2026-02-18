// package com.iongroup.backend.controller;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.iongroup.backend.model.FlowableConversionResponse;
// import com.iongroup.json2bpmn2.UiJsonToBpmn2Facade;
// import com.iongroup.json2bpmn2.ConversionResult;
// import org.springframework.http.MediaType;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/convert")
// public class FlowableConversionController {

//     @PostMapping(
//         value = "/ui-to-bpmn",
//         consumes = MediaType.APPLICATION_JSON_VALUE,
//         produces = MediaType.APPLICATION_JSON_VALUE
//     )
//     public FlowableConversionResponse convertUiJsonToBpmn(
//             @RequestBody JsonNode uiJson) {

//         // 🔹 Single call into jsontobpmn2 (black box)
//         ConversionResult result = UiJsonToBpmn2Facade.convert(uiJson);

//         // 🔹 Just return what the library produced
//         return new FlowableConversionResponse(
//                 result.getFlowableJson(),
//                 result.getBpmnXml()
//         );
//     }
// }
