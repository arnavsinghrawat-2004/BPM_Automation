package com.iongroup.library.service;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.LoanPricingPolicy;

/**
 * Service for determining which loan policies apply to a customer.
 */
public interface LoanPolicyService {

    /**
     * Get the applicable loan pricing policy for a customer based on their profile.
     * 
     * @param customerProfile the customer's profile
     * @return the applicable LoanPricingPolicy
     */
    LoanPricingPolicy getLoanPolicy(CustomerProfile customerProfile);

    /**
     * Get a default loan policy.
     * 
     * @return the default LoanPricingPolicy
     */
    LoanPricingPolicy getDefaultPolicy();
}
