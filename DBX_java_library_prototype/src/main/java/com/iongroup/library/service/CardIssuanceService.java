package com.iongroup.library.service;

import com.iongroup.library.domain.Card;
import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.EligibilityResult;

public interface CardIssuanceService {

    /**
     * Issues a new card for the given customer based on the eligibility result.
     * Returns a fully populated Card object if approved.
     * If eligibility is not approved, returns null.
     */
    Card issueCard(CustomerProfile customerProfile, EligibilityResult eligibilityResult);
}