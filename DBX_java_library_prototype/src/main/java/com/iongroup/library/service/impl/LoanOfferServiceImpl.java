package com.iongroup.library.service.impl;

import com.iongroup.library.domain.*;
import com.iongroup.library.exception.BusinessException;
import com.iongroup.library.service.LoanOfferService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class LoanOfferServiceImpl implements LoanOfferService {

    @Override
    public LoanOffers createOffer(
            CustomerProfile profile,
            LoanPricingPolicy policy
    ) {
        validate(profile, policy);

        BigDecimal income = profile.getMonthlyIncome();

        // Max principal allowed
        BigDecimal byIncome =
                income.multiply(policy.getIncomeMultiplier());

        BigDecimal principal =
                byIncome.min(policy.getMaxPrincipalCap());

        // Choose tenure (shortest allowed)
        int tenure = policy.getAllowedTenures().get(0);

        // Interest rate slab
        BigDecimal rate = policy.getInterestSlabs()
                .stream()
                .filter(s -> s.matches(principal))
                .findFirst()
                .orElseThrow(() ->
                        new BusinessException("No interest slab matches amount"))
                .getAnnualRate();

        BigDecimal emi = calculateEmi(principal, rate, tenure);

        LoanOffers offer = new LoanOffers();
        offer.setOfferId("LO-" + System.currentTimeMillis());
        offer.setCustomerId(profile.getCustomerId());
        offer.setPrincipalAmount(principal);
        offer.setInterestRate(rate);
        offer.setTenureMonths(tenure);
        offer.setEmiAmount(emi);
        offer.setCreatedAt(Instant.now());
        offer.setStatus(LoanOffers.OfferStatus.CREATED);

        return offer;
    }

    private void validate(CustomerProfile profile, LoanPricingPolicy policy) {
        if (profile == null) {
            throw new BusinessException("CustomerProfile is required");
        }
        if (policy == null) {
            throw new BusinessException("LoanPricingPolicy is required");
        }
        if (policy.getInterestSlabs() == null || policy.getInterestSlabs().isEmpty()) {
            throw new BusinessException("Interest slabs not configured");
        }
        if (policy.getAllowedTenures() == null || policy.getAllowedTenures().isEmpty()) {
            throw new BusinessException("Allowed tenures not configured");
        }
    }

    private BigDecimal calculateEmi(
            BigDecimal principal,
            BigDecimal annualRate,
            int months
    ) {
        BigDecimal monthlyRate =
                annualRate.divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        BigDecimal numerator =
                principal.multiply(monthlyRate)
                        .multiply((BigDecimal.ONE.add(monthlyRate)).pow(months));

        BigDecimal denominator =
                (BigDecimal.ONE.add(monthlyRate)).pow(months).subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
