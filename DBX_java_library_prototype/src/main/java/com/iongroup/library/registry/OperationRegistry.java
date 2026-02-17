package com.iongroup.library.registry;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Optional;

/**
 * Registry interface for managing reusable workflow operations.
 * Implementations provide metadata about all available operations for frontend consumption.
 */
public interface OperationRegistry {

    /**
     * Register a single operation descriptor.
     *
     * @param descriptor the operation metadata
     */
    void register(OperationDescriptor descriptor);

    /**
     * Get all registered operations.
     *
     * @return list of all operation descriptors
     */
    List<OperationDescriptor> getAllOperations();

    /**
     * Get a single operation by ID.
     *
     * @param operationId the operation ID
     * @return Optional containing the descriptor if found
     */
    Optional<OperationDescriptor> getOperation(String operationId);

    /**
     * Get all operations in a specific category.
     *
     * @param category the category name (e.g., "loan", "card")
     * @return list of descriptors matching the category
     */
    List<OperationDescriptor> getOperationsByCategory(String category);

    /**
     * Get all operations of a specific delegation type.
     *
     * @param delegationType the delegation type (SERVICE, SCRIPT, USER_TASK)
     * @return list of descriptors matching the delegation type
     */
    List<OperationDescriptor> getOperationsByDelegationType(DelegationType delegationType);

    /**
     * Serialize all operations as a Jackson JSON tree.
     * Useful for REST endpoints to return metadata to frontends.
     *
     * @return JsonNode containing all operation descriptors
     */
    JsonNode describeAll();

    /**
     * Get the total count of registered operations.
     *
     * @return operation count
     */
    int getOperationCount();
}
