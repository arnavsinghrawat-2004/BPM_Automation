package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.LoanOffers;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Delegate for "End / Application Closed" step.
 * Marks process completion and optionally stores the final loan offer or approval status in database.
 * 
 * Inputs: loanOffer, approvalStatus (optional), notificationStatus (optional)
 * Output: Process completion with final status
 * 
 * Flowable Node Name: EndLoanApplication
 */
public class EndLoanApplicationTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // Retrieve all key variables from process execution
            String applicationId = (String) execution.getVariable("applicationId");
            String customerId = (String) execution.getVariable("customerId");
            Long processStartTime = (Long) execution.getVariable("loanApplyProcessStartTime");
            CustomerProfile customerProfile = (CustomerProfile) execution.getVariable("customerProfile");
            LoanOffers loanOffer = (LoanOffers) execution.getVariable("loanOffer");
            Boolean approvalStatus = (Boolean) execution.getVariable("approvalStatus");
            Boolean notificationStatus = (Boolean) execution.getVariable("notificationStatus");

            // Calculate process duration
            long processDurationMs = (processStartTime != null) ? 
                (System.currentTimeMillis() - processStartTime) : 0;

            // In a real application, persist the loan offer and related data to database
            if (loanOffer != null) {
                persistLoanOfferData(loanOffer, approvalStatus);
            }

            // Log completion
            System.out.println("[EndLoanApplicationTask] Loan application process completed");
            System.out.println("  - Application ID: " + applicationId);
            System.out.println("  - Customer ID: " + customerId);
            System.out.println("  - Customer Name: " + 
                (customerProfile != null ? customerProfile.getCustomerName() : "Unknown"));
            
            if (loanOffer != null) {
                System.out.println("  - Offer ID: " + loanOffer.getOfferId());
                System.out.println("  - Principal Amount: " + loanOffer.getPrincipalAmount());
                System.out.println("  - Offer Status: " + loanOffer.getStatus());
            }
            
            if (approvalStatus != null) {
                System.out.println("  - Approval Status: " + (approvalStatus ? "APPROVED" : "REJECTED"));
            }
            
            if (notificationStatus != null) {
                System.out.println("  - Notification Status: " + (notificationStatus ? "SENT" : "FAILED"));
            }
            
            System.out.println("  - Process Duration: " + processDurationMs + " ms");
            System.out.println("[EndLoanApplicationTask] Process Status: SUCCESS");

        } catch (Exception e) {
            System.err.println("[EndLoanApplicationTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to complete loan application: " + e.getMessage(), e);
        }
    }

    /**
     * Persist loan offer and related data to database.
     * In production, this would involve database operations.
     */
    private void persistLoanOfferData(LoanOffers loanOffer, Boolean approvalStatus) {
        // In production environment:
        // - Save loanOffer to database
        // - Create loan account if approved
        // - Update customer records
        // - Create audit logs
        
        System.out.println("[EndLoanApplicationTask] Persisting data to database...");
        System.out.println("  - Saving Offer ID: " + loanOffer.getOfferId());
        System.out.println("  - Status: " + loanOffer.getStatus());
        
        if (approvalStatus != null && approvalStatus) {
            System.out.println("  - Creating loan account for approved offer");
        }
    }
}
