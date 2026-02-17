package com.iongroup.library.registry;

import org.reflections.Reflections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configuration-like helper in the library to register all annotated workflow operations.
 */
public class RegistryConfiguration {

    private static final String BASE_PACKAGE = "com.iongroup.library.adapter.flowable";

    public static List<OperationDescriptor> getAllOperations() {
        Reflections reflections = new Reflections(BASE_PACKAGE);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(WorkFlowOperation.class);

        return annotated.stream()
                .map(clazz -> {
                    WorkFlowOperation anno = clazz.getAnnotation(WorkFlowOperation.class);
                    return new OperationDescriptor(
                            anno.id(),
                            anno.description(),
                            List.of(anno.inputs()),
                            List.of(anno.outputs()),
                            clazz.getName(),
                            anno.category(),
                            anno.type(),
                            List.of(anno.selectableFields()),
                            List.of(anno.customizableFields())
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Filter operations by delegation type
     */
    public static List<OperationDescriptor> getOperationsByType(DelegationType type) {
        return getAllOperations().stream()
                .filter(op -> op.getDelegationType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Count all registered operations
     */
    public static int getOperationCount() {
        return getAllOperations().size();
    }

    /**
     * Return all valid delegation types
     */
    public static DelegationType[] getValidDelegationTypes() {
        return DelegationType.values();
    }
}
