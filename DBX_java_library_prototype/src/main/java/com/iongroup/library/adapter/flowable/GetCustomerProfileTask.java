package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.registry.DelegationType;
import com.iongroup.library.registry.WorkFlowOperation;
import com.iongroup.library.service.CustomerProfileService;
import com.iongroup.library.service.impl.CustomerProfileServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Delegate for "Get Customer Profile" step.
 * Fetches customer details from database or CRM.
 * 
 * Input: customerId
 * Output: customerProfile
 * 
 * Flowable Node Name: GetCustomerProfile
 */
@WorkFlowOperation(
    id = "GetCustomerProfile",
    description = "Get customer profile",
    category = "common",
    type = DelegationType.SERVICE,
    inputs = {"customerId", "notificationType", "content"},
    outputs = {"notificationStatus"},
    selectableFields = {},
    customizableFields = {}
)
public class GetCustomerProfileTask implements JavaDelegate {

    private CustomerProfileService customerProfileService;

    public GetCustomerProfileTask() {
        // In a real application, inject via Spring
        this.customerProfileService = new CustomerProfileServiceImpl();
    }

    public GetCustomerProfileTask(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // Get customerId from process variables
            String customerId = (String) execution.getVariable("customerId");
            
            if (customerId == null || customerId.isBlank()) {
                throw new RuntimeException("customerId is required to fetch customer profile");
            }

            // Fetch customer profile
            CustomerProfile customerProfile = customerProfileService.getCustomerProfile(customerId);
            
            if (customerProfile == null) {
                throw new RuntimeException("Customer profile not found for customerId: " + customerId);
            }

            // Store customer profile for downstream tasks
            execution.setVariable("customerProfile", customerProfile);
            
            System.out.println("[GetCustomerProfileTask] Customer profile retrieved");
            System.out.println("  - Customer ID: " + customerProfile.getCustomerId());
            System.out.println("  - Customer Name: " + customerProfile.getCustomerName());
            System.out.println("  - Monthly Income: " + customerProfile.getMonthlyIncome());
            System.out.println("  - Account Balance: " + customerProfile.getAccountBalance());

        } catch (Exception e) {
            System.err.println("[GetCustomerProfileTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to get customer profile: " + e.getMessage(), e);
        }
    }
}
