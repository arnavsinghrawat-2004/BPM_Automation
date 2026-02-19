package com.iongroup.backend.runtime;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.engine.history.HistoricActivityInstance;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlowableRuntimeService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    public FlowableRuntimeService(
            RepositoryService repositoryService,
            RuntimeService runtimeService,
            TaskService taskService,
            HistoryService historyService
    ) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
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

    public Map<String, Object> getProcessStatus(String processInstanceId) {
        // Get active tasks for this process
        List<Map<String, Object>> activeTasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list()
                .stream()
                .map(task -> (Map<String, Object>) (Map<?, ?>) Map.of(
                    "id", task.getId(),
                    "name", task.getName(),
                    "nodeId", task.getTaskDefinitionKey()
                ))
                .collect(Collectors.toList());

        // Get completed activities
        List<String> completedActivities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .list()
                .stream()
                .map(HistoricActivityInstance::getActivityId)
                .collect(Collectors.toList());

        // Get current activity (if any)
        String currentActivity = null;
        List<HistoricActivityInstance> current = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .unfinished()
                .list();
        if (!current.isEmpty()) {
            currentActivity = current.get(0).getActivityId();
        }

        return Map.of(
            "activeTasks", activeTasks,
            "completedActivities", completedActivities,
            "currentActivity", currentActivity
        );
    }

    public void completeTask(String taskId) {
        taskService.complete(taskId);
    }
}
