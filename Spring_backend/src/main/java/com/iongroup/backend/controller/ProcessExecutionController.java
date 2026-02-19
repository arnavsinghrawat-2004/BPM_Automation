package com.iongroup.backend.controller;
 
import com.iongroup.backend.runtime.FlowableRuntimeService;
import com.iongroup.json2bpmn2.UiJsonToBpmn2Facade;
import com.iongroup.json2bpmn2.UiJsonToBpmn2Facade.ConversionResult;
import com.iongroup.library.flowable_service.FlowableProcessService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/process")
public class ProcessExecutionController {

    private final FlowableRuntimeService flowableRuntimeService;
    private final FlowableProcessService flowableProcessService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProcessExecutionController(FlowableRuntimeService flowableRuntimeService,
                                      FlowableProcessService flowableProcessService) {
        this.flowableRuntimeService = flowableRuntimeService;
        this.flowableProcessService = flowableProcessService;
    }

    @PostMapping("/execute")
    public ResponseEntity<?> execute(@RequestBody String json) throws Exception {
        JsonNode uiJson = objectMapper.readTree(json);
        ConversionResult result = UiJsonToBpmn2Facade.convert(uiJson);
        String bpmnXml = result.getBpmnXml();

        flowableRuntimeService.deployBpmn(
                "dynamic-process.bpmn20.xml",
                new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8))
        );

        ProcessInstance instance = flowableRuntimeService.startProcess("ABC");
        List<Task> tasks = flowableRuntimeService.getActiveTasks();

        return ResponseEntity.ok(Map.of(
                "processInstanceId", instance.getId(),
                "tasks", tasks.stream().map(Task::getName).toList()
        ));
    }

    @PostMapping("/task/complete/{nodeId}")
    public ResponseEntity<?> completeUserTask(
            @PathVariable String nodeId,
            @RequestParam String processInstanceId,
            @RequestBody Map<String, Object> body) {

        flowableProcessService.completeUserTask(processInstanceId, nodeId, body);
        return ResponseEntity.ok("done");
    }

    @GetMapping("/status/{processInstanceId}")
    public ResponseEntity<?> getProcessStatus(@PathVariable String processInstanceId) {
        return ResponseEntity.ok(Map.of(
                "activeNodes", flowableProcessService.getActiveNodes(processInstanceId),
                "completedNodes", flowableProcessService.getCompletedNodes(processInstanceId),
                "pendingUserTasks", flowableProcessService.getPendingUserTasks(processInstanceId)
        ));
    }
}
