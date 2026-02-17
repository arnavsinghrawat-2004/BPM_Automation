package com.iongroup.library.service.impl;

import com.iongroup.library.domain.LoanOffers;
import com.iongroup.library.service.ApprovalService;

import java.time.Instant;

/**
 * Implementation of ApprovalService.
 * Handles approval and rejection of loan offers.
 */
public class ApprovalServiceImpl implements ApprovalService {

    @Override
    public boolean approveOffer(LoanOffers loanOffer) {
        if (loanOffer == null) {
            System.out.println("[ApprovalService] Cannot approve null offer");
            return false;
        }

        // In a real scenario, this might involve digital signature, OTP verification, etc.
        // For now, we'll do automatic approval
        loanOffer.setStatus(LoanOffers.OfferStatus.ACCEPTED);
        loanOffer.setAcceptedAt(Instant.now());

        System.out.println("[ApprovalService] Offer " + loanOffer.getOfferId() 
            + " approved for customer: " + loanOffer.getCustomerId());
        
        return true;
    }

    @Override
    public void rejectOffer(LoanOffers loanOffer, String reason) {
        if (loanOffer == null) {
            System.out.println("[ApprovalService] Cannot reject null offer");
            return;
        }

        loanOffer.setStatus(LoanOffers.OfferStatus.REJECTED);
        loanOffer.setRemarks(reason);

        System.out.println("[ApprovalService] Offer " + loanOffer.getOfferId() 
            + " rejected for customer: " + loanOffer.getCustomerId() 
            + " | Reason: " + reason);
    }
}
