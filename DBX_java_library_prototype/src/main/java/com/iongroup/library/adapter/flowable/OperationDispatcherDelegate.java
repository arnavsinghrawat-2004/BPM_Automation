package com.iongroup.library.adapter.flowable;

import com.iongroup.library.exception.BusinessException;
import com.iongroup.library.registry.OperationDescriptor;
import com.iongroup.library.registry.RegistryConfiguration;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import java.util.List;
import java.util.Optional;

/**
 * Universal dispatcher delegate for BPMN.
 * Dynamically resolves and executes the actual operation
 * using RegistryConfiguration.
 */
public class OperationDispatcherDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {

        // 1️⃣ Extract operation id from BPMN task extension
        String operationId = Optional.ofNullable(
                execution.getCurrentFlowElement()
                        .getExtensionElements()
                        .get("delegationId")
        )
        .map(list -> list.get(0).getElementText())
        .orElse(null);

        if (operationId == null || operationId.isBlank()) {
            throw new BusinessException(
                    "DISPATCHER_001",
                    "Unable to resolve operation ID from BPMN activity"
            );
        }

        // 2️⃣ Lookup operation metadata dynamically
        List<OperationDescriptor> allOps = RegistryConfiguration.getAllOperations();
        OperationDescriptor descriptor = allOps.stream()
                .filter(op -> op.getId().equals(operationId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "DISPATCHER_002",
                        "No operation registered with id: " + operationId
                ));

        // 3️⃣ Validate delegate class
        String delegateClass = descriptor.getDelegateClass();
        if (delegateClass == null || delegateClass.isBlank()) {
            throw new BusinessException(
                    "DISPATCHER_003",
                    "Operation " + operationId + " has no delegateClass defined"
            );
        }

        try {
            // 4️⃣ Instantiate and execute delegate
            Class<?> clazz = Class.forName(delegateClass);
            if (!JavaDelegate.class.isAssignableFrom(clazz)) {
                throw new BusinessException(
                        "DISPATCHER_004",
                        "Delegate class does not implement JavaDelegate: " + delegateClass
                );
            }

            JavaDelegate realDelegate = (JavaDelegate) clazz.getDeclaredConstructor().newInstance();
            realDelegate.execute(execution);

        } catch (BusinessException e) {
            throw e; // preserve library-specific errors
        } catch (Exception e) {
            throw new BusinessException(
                    "DISPATCHER_005",
                    "Failed to execute delegate for operation: " + operationId,
                    e
            );
        }
    }
}
