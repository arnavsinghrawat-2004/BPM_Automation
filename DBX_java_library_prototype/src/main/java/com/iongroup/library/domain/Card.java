package com.iongroup.library.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a credit card issued to a customer.
**/
public class Card implements Serializable{
    private static final long serialVersionUID = 1L;
    /** Card number (can be generated later) */
    private String cardNumber;

    /** Name of the cardholder */
    private String cardHolderName;

    /** Credit limit */
    private BigDecimal creditLimit;

    /** Issue date */
    private LocalDate issueDate;

    /** Expiration date */
    private LocalDate expiryDate;

    // Enum defined inside the Card class
    public enum CardType {
        SILVER,
        GOLD,
        PLATINUM,
        TITANIUM
    }

    /** Card type (Silver, Gold, Platinum, Titanium) */
    private CardType cardType;

    // Default constructor
    public Card() {}

    // Full constructor
    public Card(String cardNumber, String cardHolderName, BigDecimal creditLimit,
                LocalDate issueDate, LocalDate expiryDate, CardType cardType) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.creditLimit = creditLimit;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
    }

    // Getters and setters
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber='" + cardNumber + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", creditLimit=" + creditLimit +
                ", issueDate=" + issueDate +
                ", expiryDate=" + expiryDate +
                ", cardType=" + cardType +
                '}';
    }
}
