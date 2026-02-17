package com.example.trails;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;

import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.history.HistoricVariableInstanceQuery;

import com.example.trails.DemoService;

public class RunFlowableProcess {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: java RunFlowableProcess <path/to/model_service_task.bpmn20.xml>");
            System.exit(2);
        }
        Path bpmnPath = Path.of(args[0]);
        if (!Files.exists(bpmnPath)) {
            System.err.println("BPMN file not found: " + bpmnPath.toAbsolutePath());
            System.exit(2);
        }

        // 1) Provide beans so expressions like ${demoService.run(...)} resolve
        Map<Object, Object> beans = new HashMap<>();
        beans.put("demoService", new DemoService());

        // 2) Build in-memory engine with the beans registered
        // 2) Build in-memory engine with the beans registered
        ProcessEngineConfiguration cfg =
                ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();  // documented API

        cfg.setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=1000");
        cfg.setJdbcDriver("org.h2.Driver");
        cfg.setJdbcUsername("sa");
        cfg.setJdbcPassword("");
        cfg.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        cfg.setBeans(beans);
        // cfg.setAsyncExecutorActivate(false); // not needed for this demo

        ProcessEngine engine = cfg.buildProcessEngine();

        try {
            RepositoryService repo = engine.getRepositoryService();
            RuntimeService runtime = engine.getRuntimeService();
            HistoryService history = engine.getHistoryService();

            // 3) Deploy BPMN
            Deployment dep = repo.createDeployment()
                    .name("service-task-demo")
                    .addInputStream("model_service_task.bpmn20.xml", Files.newInputStream(bpmnPath))
                    .deploy();
            System.out.println("Deployed: " + dep.getId());

            // 4) Start process with an input variable consumed by the expression
            Map<String, Object> vars = new HashMap<>();
            vars.put("inputName", "Aryan");
            ProcessInstance pi = runtime.startProcessInstanceByKey("svcProcess", vars);
            System.out.println("Started instance: " + pi.getProcessInstanceId());

            // For a straight-through process (start->service->end), the instance ends immediately.
            // Fetch the service task's result variable from history.
            HistoricVariableInstanceQuery hvq = history.createHistoricVariableInstanceQuery()
                    .processInstanceId(pi.getProcessInstanceId())
                    .variableName("resultVar");
            HistoricVariableInstance hv = hvq.singleResult();

            System.out.println("resultVar = " + (hv == null ? "<not found>" : hv.getValue()));

            HistoricVariableInstance greeting = history.createHistoricVariableInstanceQuery()
                    .processInstanceId(pi.getProcessInstanceId())
                    .variableName("greeting")
                    .singleResult();
            System.out.println("greeting = " + (greeting == null ? "<not found>" : greeting.getValue()));

        } finally {
            engine.close();
        }
    }
}