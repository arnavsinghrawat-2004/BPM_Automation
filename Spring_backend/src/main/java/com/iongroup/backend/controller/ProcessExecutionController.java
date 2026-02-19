package com.iongroup.backend.controller;
 
import com.iongroup.backend.runtime.FlowableRuntimeService;
import com.iongroup.json2bpmn2.UiJsonToBpmn2Facade;
import com.iongroup.json2bpmn2.UiJsonToBpmn2Facade.ConversionResult;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final org.flowable.engine.TaskService taskService;
 
    public ProcessExecutionController(FlowableRuntimeService flowableRuntimeService, org.flowable.engine.TaskService taskService) {
        this.flowableRuntimeService = flowableRuntimeService;
        this.taskService = taskService;
    }
 
    @PostMapping("/execute")
    public ResponseEntity<?> execute(@RequestBody String json) throws Exception {
 
        // 1) Parse UI JSON
        JsonNode uiJson = objectMapper.readTree(json);
 
        // 2) Convert: UI JSON → Flowable JSON → BPMN Model → BPMN 2.0 XML
        ConversionResult result = UiJsonToBpmn2Facade.convert(uiJson);
        String bpmnXml = result.getBpmnXml();
 
        // 3) Deploy BPMN XML
        flowableRuntimeService.deployBpmn(
                "dynamic-process.bpmn20.xml",
                new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8))
        );
 
        // 4) Start process - MUST match the process ID defined in JsonToBpmn2Converter
        //    JsonToBpmn2Converter.PROCESS_ID = "ComplexProcess"
        ProcessInstance instance = flowableRuntimeService.startProcess("ABC");
 
        // 5) Fetch active tasks
        List<Task> tasks = flowableRuntimeService.getActiveTasks();
 
        return ResponseEntity.ok(
                Map.of(
                        "processInstanceId", instance.getId(),
                        "tasks", tasks.stream().map(Task::getName).toList()
                )
        );
    }
     @PostMapping("/task/complete/{nodeId}")
    public ResponseEntity<?> completeUserTask(
            @PathVariable String nodeId,
            @RequestParam String processInstanceId,
            @RequestBody Map<String, Object> body) {
 
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(nodeId)
                .singleResult();
 
        if (task == null) {
            return ResponseEntity.status(404).body("Task not found or already completed");
        }
 
        taskService.complete(task.getId(), body);
 
        return ResponseEntity.ok("done");
    }
}