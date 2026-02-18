package com.iongroup.backend.runtime;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlowableRuntimeService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public FlowableRuntimeService(
            RepositoryService repositoryService,
            RuntimeService runtimeService,
            TaskService taskService
    ) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    public void deployBpmn(String resourceName, InputStream bpmnStream) {
        repositoryService.createDeployment()
                .addInputStream(resourceName, bpmnStream)
                .deploy();
        repositoryService.createProcessDefinitionQuery()
                .list()
                .forEach(pd -> System.out.println("DEPLOYED DEF KEY=" + pd.getKey() + " ID=" + pd.getId()));

    }

    public ProcessInstance startProcess(String processKey) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("customerId", "CUST-1001");

        return runtimeService.startProcessInstanceByKey(processKey, variables);
    }

    public List<Task> getActiveTasks() {
        return taskService.createTaskQuery().list();
    }
}
