package com.iongroup.library.flowable_service;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.task.api.Task;

import com.iongroup.library.domain.CustomerProfile;

import java.util.List;
import java.util.Map;

public class FlowableProcessService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    public FlowableProcessService(RuntimeService runtimeService,
            TaskService taskService,
            HistoryService historyService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
    }

    public List<String> getActiveNodes(String processInstanceId) {
        return runtimeService.createExecutionQuery()
                .processInstanceId(processInstanceId)
                .list()
                .stream()
                .map(Execution::getActivityId)
                .filter(id -> id != null)
                .toList();
    }

    public List<String> getCompletedNodes(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .list()
                .stream()
                .map(HistoricActivityInstance::getActivityId)
                .toList();
    }

    public List<Map<String, String>> getPendingUserTasks(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list()
                .stream()
                .map(task -> Map.of(
                        "nodeId", task.getTaskDefinitionKey(),
                        "taskName", task.getName(),
                        "taskId", task.getId()))
                .toList();
    }

    public void completeUserTask(String processInstanceId, String nodeId, Map<String, Object> variables) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(nodeId)
                .singleResult();

        if (task == null) {
            throw new IllegalStateException("Task not found for nodeId: " + nodeId
                    + " in process: " + processInstanceId);
        }

        // If the variables look like customer fields, build a CustomerProfile object
        // too
        if (variables.containsKey("CUSTOMER_NAME") || variables.containsKey("customerName")) {
            CustomerProfile profile = new CustomerProfile();
            profile.setCustomerName(getField(variables, "CUSTOMER_NAME", "customerName"));
            profile.setContactNumber(getField(variables, "CONTACT_NUMBER", "contactNumber"));
            profile.setPanNumber(getField(variables, "PAN", "panNumber"));
            profile.setAadharNumber(getField(variables, "AADHAR", "aadharNumber"));
            profile.setCustomerAddress(getField(variables, "ADDRESS", "customerAddress"));
            String income = getField(variables, "MONTHLY_INCOME", "monthlyIncome");
            if (income != null)
                profile.setMonthlyIncome(new java.math.BigDecimal(income));
            variables.put("customerProfile", profile);
        }

        taskService.complete(task.getId(), variables);
    }

    private String getField(Map<String, Object> vars, String... keys) {
        for (String key : keys) {
            if (vars.containsKey(key) && vars.get(key) != null) {
                return vars.get(key).toString();
            }
        }
        return null;
    }

    public void terminateProcess(String processInstanceId, String reason) {
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }

    public void suspendProcess(String processInstanceId) {
        runtimeService.suspendProcessInstanceById(processInstanceId);
    }

    public void resumeProcess(String processInstanceId) {
        runtimeService.activateProcessInstanceById(processInstanceId);
    }
}