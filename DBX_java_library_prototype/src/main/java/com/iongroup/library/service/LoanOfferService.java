package com.iongroup.library.service;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.LoanOffers;
import com.iongroup.library.domain.LoanPricingPolicy;

public interface LoanOfferService {

    LoanOffers createOffer(
            CustomerProfile profile,
            LoanPricingPolicy policy
    );
}
