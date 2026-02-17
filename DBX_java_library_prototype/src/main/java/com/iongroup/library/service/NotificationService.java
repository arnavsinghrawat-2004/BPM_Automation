package com.iongroup.library.service;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.LoanOffers;

/**
 * Service for sending notifications to customers about loan offers and approval status.
 */
public interface NotificationService {

    /**
     * Send loan offer notification to customer via email/SMS.
     * 
     * @param customerProfile the customer profile with contact details
     * @param loanOffer the loan offer to send
     * @return true if notification sent successfully, false otherwise
     */
    boolean notifyLoanOffer(CustomerProfile customerProfile, LoanOffers loanOffer);

    /**
     * Send approval status notification to customer.
     * 
     * @param customerProfile the customer profile with contact details
     * @param loanOffer the loan offer for which approval status is being sent
     * @param approvalStatus true if approved, false if rejected
     * @param message optional message to include in notification
     * @return true if notification sent successfully, false otherwise
     */
    boolean notifyApprovalStatus(
        CustomerProfile customerProfile,
        LoanOffers loanOffer,
        boolean approvalStatus,
        String message
    );
}
