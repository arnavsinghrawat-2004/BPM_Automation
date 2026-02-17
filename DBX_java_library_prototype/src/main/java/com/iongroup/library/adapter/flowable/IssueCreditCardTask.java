package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.Card;
import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.EligibilityResult;
import com.iongroup.library.service.CardIssuanceService;
import com.iongroup.library.service.impl.CardIssuanceServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Delegate for "Issue Credit Card" step.
 * Inputs: customerProfile, eligibilityStatus
 * Output: cardIssued (Card)
 * Flowable Node Name: IssueCreditCard
 */
public class IssueCreditCardTask implements JavaDelegate {

    private CardIssuanceService cardIssuanceService;

    public IssueCreditCardTask() {
        this.cardIssuanceService = new CardIssuanceServiceImpl();
    }

    public IssueCreditCardTask(CardIssuanceService cardIssuanceService) {
        this.cardIssuanceService = cardIssuanceService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            CustomerProfile profile = (CustomerProfile) execution.getVariable("customerProfile");
            EligibilityResult eligibility = (EligibilityResult) execution.getVariable("eligibilityStatus");

            if (profile == null || eligibility == null) {
                throw new RuntimeException("customerProfile and eligibilityStatus are required to issue card");
            }

            Card card = cardIssuanceService.issueCard(profile, eligibility);

            if (card == null) {
                execution.setVariable("cardIssued", null);
                System.out.println("[IssueCreditCardTask] Card not issued (not eligible)");
                return;
            }

            execution.setVariable("cardIssued", card);

            System.out.println("[IssueCreditCardTask] Card issued: " + card.getCardNumber());

        } catch (Exception e) {
            System.err.println("[IssueCreditCardTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to issue credit card: " + e.getMessage(), e);
        }
    }
}
