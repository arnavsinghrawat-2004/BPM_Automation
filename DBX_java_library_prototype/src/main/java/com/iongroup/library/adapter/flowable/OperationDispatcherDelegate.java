package com.iongroup.library.adapter.flowable;

import com.iongroup.library.exception.BusinessException;
import com.iongroup.library.registry.OperationDescriptor;
import com.iongroup.library.registry.OperationRegistry;
import com.iongroup.library.registry.OperationRegistryFactory;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * Universal dispatcher delegate.
 *
 * This is the ONLY delegate referenced in BPMN files.
 * It dynamically resolves and executes the actual operation
 * using the OperationRegistry.
 */
public class OperationDispatcherDelegate implements JavaDelegate {

    private static final OperationRegistry REGISTRY =
            OperationRegistryFactory.getRegistry();

    @Override
    public void execute(DelegateExecution execution) {

        // 1️⃣ BPMN task id == operation id
        String operationId =
            (String) execution.getCurrentFlowElement()
                .getExtensionElements()
                .get("delegationId")
                .get(0)
                .getElementText();


        if (operationId == null || operationId.isBlank()) {
            throw new BusinessException(
                    "DISPATCHER_001",
                    "Unable to resolve operation ID from BPMN activity"
            );
        }

        // 2️⃣ Lookup operation metadata
        OperationDescriptor descriptor = REGISTRY
                .getOperation(operationId)
                .orElseThrow(() -> new BusinessException(
                        "DISPATCHER_002",
                        "No operation registered with id: " + operationId
                ));

        // 3️⃣ Validate delegation type (future-proofing)
        if (descriptor.getDelegateClass() == null ||
            descriptor.getDelegateClass().isBlank()) {
            throw new BusinessException(
                    "DISPATCHER_003",
                    "Operation " + operationId + " has no delegateClass defined"
            );
        }

        try {
            // 4️⃣ Instantiate the real delegate
            Class<?> clazz = Class.forName(descriptor.getDelegateClass());

            if (!JavaDelegate.class.isAssignableFrom(clazz)) {
                throw new BusinessException(
                        "DISPATCHER_004",
                        "Delegate class does not implement JavaDelegate: "
                                + descriptor.getDelegateClass()
                );
            }

            JavaDelegate realDelegate =
                    (JavaDelegate) clazz.getDeclaredConstructor().newInstance();

            // 5️⃣ Execute business logic
            realDelegate.execute(execution);

        } catch (BusinessException e) {
            throw e; // preserve business semantics
        } catch (Exception e) {
            throw new BusinessException(
                    "DISPATCHER_005",
                    "Failed to execute delegate for operation: " + operationId,
                    e
            );
        }
    }
}
