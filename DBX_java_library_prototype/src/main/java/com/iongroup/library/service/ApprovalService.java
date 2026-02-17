package com.iongroup.library.service;

import com.iongroup.library.domain.LoanOffers;

/**
 * Service for handling customer approval and digital signature of loan offers.
 */
public interface ApprovalService {

    /**
     * Get approval status for a loan offer.
     * This could be manual approval, digital signature, or automated approval.
     * 
     * @param loanOffer the loan offer to approve
     * @return true if approved, false otherwise
     */
    boolean approveOffer(LoanOffers loanOffer);

    /**
     * Reject a loan offer.
     * 
     * @param loanOffer the loan offer to reject
     * @param reason the reason for rejection
     */
    void rejectOffer(LoanOffers loanOffer, String reason);
}
