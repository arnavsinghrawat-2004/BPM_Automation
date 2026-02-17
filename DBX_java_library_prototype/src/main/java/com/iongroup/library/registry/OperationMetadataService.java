package com.iongroup.library.registry;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Utility class to expose registry metadata for frontends.
 * Can be used directly or wrapped in a REST controller.
 */
public class OperationMetadataService {

    private final OperationRegistry registry;

    public OperationMetadataService(OperationRegistry registry) {
        this.registry = registry;
    }

    /**
     * Get all operations for frontend UI population.
     */
    public List<OperationDescriptor> getAllOperations() {
        return registry.getAllOperations();
    }

    /**
     * Get operations grouped by category for filtering.
     */
    public List<OperationDescriptor> getOperationsByCategory(String category) {
        return registry.getOperationsByCategory(category);
    }

    /**
     * Get the complete metadata as JSON (for REST endpoints).
     */
    public JsonNode getOperationsAsJson() {
        return registry.describeAll();
    }

    /**
     * Get a single operation by ID.
     */
    public OperationDescriptor getOperation(String operationId) {
        return registry.getOperation(operationId).orElse(null);
    }

    /**
     * Get registry statistics.
     */
    public RegistryStats getStats() {
        List<OperationDescriptor> all = getAllOperations();
        return new RegistryStats(
                all.size(),
                all.stream().map(OperationDescriptor::getCategory).distinct().count()
        );
    }

    /**
     * Simple stats container.
     */
    public static class RegistryStats {
        public int totalOperations;
        public long totalCategories;

        public RegistryStats(int totalOperations, long totalCategories) {
            this.totalOperations = totalOperations;
            this.totalCategories = totalCategories;
        }
    }
}
