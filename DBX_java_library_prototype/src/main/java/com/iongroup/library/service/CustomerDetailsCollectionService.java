package com.iongroup.library.service;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.domain.RequiredCustomerDetails;

/**
 * Collects missing customer details from the user or external systems.
 */
public interface CustomerDetailsCollectionService {

    /**
     * Populate only the required fields into the customer profile.
     *
     * @param profile existing customer profile
     * @param required which fields must be populated
     */
    void collectRequiredDetails(CustomerProfile profile, RequiredCustomerDetails required);
}
 
    

