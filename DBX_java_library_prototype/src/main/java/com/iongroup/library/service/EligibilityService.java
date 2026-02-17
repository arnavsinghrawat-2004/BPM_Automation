package com.iongroup.library.service;

import java.math.BigDecimal;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.EligibilityResult;

public interface EligibilityService {

    /**
     * Checks eligibility for a customer profile.
     * Returns an EligibilityResult containing approval status,
     * approved amount, and optional reason code.
     */
    EligibilityResult checkEligibility(CustomerProfile customerProfile, BigDecimal requestedAmountLimit);

}