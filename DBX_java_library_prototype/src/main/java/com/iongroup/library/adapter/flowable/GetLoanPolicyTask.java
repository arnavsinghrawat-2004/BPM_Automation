package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.LoanPricingPolicy;
import com.iongroup.library.registry.DelegationType;
import com.iongroup.library.registry.WorkFlowOperation;
import com.iongroup.library.service.LoanPolicyService;
import com.iongroup.library.service.impl.LoanPolicyServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Delegate for "Get Loan Policy / Pricing" step.
 * Determines which loan policies apply to this customer based on their profile.
 * 
 * Input: customerProfile
 * Output: loanPricingPolicy
 * 
 * Flowable Node Name: GetLoanPolicy
 */
@WorkFlowOperation(
    id = "GetLoanPolicy",
    description = "Fetch loan policy from external system",
    category = "loan",
    type = DelegationType.SERVICE,
    inputs = {"loanType"},
    outputs = {"loanPolicy"},
    selectableFields = {},
    customizableFields = {}
)
public class GetLoanPolicyTask implements JavaDelegate {

    private LoanPolicyService loanPolicyService;

    public GetLoanPolicyTask() {
        // In a real application, inject via Spring
        this.loanPolicyService = new LoanPolicyServiceImpl();
    }

    public GetLoanPolicyTask(LoanPolicyService loanPolicyService) {
        this.loanPolicyService = loanPolicyService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // Get customer profile from process variables
            CustomerProfile customerProfile = (CustomerProfile) execution.getVariable("customerProfile");
            
            if (customerProfile == null) {
                throw new RuntimeException("customerProfile is required to get loan policy");
            }

            // Get applicable loan policy for the customer
            LoanPricingPolicy loanPricingPolicy = loanPolicyService.getLoanPolicy(customerProfile);
            
            if (loanPricingPolicy == null) {
                throw new RuntimeException("No loan policy found for customer: " + customerProfile.getCustomerId());
            }

            // Store loan pricing policy for downstream tasks
            execution.setVariable("loanPricingPolicy", loanPricingPolicy);
            
            System.out.println("[GetLoanPolicyTask] Loan policy retrieved");
            System.out.println("  - Customer ID: " + customerProfile.getCustomerId());
            System.out.println("  - Max Principal Cap: " + loanPricingPolicy.getMaxPrincipalCap());
            System.out.println("  - Income Multiplier: " + loanPricingPolicy.getIncomeMultiplier());
            System.out.println("  - Max EMI to Income Ratio: " + loanPricingPolicy.getMaxEmiToIncomeRatio());
            System.out.println("  - Allowed Tenures: " + loanPricingPolicy.getAllowedTenures());

        } catch (Exception e) {
            System.err.println("[GetLoanPolicyTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to get loan policy: " + e.getMessage(), e);
        }
    }
}
