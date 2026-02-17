package com.iongroup.library.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class InterestSlab implements Serializable{
    private static final long serialVersionUID = 6L;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;   // null = no upper bound
    private BigDecimal annualRate;  // percentage

    public InterestSlab() {}

    public InterestSlab(
            BigDecimal minAmount,
            BigDecimal maxAmount,
            BigDecimal annualRate
    ) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.annualRate = annualRate;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public BigDecimal getAnnualRate() {
        return annualRate;
    }

    public void setAnnualRate(BigDecimal annualRate) {
        this.annualRate = annualRate;
    }

    public boolean matches(BigDecimal amount) {
        boolean aboveMin = amount.compareTo(minAmount) >= 0;
        boolean belowMax = (maxAmount == null) || amount.compareTo(maxAmount) <= 0;
        return aboveMin && belowMax;
    }
}
