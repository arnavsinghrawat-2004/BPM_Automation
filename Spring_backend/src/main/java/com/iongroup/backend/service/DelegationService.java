package com.iongroup.backend.service;

import com.iongroup.library.registry.DelegationType;
import com.iongroup.library.registry.OperationDescriptor;
import com.iongroup.library.registry.RegistryConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DelegationService {

    public List<OperationDescriptor> getAllDelegations() {
        return RegistryConfiguration.getAllOperations();
    }

    public List<OperationDescriptor> getDelegationsByType(DelegationType type) {
        return RegistryConfiguration.getOperationsByType(type);
    }

    public int getDelegationCount() {
        return RegistryConfiguration.getOperationCount();
    }

    public DelegationType[] getValidDelegationTypes() {
        return RegistryConfiguration.getValidDelegationTypes();
    }
}
