package com.iongroup.library.service;

import com.iongroup.library.domain.CustomerProfile;

/**
 * Service for fetching and managing customer profiles from database or CRM.
 */
public interface CustomerProfileService {

    /**
     * Fetch customer profile by customerId.
     * 
     * @param customerId the unique customer identifier
     * @return CustomerProfile if found, null otherwise
     */
    CustomerProfile getCustomerProfile(String customerId);

    /**
     * Save or update customer profile.
     * 
     * @param customerProfile the profile to save
     */
    void saveCustomerProfile(CustomerProfile customerProfile);
}
