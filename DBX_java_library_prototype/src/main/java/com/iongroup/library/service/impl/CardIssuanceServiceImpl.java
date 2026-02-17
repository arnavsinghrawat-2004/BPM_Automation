package com.iongroup.library.service.impl;

import com.iongroup.library.domain.Card;
import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.EligibilityResult;
import com.iongroup.library.service.CardIssuanceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class CardIssuanceServiceImpl implements CardIssuanceService {

    @Override
    public Card issueCard(CustomerProfile customerProfile, EligibilityResult eligibilityResult) {
        if (customerProfile == null || eligibilityResult == null) {
            System.out.println("[CardIssuanceService] Missing input");
            return null;
        }

        if (!eligibilityResult.isApproved()) {
            System.out.println("[CardIssuanceService] Customer not eligible: " + eligibilityResult.getReasonCode());
            return null;
        }

        // Create new Card object
        Card card = new Card();
        card.setCardNumber(generateCardNumber());
        card.setCardHolderName(customerProfile.getCustomerName());
        card.setCreditLimit(eligibilityResult.getAmount());
        card.setIssueDate(LocalDate.now());
        card.setExpiryDate(LocalDate.now().plusYears(3)); // 3-year default validity

        // Determine CardType from reasonCode (simplest approach)
        String reason = eligibilityResult.getReasonCode();
        if (reason.contains("PLATINUM")) {
            card.setCardType(Card.CardType.PLATINUM);
        } else if (reason.contains("GOLD")) {
            card.setCardType(Card.CardType.GOLD);
        } else {
            card.setCardType(Card.CardType.SILVER);
        }

        System.out.println("[CardIssuanceService] Issued card: " + card);

        return card;
    }

    // Simple card number generator
    private String generateCardNumber() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}