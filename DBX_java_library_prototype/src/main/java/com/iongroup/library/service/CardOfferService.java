package com.iongroup.library.service;

import com.iongroup.library.domain.CreditCardOffer;
import com.iongroup.library.domain.CardPricingPolicy;
import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.EligibilityResult;

import java.util.List;

public interface CardOfferService {

    /**
     * Returns a list of available card offers for the customer given eligibility.
     */
    List<CreditCardOffer> getAvailableOffers(CustomerProfile profile, EligibilityResult eligibilityResult);

    /**
     * Create a concrete credit card offer using pricing policy.
     */
    CreditCardOffer createOffer(CustomerProfile profile, CardPricingPolicy pricingPolicy);
}
