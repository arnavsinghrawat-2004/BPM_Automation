package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.LoanOffers;
import com.iongroup.library.registry.DelegationType;
import com.iongroup.library.registry.WorkFlowOperation;
import com.iongroup.library.service.NotificationService;
import com.iongroup.library.service.impl.NotificationServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Delegate for "Notify Customer" step.
 * Sends email/SMS notifications about loan offer or approval status.
 * 
 * Inputs: customerProfile, loanOffer, approvalStatus (if available)
 * Output: notificationStatus (true/false)
 * 
 * Flowable Node Name: NotifyCustomer
 */
@WorkFlowOperation(
    id = "NotifyCustomer",
    description = "Send notification to customer",
    category = "common",
    type = DelegationType.SERVICE,
    inputs = {"customerId", "notificationType", "content"},
    outputs = {"notificationStatus"},
    selectableFields = {},
    customizableFields = {}
)
public class NotifyCustomerTask implements JavaDelegate {

    private NotificationService notificationService;

    public NotifyCustomerTask() {
        // In a real application, inject via Spring
        this.notificationService = new NotificationServiceImpl();
    }

    public NotifyCustomerTask(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            // Get customer profile and loan offer from process variables
            CustomerProfile customerProfile = (CustomerProfile) execution.getVariable("customerProfile");
            LoanOffers loanOffer = (LoanOffers) execution.getVariable("loanOffer");
            
            if (customerProfile == null) {
                throw new RuntimeException("customerProfile is required for notification");
            }
            
            if (loanOffer == null) {
                throw new RuntimeException("loanOffer is required for notification");
            }

            boolean notificationStatus = false;

            // Check if approval status is available
            Boolean approvalStatus = (Boolean) execution.getVariable("approvalStatus");
            
            if (approvalStatus != null) {
                // Send approval status notification
                String message = approvalStatus ? 
                    "Congratulations! Your loan offer has been approved." :
                    "We regret to inform that your loan offer has been rejected.";
                
                notificationStatus = notificationService.notifyApprovalStatus(
                    customerProfile,
                    loanOffer,
                    approvalStatus,
                    message
                );
            } else {
                // Send loan offer notification
                notificationStatus = notificationService.notifyLoanOffer(customerProfile, loanOffer);
            }

            // Store notification status for downstream decisions
            execution.setVariable("notificationStatus", notificationStatus);
            
            System.out.println("[NotifyCustomerTask] Customer notification processed");
            System.out.println("  - Customer ID: " + customerProfile.getCustomerId());
            System.out.println("  - Customer Name: " + customerProfile.getCustomerName());
            System.out.println("  - Contact Number: " + customerProfile.getContactNumber());
            System.out.println("  - Offer ID: " + loanOffer.getOfferId());
            System.out.println("  - Notification Status: " + (notificationStatus ? "SENT" : "FAILED"));

        } catch (Exception e) {
            System.err.println("[NotifyCustomerTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to notify customer: " + e.getMessage(), e);
        }
    }
}
