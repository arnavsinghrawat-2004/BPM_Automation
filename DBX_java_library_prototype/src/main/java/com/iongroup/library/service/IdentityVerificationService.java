package com.iongroup.library.service;

import com.iongroup.library.domain.CustomerProfile;

public interface IdentityVerificationService {

    /**
     * Verify the identity of a customer (OTP/KYC).
     * Returns true if verification succeeds, false otherwise.
     */
    boolean verifyIdentity(CustomerProfile customerProfile);
}
