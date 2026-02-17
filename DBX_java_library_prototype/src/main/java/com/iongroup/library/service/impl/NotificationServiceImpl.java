package com.iongroup.library.service.impl;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.LoanOffers;
import com.iongroup.library.service.NotificationService;

/**
 * Implementation of NotificationService.
 * Sends email/SMS notifications to customers.
 */
public class NotificationServiceImpl implements NotificationService {

    @Override
    public boolean notifyLoanOffer(CustomerProfile customerProfile, LoanOffers loanOffer) {
        if (customerProfile == null || loanOffer == null) {
            System.out.println("[NotificationService] Cannot notify: missing customer or offer");
            return false;
        }

        try {
            // In production, integrate with email/SMS provider (SendGrid, Twilio, etc.)
            String message = String.format(
                "Dear %s, Your loan offer has been created.\n" +
                "Principal: %.2f | Interest Rate: %.2f%% | Tenure: %d months | EMI: %.2f\n" +
                "Offer ID: %s | Valid until: %s",
                customerProfile.getCustomerName(),
                loanOffer.getPrincipalAmount(),
                loanOffer.getInterestRate(),
                loanOffer.getTenureMonths(),
                loanOffer.getEmiAmount(),
                loanOffer.getOfferId(),
                loanOffer.getValidUntil()
            );

            System.out.println("[NotificationService] Sending offer notification to: " 
                + customerProfile.getContactNumber());
            System.out.println(message);
            
            return true;
        } catch (Exception e) {
            System.err.println("[NotificationService] Error sending notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean notifyApprovalStatus(
            CustomerProfile customerProfile,
            LoanOffers loanOffer,
            boolean approvalStatus,
            String message) {
        
        if (customerProfile == null || loanOffer == null) {
            System.out.println("[NotificationService] Cannot notify: missing customer or offer");
            return false;
        }

        try {
            String status = approvalStatus ? "APPROVED" : "REJECTED";
            String notification = String.format(
                "Dear %s, Your loan offer is %s.\n" +
                "Offer ID: %s | Principal: %.2f | Status: %s\n" +
                "%s",
                customerProfile.getCustomerName(),
                status,
                loanOffer.getOfferId(),
                loanOffer.getPrincipalAmount(),
                status,
                message != null ? "Message: " + message : ""
            );

            System.out.println("[NotificationService] Sending approval notification to: " 
                + customerProfile.getContactNumber());
            System.out.println(notification);
            
            return true;
        } catch (Exception e) {
            System.err.println("[NotificationService] Error sending notification: " + e.getMessage());
            return false;
        }
    }
}
