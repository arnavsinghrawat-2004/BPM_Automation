package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.RequiredCustomerDetails;
import com.iongroup.library.service.CustomerDetailsCollectionService;
import com.iongroup.library.service.impl.CustomerCollectionServiceImpl;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Collects customer details dynamically based on BPMN configuration.
 *
 * BPMN extension element:
 *  <flowable:requiredFields>CUSTOMER_NAME,PAN,MONTHLY_INCOME</flowable:requiredFields>
 */
public class EnterCustomerDetailsTask implements JavaDelegate {

    private final CustomerDetailsCollectionService collectionService;

    public EnterCustomerDetailsTask() {
        // POC-friendly instantiation
        this.collectionService = new CustomerCollectionServiceImpl();
    }

    public EnterCustomerDetailsTask(CustomerDetailsCollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @Override
    public void execute(DelegateExecution execution) {

        // 1. Get or create customer profile
        CustomerProfile profile =
                (CustomerProfile) execution.getVariable("customerProfile");

        if (profile == null) {
            profile = new CustomerProfile();

            // Optional but strongly recommended
            Object customerId = execution.getVariable("customerId");
            if (customerId != null) {
                profile.setCustomerId(customerId.toString());
            }

            execution.setVariable("customerProfile", profile);

            System.out.println("[EnterCustomerDetailsTask] Created new CustomerProfile");
        }

        // 2. Read BPMN extension element
        String requiredFieldsRaw = extractRequiredFields(execution);

        RequiredCustomerDetails required = new RequiredCustomerDetails();

        // 3. Parse required fields
        if (requiredFieldsRaw != null && !requiredFieldsRaw.isBlank()) {
            Set<String> fields = Arrays.stream(requiredFieldsRaw.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());

            for (String field : fields) {
                switch (field) {
                    case "CUSTOMER_NAME" -> required.setCustomerName(true);
                    case "CONTACT_NUMBER" -> required.setContactNumber(true);
                    case "ADDRESS" -> required.setCustomerAddress(true);
                    case "PAN" -> required.setPanNumber(true);
                    case "AADHAR" -> required.setAadharNumber(true);
                    case "MONTHLY_INCOME" -> required.setMonthlyIncome(true);
                    default ->
                            System.out.println(
                                    "[EnterCustomerDetailsTask] Unknown required field: " + field
                            );
                }
            }
        }

        // 4. Collect only required details
        collectionService.collectRequiredDetails(profile, required);

        // 5. Persist back to Flowable context
        execution.setVariable("customerProfile", profile);

        System.out.println("[EnterCustomerDetailsTask] Customer details collected successfully");
    }


    /**
     * Safely extracts the requiredFields extension element from BPMN.
     */
    private String extractRequiredFields(DelegateExecution execution) {

        Map<String, List<ExtensionElement>> extensionElements =
                execution.getCurrentFlowElement().getExtensionElements();

        if (extensionElements == null) {
            return null;
        }

        List<ExtensionElement> elements = extensionElements.get("requiredFields");

        if (elements == null || elements.isEmpty()) {
            return null;
        }

        return elements.get(0).getElementText();
    }
}
