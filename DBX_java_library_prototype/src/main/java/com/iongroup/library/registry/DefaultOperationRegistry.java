package com.iongroup.library.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of OperationRegistry.
 * Thread-safe registry that manages all workflow operation metadata.
 */
public class DefaultOperationRegistry implements OperationRegistry {

    private final Map<String, OperationDescriptor> operations = new LinkedHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public synchronized void register(OperationDescriptor descriptor) {
        if (descriptor == null || descriptor.getId() == null) {
            throw new IllegalArgumentException("Operation descriptor and ID cannot be null");
        }
        operations.put(descriptor.getId(), descriptor);
    }

    @Override
    public synchronized List<OperationDescriptor> getAllOperations() {
        return new ArrayList<>(operations.values());
    }

    @Override
    public synchronized Optional<OperationDescriptor> getOperation(String operationId) {
        return Optional.ofNullable(operations.get(operationId));
    }

    @Override
    public synchronized List<OperationDescriptor> getOperationsByCategory(String category) {
        return operations.values()
                .stream()
                .filter(op -> category.equals(op.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<OperationDescriptor> getOperationsByDelegationType(DelegationType delegationType) {
        return operations.values()
                .stream()
                .filter(op -> delegationType.equals(op.getDelegationType()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized JsonNode describeAll() {
        return objectMapper.valueToTree(getAllOperations());
    }

    @Override
    public synchronized int getOperationCount() {
        return operations.size();
    }

    /**
     * Get the underlying map (for testing or advanced usage).
     */
    protected synchronized Map<String, OperationDescriptor> getOperationsMap() {
        return new LinkedHashMap<>(operations);
    }
}
