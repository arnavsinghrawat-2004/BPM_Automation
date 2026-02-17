package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.LoanOffers;
import com.iongroup.library.service.ApprovalService;
import com.iongroup.library.service.impl.ApprovalServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Delegate for "Customer Approval / Signature" step (optional).
 * Handles customer approval or digital signature of the loan offer.
 * This could be manual approval, automated approval, or digital signature flow.
 * 
 * Input: loanOffer
 * Output: approvalStatus (true/false)
 * 
 * Flowable Node Name: CustomerApproval
 */
public class CustomerApprovalTask implements JavaDelegate {

    private ApprovalService approvalService;

    public CustomerApprovalTask() {
        // In a real application, inject via Spring
        this.approvalService = new ApprovalServiceImpl();
    }

    public CustomerApprovalTask(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // Get loan offer from process variables
            LoanOffers loanOffer = (LoanOffers) execution.getVariable("loanOffer");
            
            if (loanOffer == null) {
                throw new RuntimeException("loanOffer is required for approval");
            }

            // Approve the loan offer
            // In production, this could involve:
            // - Waiting for customer's manual approval via a user task
            // - Digital signature verification
            // - OTP verification
            // - Automatic approval based on rules
            boolean approvalStatus = approvalService.approveOffer(loanOffer);
            
            // Store approval status for downstream tasks
            execution.setVariable("approvalStatus", approvalStatus);
            
            System.out.println("[CustomerApprovalTask] Loan offer approval processed");
            System.out.println("  - Offer ID: " + loanOffer.getOfferId());
            System.out.println("  - Customer ID: " + loanOffer.getCustomerId());
            System.out.println("  - Approval Status: " + (approvalStatus ? "APPROVED" : "REJECTED"));
            System.out.println("  - Current Offer Status: " + loanOffer.getStatus());

        } catch (Exception e) {
            System.err.println("[CustomerApprovalTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to process approval: " + e.getMessage(), e);
        }
    }
}
