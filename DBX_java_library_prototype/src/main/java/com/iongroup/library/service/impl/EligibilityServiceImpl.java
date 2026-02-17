package com.iongroup.library.service.impl;

import com.iongroup.library.domain.Card;
import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.EligibilityResult;
import com.iongroup.library.exception.BusinessException;
import com.iongroup.library.service.EligibilityService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class EligibilityServiceImpl implements EligibilityService {

    @Override
    public EligibilityResult checkEligibility(CustomerProfile customerProfile,
                                            BigDecimal requestedAmountLimit) {

        EligibilityResult result = new EligibilityResult();

        // 1. Null check
        if (customerProfile == null) {
            result.setApproved(false);
            result.setAmount(BigDecimal.ZERO);
            result.setReasonCode("Customer profile is missing");
            return result;
        }

        // 2. KYC validation
        if (isBlank(customerProfile.getPanNumber()) ||
            isBlank(customerProfile.getAadharNumber())) {

            result.setApproved(false);
            result.setAmount(BigDecimal.ZERO);
            result.setReasonCode("Missing PAN or Aadhar");
            return result;
        }

        // 3. Minimum balance check
        BigDecimal minBalance = BigDecimal.valueOf(10000);
        if (customerProfile.getAccountBalance() == null ||
            customerProfile.getAccountBalance().compareTo(minBalance) < 0) {

            result.setApproved(false);
            result.setAmount(BigDecimal.ZERO);
            result.setReasonCode("Account balance too low");
            return result;
        }

        // 4. Determine system eligibility
        BigDecimal balance = customerProfile.getAccountBalance();
        Card.CardType cardType;
        BigDecimal systemEligibleAmount;

        if (balance.compareTo(BigDecimal.valueOf(50000)) >= 0) {
            cardType = Card.CardType.PLATINUM;
            systemEligibleAmount = BigDecimal.valueOf(50000);
        } else if (balance.compareTo(BigDecimal.valueOf(30000)) >= 0) {
            cardType = Card.CardType.GOLD;
            systemEligibleAmount = BigDecimal.valueOf(30000);
        } else {
            cardType = Card.CardType.SILVER;
            systemEligibleAmount = BigDecimal.valueOf(10000);
        }

        // 🔴 THIS IS THE MISSING PART 🔴
        if (requestedAmountLimit == null ||
            requestedAmountLimit.compareTo(BigDecimal.ZERO) <= 0) {

            result.setApproved(false);
            result.setAmount(BigDecimal.ZERO);
            result.setReasonCode("Invalid requested amount limit");
            return result;
        }

        if (requestedAmountLimit.compareTo(systemEligibleAmount) > 0) {
            result.setApproved(false);
            result.setAmount(BigDecimal.ZERO);
            result.setReasonCode(
                "Requested limit exceeds eligibility for " + cardType.name()
            );
            throw new BusinessException("Can't proceed if loan amount not approved");
        }

        // 5. Approved – cap at requested limit
        result.setApproved(true);
        result.setAmount(requestedAmountLimit);
        result.setReasonCode("Approved: " + cardType.name());

        System.out.println("[EligibilityService] Customer: " +
                customerProfile.getCustomerName() +
                " | Requested: " + requestedAmountLimit +
                " | Approved: " + result.getAmount());

        System.out.println("Eligibility Amount: "+result.getAmount());
        return result;
    }


    // Utility
    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}