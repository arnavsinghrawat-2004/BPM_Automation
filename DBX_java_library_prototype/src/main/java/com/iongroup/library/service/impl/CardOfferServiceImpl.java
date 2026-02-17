package com.iongroup.library.service.impl;

import com.iongroup.library.domain.*;
import com.iongroup.library.service.CardOfferService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Simple implementation that synthesizes a few card offers based on eligibility and profile */
public class CardOfferServiceImpl implements CardOfferService {

    @Override
    public List<CreditCardOffer> getAvailableOffers(CustomerProfile profile, EligibilityResult eligibilityResult) {
        List<CreditCardOffer> offers = new ArrayList<>();

        if (profile == null || eligibilityResult == null) {
            return offers;
        }

        // Use approved amount from eligibility as a reference for credit limits
        BigDecimal baseLimit = eligibilityResult.getAmount() != null
                ? eligibilityResult.getAmount()
                : BigDecimal.valueOf(1000);

        // Cashback card
        CreditCardOffer cashback = new CreditCardOffer();
        cashback.setOfferId("CCO-" + UUID.randomUUID().toString());
        cashback.setCustomerId(profile.getCustomerId());
        cashback.setCardType(Card.CardType.GOLD);
        cashback.setCreditLimit(baseLimit.min(BigDecimal.valueOf(50000)));
        cashback.setAnnualFee(BigDecimal.valueOf(500));
        cashback.setRewardsType("CASHBACK");
        cashback.setCreatedAt(Instant.now());
        cashback.setStatus(CreditCardOffer.OfferStatus.CREATED);
        offers.add(cashback);

        // Travel card
        CreditCardOffer travel = new CreditCardOffer();
        travel.setOfferId("CCO-" + UUID.randomUUID().toString());
        travel.setCustomerId(profile.getCustomerId());
        travel.setCardType(Card.CardType.PLATINUM);
        travel.setCreditLimit(baseLimit.min(BigDecimal.valueOf(100000)));
        travel.setAnnualFee(BigDecimal.valueOf(2000));
        travel.setRewardsType("TRAVEL");
        travel.setCreatedAt(Instant.now());
        travel.setStatus(CreditCardOffer.OfferStatus.CREATED);
        offers.add(travel);

        // Premium card
        CreditCardOffer premium = new CreditCardOffer();
        premium.setOfferId("CCO-" + UUID.randomUUID().toString());
        premium.setCustomerId(profile.getCustomerId());
        premium.setCardType(Card.CardType.TITANIUM);
        premium.setCreditLimit(baseLimit.multiply(BigDecimal.valueOf(2)).min(BigDecimal.valueOf(200000)));
        premium.setAnnualFee(BigDecimal.valueOf(5000));
        premium.setRewardsType("PREMIUM");
        premium.setCreatedAt(Instant.now());
        premium.setStatus(CreditCardOffer.OfferStatus.CREATED);
        offers.add(premium);

        return offers;
    }

    @Override
    public CreditCardOffer createOffer(CustomerProfile profile, CardPricingPolicy pricingPolicy) {
        if (profile == null || pricingPolicy == null) {
            return null;
        }

        BigDecimal income = profile.getMonthlyIncome() != null ? profile.getMonthlyIncome() : BigDecimal.ZERO;
        BigDecimal limit = income.multiply(pricingPolicy.getMaxLimitMultiplier() != null
                ? pricingPolicy.getMaxLimitMultiplier() : BigDecimal.valueOf(10));

        CreditCardOffer offer = new CreditCardOffer();
        offer.setOfferId("CCF-" + UUID.randomUUID().toString());
        offer.setCustomerId(profile.getCustomerId());
        offer.setCreditLimit(limit.min(BigDecimal.valueOf(100000)));
        offer.setAnnualFee(pricingPolicy.getBaseAnnualFee() != null ? pricingPolicy.getBaseAnnualFee() : BigDecimal.ZERO);
        offer.setRewardsType("STANDARD");
        offer.setCardType(pricingPolicy.getAllowedCardTypes() != null && !pricingPolicy.getAllowedCardTypes().isEmpty()
                ? pricingPolicy.getAllowedCardTypes().get(0) : Card.CardType.SILVER);
        offer.setCreatedAt(Instant.now());
        offer.setStatus(CreditCardOffer.OfferStatus.CREATED);

        return offer;
    }
}
