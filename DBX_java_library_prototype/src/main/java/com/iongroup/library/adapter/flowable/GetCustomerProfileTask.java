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
@WorkFlowOperation(id = "GetCustomerProfile", description = "Get customer profile", category = "common", type = DelegationType.SERVICE, inputs = {
        "customerId", "notificationType",
        "content" }, outputs = { "notificationStatus" }, selectableFields = {}, customizableFields = {})
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
            // Check if customerProfile was already set by a user task
            CustomerProfile existingProfile = (CustomerProfile) execution.getVariable("customerProfile");

            if (existingProfile != null) {
                System.out.println("[GetCustomerProfileTask] Customer profile fetched from service");
                System.out.println("  - Customer ID: " + existingProfile.getCustomerId());
                System.out.println("  - Customer Name: " + existingProfile.getCustomerName());
                System.out.println("  - Contact Number: " + existingProfile.getContactNumber());
                System.out.println("  - PAN Number: " + existingProfile.getPanNumber());
                System.out.println("  - Aadhar Number: " + existingProfile.getAadharNumber());
                System.out.println("  - Address: " + existingProfile.getCustomerAddress());
                System.out.println("  - Monthly Income: " + existingProfile.getMonthlyIncome());
                System.out.println("  - Account Balance: " + existingProfile.getAccountBalance());
                return;
            }

            // No profile yet — fetch from service
            String customerId = (String) execution.getVariable("customerId");

            if (customerId == null || customerId.isBlank()) {
                throw new RuntimeException("customerId is required to fetch customer profile");
            }

            CustomerProfile customerProfile = customerProfileService.getCustomerProfile(customerId);

            if (customerProfile == null) {
                throw new RuntimeException("Customer profile not found for customerId: " + customerId);
            }

            execution.setVariable("customerProfile", customerProfile);

            System.out.println("[GetCustomerProfileTask] Customer profile fetched from service");
            System.out.println("  - Customer Name: " + customerProfile.getCustomerName());
            System.out.println("  - Monthly Income: " + customerProfile.getMonthlyIncome());

        } catch (Exception e) {
            System.err.println("[GetCustomerProfileTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to get customer profile: " + e.getMessage(), e);
        }
    }
}
