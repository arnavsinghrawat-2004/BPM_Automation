package com.iongroup.library.service.impl;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.RequiredCustomerDetails;
import com.iongroup.library.service.CustomerDetailsCollectionService;

import java.math.BigDecimal;

/**
 * Mock implementation.
 * In real systems, this would call UI, APIs, or document upload flows.
 */
public class CustomerCollectionServiceImpl implements CustomerDetailsCollectionService {

    @Override
    public void collectRequiredDetails(CustomerProfile profile,
                                       RequiredCustomerDetails required) {

        if (required.isCustomerName() && profile.getCustomerName() == null) {
            profile.setCustomerName("Entered Name");
        }

        if (required.isContactNumber() && profile.getContactNumber() == null) {
            profile.setContactNumber("9999999999");
        }

        if (required.isCustomerAddress() && profile.getCustomerAddress() == null) {
            profile.setCustomerAddress("Entered Address");
        }

        if (required.isPanNumber() && profile.getPanNumber() == null) {
            profile.setPanNumber("ABCDE1234F");
        }

        if (required.isAadharNumber() && profile.getAadharNumber() == null) {
            profile.setAadharNumber("111122223333");
        }

        if (required.isMonthlyIncome() && profile.getMonthlyIncome() == null) {
            profile.setMonthlyIncome(new BigDecimal("50000"));
        }



        System.out.println("[CustomerDetailsCollectionService] Required fields populated");
    }
}
