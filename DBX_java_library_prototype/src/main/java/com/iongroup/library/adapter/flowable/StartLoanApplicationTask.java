package com.iongroup.library.adapter.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Delegate for "Start Loan Application" step.
 * Triggered when a customer submits a loan request.
 * 
 * Input: Application submission (from frontend)
 * Output: applicationId or customerId
 * 
 * Frontend Node Name: StartLoanApplication
 */
public class StartLoanApplicationTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // Extract customerId from the process variables
            // This would typically come from the process start request
            String customerId = (String) execution.getVariable("customerId");
            
            if (customerId == null || customerId.isBlank()) {
                throw new RuntimeException("customerId is required to start loan application");
            }

            // Generate application ID
            String applicationId = "APP-" + System.currentTimeMillis();
            
            // Store variables for downstream tasks
            execution.setVariable("applicationId", applicationId);
            execution.setVariable("loanApplyProcessStartTime", System.currentTimeMillis());
            
            System.out.println("[StartLoanApplicationTask] Application started");
            System.out.println("  - Application ID: " + applicationId);
            System.out.println("  - Customer ID: " + customerId);

        } catch (Exception e) {
            System.err.println("[StartLoanApplicationTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to start loan application: " + e.getMessage(), e);
        }
    }
}
