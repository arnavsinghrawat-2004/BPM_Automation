package com.iongroup.library.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class LoanPricingPolicy implements Serializable{
    private static final long serialVersionUID = 8L;
    // Constraints
    private BigDecimal maxEmiToIncomeRatio;   // e.g. 0.4
    private BigDecimal incomeMultiplier;      // e.g. 15
    private BigDecimal maxPrincipalCap;        // absolute cap

    // Options
    private List<Integer> allowedTenures;      // months
    private List<InterestSlab> interestSlabs;  // amount → rate

    public BigDecimal getMaxEmiToIncomeRatio() {
        return maxEmiToIncomeRatio;
    }

    public void setMaxEmiToIncomeRatio(BigDecimal maxEmiToIncomeRatio) {
        this.maxEmiToIncomeRatio = maxEmiToIncomeRatio;
    }

    public BigDecimal getIncomeMultiplier() {
        return incomeMultiplier;
    }

    public void setIncomeMultiplier(BigDecimal incomeMultiplier) {
        this.incomeMultiplier = incomeMultiplier;
    }

    public BigDecimal getMaxPrincipalCap() {
        return maxPrincipalCap;
    }

    public void setMaxPrincipalCap(BigDecimal maxPrincipalCap) {
        this.maxPrincipalCap = maxPrincipalCap;
    }

    public List<Integer> getAllowedTenures() {
        return allowedTenures;
    }

    public void setAllowedTenures(List<Integer> allowedTenures) {
        this.allowedTenures = allowedTenures;
    }

    public List<InterestSlab> getInterestSlabs() {
        return interestSlabs;
    }

    public void setInterestSlabs(List<InterestSlab> interestSlabs) {
        this.interestSlabs = interestSlabs;
    }
}
