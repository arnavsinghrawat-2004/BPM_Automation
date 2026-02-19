// package com.iongroup.backend.controller;

// import com.iongroup.backend.runtime.FlowableRuntimeService;
// import org.flowable.engine.runtime.ProcessInstance;
// import org.flowable.task.api.Task;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/process")
// public class ProcessController {

//     private final FlowableRuntimeService runtimeService;

//     public ProcessController(FlowableRuntimeService runtimeService) {
//         this.runtimeService = runtimeService;
//     }

//     @PostMapping("/start/{key}")
//     public String start(@PathVariable String key) {
//         ProcessInstance pi = runtimeService.startProcess(key);
//         return "Started process: " + pi.getId();
//     }

//     @GetMapping("/tasks")
//     public List<Task> tasks() {
//         return runtimeService.getActiveTasks();
//     }
// }
