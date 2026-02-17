package com.iongroup.library.service.impl;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.InterestSlab;
import com.iongroup.library.domain.LoanPricingPolicy;
import com.iongroup.library.service.LoanPolicyService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of LoanPolicyService.
 * Returns loan policies based on customer profile or a default policy.
 */
public class LoanPolicyServiceImpl implements LoanPolicyService {

    @Override
    public LoanPricingPolicy getLoanPolicy(CustomerProfile customerProfile) {
        // Use default policy if customer profile is available
        LoanPricingPolicy policy = getDefaultPolicy();
        
        if (customerProfile != null && customerProfile.getMonthlyIncome() != null) {
            // Adjust policy based on customer income
            BigDecimal income = customerProfile.getMonthlyIncome();
            if (income.compareTo(BigDecimal.valueOf(100000)) >= 0) {
                // Premium policy for high-income customers
                policy.setMaxPrincipalCap(BigDecimal.valueOf(1000000));
                policy.setIncomeMultiplier(BigDecimal.valueOf(20));
            } else if (income.compareTo(BigDecimal.valueOf(50000)) < 0) {
                // Basic policy for low-income customers
                policy.setMaxPrincipalCap(BigDecimal.valueOf(200000));
                policy.setIncomeMultiplier(BigDecimal.valueOf(10));
            }
        }
        
        return policy;
    }

    @Override
    public LoanPricingPolicy getDefaultPolicy() {
        LoanPricingPolicy policy = new LoanPricingPolicy();
        
        // Default constraints
        policy.setMaxEmiToIncomeRatio(BigDecimal.valueOf(0.4));     // EMI should not exceed 40% of income
        policy.setIncomeMultiplier(BigDecimal.valueOf(15));          // Max principal = income * 15
        policy.setMaxPrincipalCap(BigDecimal.valueOf(500000));       // Absolute cap of 500,000

        // Allowed tenures in months
        List<Integer> tenures = new ArrayList<>();
        tenures.add(12);  // 1 year
        tenures.add(24);  // 2 years
        tenures.add(36);  // 3 years
        tenures.add(48);  // 4 years
        tenures.add(60);  // 5 years
        policy.setAllowedTenures(tenures);

        // Interest rate slabs
        List<InterestSlab> slabs = new ArrayList<>();
        slabs.add(new InterestSlab(BigDecimal.ZERO, BigDecimal.valueOf(100000), BigDecimal.valueOf(9.0)));
        slabs.add(new InterestSlab(BigDecimal.valueOf(100001), BigDecimal.valueOf(300000), BigDecimal.valueOf(8.5)));
        slabs.add(new InterestSlab(BigDecimal.valueOf(300001), BigDecimal.valueOf(500000), BigDecimal.valueOf(8.0)));
        slabs.add(new InterestSlab(BigDecimal.valueOf(500001), null, BigDecimal.valueOf(7.5)));
        policy.setInterestSlabs(slabs);

        return policy;
    }
}
