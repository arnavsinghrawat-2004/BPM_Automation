package com.iongroup.library.runtime;

import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.Map;

public class FlowableRunner {

    public static void main(String[] args) {

        ProcessEngineConfiguration cfg =
            ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setAsyncExecutorActivate(false);

        ProcessEngine processEngine = cfg.buildProcessEngine();

        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();

        // 1️⃣ Deploy BPMN
        repositoryService.createDeployment()
            .addClasspathResource("processes/newTrial.bpmn20.xml")
            .deploy();

        System.out.println("✅ BPMN deployed");

        // 2️⃣ Start process
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerId", "CUST-1001");

        ProcessInstance pi =
            runtimeService.startProcessInstanceByKey("ComplexProcess", variables);

        System.out.println("🚀 Process started: " + pi.getId());

        // 3️⃣ Inspect waiting user tasks
        for (Task task : taskService.createTaskQuery().list()) {
            System.out.println("🧍 Waiting at user task:");
            System.out.println("  ID   : " + task.getTaskDefinitionKey());
            System.out.println("  Name : " + task.getName());
        }
    }
}
