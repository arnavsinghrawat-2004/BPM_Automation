package com.iongroup.library.adapter.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import com.iongroup.library.registry.DelegationType;
import com.iongroup.library.registry.WorkFlowOperation;

/**
 * Delegate for "Start Card Application" step.
 * Input: customerId
 * Output: applicationId
 * Flowable Node Name: StartApplication
 */
@WorkFlowOperation(
    id = "StartCardApplication",
    description = "Initialize credit card application workflow",
    category = "card",
    type = DelegationType.SERVICE,
    inputs = {"customerId"},
    outputs = {"applicationId", "customerProfile"},
    selectableFields = {},
    customizableFields = {}
)
public class StartCardApplicationTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        try {
            String customerId = (String) execution.getVariable("customerId");

            if (customerId == null || customerId.isBlank()) {
                throw new RuntimeException("customerId is required to start card application");
            }

            String applicationId = "CCA-" + System.currentTimeMillis();

            execution.setVariable("applicationId", applicationId);
            execution.setVariable("cardApplyProcessStartTime", System.currentTimeMillis());

            System.out.println("[StartCardApplicationTask] Card application started");
            System.out.println("  - Application ID: " + applicationId);
            System.out.println("  - Customer ID: " + customerId);

        } catch (Exception e) {
            System.err.println("[StartCardApplicationTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to start card application: " + e.getMessage(), e);
        }
    }
}
