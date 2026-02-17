package com.iongroup.library.service.impl;

import com.iongroup.library.domain.CustomerProfile;
import com.iongroup.library.service.CustomerProfileService;

/**
 * Implementation of CustomerProfileService.
 * In a real application, this would fetch from a database or CRM.
 * For now, it returns mock data.
 */
public class CustomerProfileServiceImpl implements CustomerProfileService {

    @Override
    public CustomerProfile getCustomerProfile(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return null;
        }

        // Mock: In production, fetch from database/CRM
        CustomerProfile profile = new CustomerProfile();
        profile.setCustomerId(customerId);
        profile.setCustomerName("John Doe");
        profile.setContactNumber("9876543210");
        profile.setPanNumber("AAAAA1234A");
        profile.setAadharNumber("123456789012");
        
        // Generate realistic mock data based on customerId
        long idHash = customerId.hashCode();
        java.math.BigDecimal balance = new java.math.BigDecimal((Math.abs(idHash) % 500000) + 10000);
        java.math.BigDecimal income = new java.math.BigDecimal((Math.abs(idHash) % 200000) + 30000);
        
        profile.setAccountBalance(balance);
        profile.setMonthlyIncome(income);
        profile.setCustomerAddress("123 Main Street, City, Zip");

        System.out.println("[CustomerProfileService] Fetched profile for customerId: " + customerId);
        return profile;
    }

    @Override
    public void saveCustomerProfile(CustomerProfile customerProfile) {
        if (customerProfile != null) {
            System.out.println("[CustomerProfileService] Saved profile for customerId: " 
                + customerProfile.getCustomerId());
        }
    }
}
