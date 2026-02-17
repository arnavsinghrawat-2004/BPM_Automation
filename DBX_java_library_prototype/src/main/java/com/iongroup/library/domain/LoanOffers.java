package com.iongroup.library.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class LoanOffers implements Serializable{
    private static final long serialVersionUID = 7L;
    private String offerId;
    private String customerId;

    // Financial terms
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private BigDecimal totalPayableAmount;

    // Validity
    private Instant validFrom;
    private Instant validUntil;

    // Status
    private OfferStatus status;
    private Instant createdAt;
    private Instant acceptedAt;

    // Optional metadata
    private String remarks;

    public enum OfferStatus {
        CREATED,
        ACCEPTED,
        REJECTED,
        EXPIRED
    }

    // Getters & setters only (no logic)
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }
    public String getOfferId() {
        return offerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }
    public Integer getTenureMonths() {
        return tenureMonths;
    }
    public void setEmiAmount(BigDecimal emiAmount) {
        this.emiAmount = emiAmount;
    }
    public BigDecimal getEmiAmount() {
        return emiAmount;
    }
    public void setTotalPayableAmount(BigDecimal totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }
    public BigDecimal getTotalPayableAmount() {
        return totalPayableAmount;
    }
    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidFrom() {
        return validFrom;
    }
    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }
    public Instant getValidUntil() {
        return validUntil;
    }
    public void setStatus(OfferStatus status) {
        this.status = status;
    }
    public OfferStatus getStatus() {
        return status;
    }
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public void setAcceptedAt(Instant acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
    public Instant getAcceptedAt() {
        return acceptedAt;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public String getRemarks() {
        return remarks;
    }
    
}
