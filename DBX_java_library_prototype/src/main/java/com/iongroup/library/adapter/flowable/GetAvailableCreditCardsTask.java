package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.CreditCardOffer;
import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.EligibilityResult;
import com.iongroup.library.service.CardOfferService;
import com.iongroup.library.service.impl.CardOfferServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.List;

/**
 * Delegate for "Get Available Credit Cards" step.
 * Inputs: customerProfile, eligibilityStatus (EligibilityResult)
 * Output: availableCardOffers
 * Flowable Node Name: GetAvailableCreditCards
 */
public class GetAvailableCreditCardsTask implements JavaDelegate {

    private CardOfferService cardOfferService;

    public GetAvailableCreditCardsTask() {
        this.cardOfferService = new CardOfferServiceImpl();
    }

    public GetAvailableCreditCardsTask(CardOfferService cardOfferService) {
        this.cardOfferService = cardOfferService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            CustomerProfile profile = (CustomerProfile) execution.getVariable("customerProfile");
            EligibilityResult eligibility = (EligibilityResult) execution.getVariable("eligibilityStatus");

            if (profile == null) {
                throw new RuntimeException("customerProfile is required to fetch card offers");
            }

            List<CreditCardOffer> offers = cardOfferService.getAvailableOffers(profile, eligibility);

            execution.setVariable("availableCardOffers", offers);

            System.out.println("[GetAvailableCreditCardsTask] Generated " + (offers != null ? offers.size() : 0) + " offers for " + profile.getCustomerId());

        } catch (Exception e) {
            System.err.println("[GetAvailableCreditCardsTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to get available credit cards: " + e.getMessage(), e);
        }
    }
}
