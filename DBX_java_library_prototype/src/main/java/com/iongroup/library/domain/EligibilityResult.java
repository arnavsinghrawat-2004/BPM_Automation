package com.iongroup.library.domain;

import java.math.BigDecimal;
import java.io.Serializable;

/**
 * Represents the outcome of an eligibility or affordability check.
 * This is process-agnostic and can be reused for credit card or loan decisions.
 */
public class EligibilityResult implements Serializable{
    private static final long serialVersionUID = 5L;
    /** Whether the customer is approved for this product */
    private boolean approved;

    /** Approved limit for credit card or approved loan amount */
    private BigDecimal amount;

    /** Optional reason code explaining approval/denial */
    private String reasonCode;

    // // Default constructor
    public EligibilityResult() {
    }

    // Constructor with fields
    public EligibilityResult(boolean approved, BigDecimal amount, String reasonCode) {
        this.approved = approved;
        this.amount = amount;
        this.reasonCode = reasonCode;
    }

    // Getter and Setter for approved
    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    // Getter and Setter for amount
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    // Getter and Setter for reasonCode
    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    @Override
    public String toString() {
        return "EligibilityResult{" +
                "approved=" + approved +
                ", amount=" + amount +
                ", reasonCode='" + reasonCode + '\'' +
                '}';
    }
}
