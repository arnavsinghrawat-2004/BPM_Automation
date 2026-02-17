package com.iongroup.library.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/** Pricing policy used when creating credit card offers. */
public class CardPricingPolicy implements Serializable{
    private static final long serialVersionUID = 2L;
    private BigDecimal maxLimitMultiplier; // multiplier over monthly income
    private BigDecimal baseAnnualFee;
    private List<Card.CardType> allowedCardTypes;

    public CardPricingPolicy() {}

    public BigDecimal getMaxLimitMultiplier() {
        return maxLimitMultiplier;
    }

    public void setMaxLimitMultiplier(BigDecimal maxLimitMultiplier) {
        this.maxLimitMultiplier = maxLimitMultiplier;
    }

    public BigDecimal getBaseAnnualFee() {
        return baseAnnualFee;
    }

    public void setBaseAnnualFee(BigDecimal baseAnnualFee) {
        this.baseAnnualFee = baseAnnualFee;
    }

    public java.util.List<Card.CardType> getAllowedCardTypes() {
        return allowedCardTypes;
    }

    public void setAllowedCardTypes(java.util.List<Card.CardType> allowedCardTypes) {
        this.allowedCardTypes = allowedCardTypes;
    }
}
