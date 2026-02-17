package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.LoanOffers;
import com.iongroup.library.domain.LoanPricingPolicy;
import com.iongroup.library.service.LoanOfferService;
import com.iongroup.library.service.impl.LoanOfferServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Delegate for "Create Loan Offer" step.
 * Calculates loan terms (interest, tenure, EMI, etc.) and creates the offer.
 * 
 * Inputs: customerProfile, loanPricingPolicy
 * Output: loanOffer
 * 
 * Flowable Node Name: CreateLoanOffer
 */
public class CreateLoanOfferTask implements JavaDelegate {

    private LoanOfferService loanOfferService;

    public CreateLoanOfferTask() {
        // In a real application, inject via Spring
        this.loanOfferService = new LoanOfferServiceImpl();
    }

    public CreateLoanOfferTask(LoanOfferService loanOfferService) {
        this.loanOfferService = loanOfferService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // Get customer profile and loan pricing policy from process variables
            CustomerProfile customerProfile = (CustomerProfile) execution.getVariable("customerProfile");
            LoanPricingPolicy loanPricingPolicy = (LoanPricingPolicy) execution.getVariable("loanPricingPolicy");
            
            if (customerProfile == null) {
                throw new RuntimeException("customerProfile is required to create loan offer");
            }
            
            if (loanPricingPolicy == null) {
                throw new RuntimeException("loanPricingPolicy is required to create loan offer");
            }

            // Create loan offer using the service
            LoanOffers loanOffer = loanOfferService.createOffer(customerProfile, loanPricingPolicy);
            
            if (loanOffer == null) {
                throw new RuntimeException("Failed to create loan offer");
            }

            // Store loan offer for downstream tasks
            execution.setVariable("loanOffer", loanOffer);
            
            System.out.println("[CreateLoanOfferTask] Loan offer created successfully");
            System.out.println("  - Offer ID: " + loanOffer.getOfferId());
            System.out.println("  - Customer ID: " + loanOffer.getCustomerId());
            System.out.println("  - Principal Amount: " + loanOffer.getPrincipalAmount());
            System.out.println("  - Interest Rate: " + loanOffer.getInterestRate() + "%");
            System.out.println("  - Tenure (Months): " + loanOffer.getTenureMonths());
            System.out.println("  - EMI: " + loanOffer.getEmiAmount());
            System.out.println("  - Status: " + loanOffer.getStatus());

        } catch (Exception e) {
            System.err.println("[CreateLoanOfferTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to create loan offer: " + e.getMessage(), e);
        }
    }
}
