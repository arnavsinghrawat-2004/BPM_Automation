package com.iongroup.library.adapter.flowable;

import com.iongroup.library.domain.CardPricingPolicy;
import com.iongroup.library.domain.CreditCardOffer;
import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.service.CardOfferService;
import com.iongroup.library.service.impl.CardOfferServiceImpl;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Delegate for "Create Card Offer" step.
 * Inputs: customerProfile, cardPricingPolicy
 * Output: creditCardOffer
 * Flowable Node Name: CreateCardOffer
 */
public class CreateCardOfferTask implements JavaDelegate {

    private CardOfferService cardOfferService;

    public CreateCardOfferTask() {
        this.cardOfferService = new CardOfferServiceImpl();
    }

    public CreateCardOfferTask(CardOfferService cardOfferService) {
        this.cardOfferService = cardOfferService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        try {
            CustomerProfile profile = (CustomerProfile) execution.getVariable("customerProfile");
            CardPricingPolicy pricingPolicy = (CardPricingPolicy) execution.getVariable("cardPricingPolicy");

            if (profile == null) {
                throw new RuntimeException("customerProfile is required to create card offer");
            }

            if (pricingPolicy == null) {
                throw new RuntimeException("cardPricingPolicy is required to create card offer");
            }

            CreditCardOffer offer = cardOfferService.createOffer(profile, pricingPolicy);

            if (offer == null) {
                throw new RuntimeException("Failed to create card offer");
            }

            execution.setVariable("creditCardOffer", offer);

            System.out.println("[CreateCardOfferTask] Card offer created: " + offer.getOfferId());

        } catch (Exception e) {
            System.err.println("[CreateCardOfferTask] Error: " + e.getMessage());
            throw new RuntimeException("Failed to create card offer: " + e.getMessage(), e);
        }
    }
}
