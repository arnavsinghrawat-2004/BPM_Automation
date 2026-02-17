package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.EligibilityResult;
import com.iongroup.library.service.EligibilityService;
import com.iongroup.library.service.impl.EligibilityServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.bpmn.model.ExtensionElement;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Delegate for "Check Eligibility / Risk Assessment" step (optional but common).
 * Performs quick check on basic eligibility (age, credit score, KYC, etc.).
 * 
 * Input: customerProfile
 * Output: eligibilityStatus (true/false), eligibilityResult
 * 
 * Flowable Node Name: CheckEligibility
 */
public class CheckEligibilityTask implements JavaDelegate {

    private EligibilityService eligibilityService;
    
    private BigDecimal extractRequestedAmountLimit(DelegateExecution execution) {

        Map<String, List<ExtensionElement>> extensionElements =
                execution.getCurrentFlowElement().getExtensionElements();

        if (extensionElements == null) {
            return null;
        }

        List<ExtensionElement> elements =
                extensionElements.get("requestedAmountLimit");

        if (elements == null || elements.isEmpty()) {
            return null;
        }

        return new BigDecimal(elements.get(0).getElementText());
    }

    public CheckEligibilityTask() {
        // In a real application, inject via Spring
        this.eligibilityService = new EligibilityServiceImpl();
    }

    public CheckEligibilityTask(EligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // Get customer profile from process variables
            CustomerProfile customerProfile = (CustomerProfile) execution.getVariable("customerProfile");
            
            if (customerProfile == null) {
                throw new RuntimeException("customerProfile is required for eligibility check");
            }

            // Check eligibility
            BigDecimal requestedLimit = extractRequestedAmountLimit(execution);

            if (requestedLimit == null) {
                throw new RuntimeException(
                    "requestedAmountLimit not configured in BPMN for CheckEligibility task"
                );
            }

            EligibilityResult eligibilityResult =
                    eligibilityService.checkEligibility(customerProfile, requestedLimit);

                        
            if (eligibilityResult == null) {
                throw new RuntimeException("Eligibility check failed");
            }

            // Store eligibility result for downstream decisions
            execution.setVariable("eligibilityResult", eligibilityResult);
            execution.setVariable("eligibilityStatus", eligibilityResult.isApproved());
            
            System.out.println("[CheckEligibilityTask] Eligibility check completed");
            System.out.println("  - Customer ID: " + customerProfile.getCustomerId());
            System.out.println("  - Approved: " + eligibilityResult.isApproved());
            System.out.println("  - Amount: " + eligibilityResult.getAmount());
            System.out.println("  - Reason: " + eligibilityResult.getReasonCode());

        } catch (Exception e) {
            System.err.println("[CheckEligibilityTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to check eligibility: " + e.getMessage(), e);
        }
    }
}
