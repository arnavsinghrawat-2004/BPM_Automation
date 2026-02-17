package com.iongroup.library.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/** Represents a credit card offer presented to the customer. */
public class CreditCardOffer implements Serializable{
    private static final long serialVersionUID = 3L;
    private String offerId;
    private String customerId;
    private Card.CardType cardType;

    private BigDecimal creditLimit;
    private BigDecimal annualFee;
    private String rewardsType; // e.g., CASHBACK, TRAVEL, PREMIUM

    private Instant validFrom;
    private Instant validUntil;

    private OfferStatus status;
    private Instant createdAt;

    public enum OfferStatus {
        CREATED, ACCEPTED, REJECTED, EXPIRED
    }

    public CreditCardOffer() {}

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Card.CardType getCardType() {
        return cardType;
    }

    public void setCardType(Card.CardType cardType) {
        this.cardType = cardType;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public String getRewardsType() {
        return rewardsType;
    }

    public void setRewardsType(String rewardsType) {
        this.rewardsType = rewardsType;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
