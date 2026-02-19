package com.iongroup.library.registry;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Configuration-like helper in the library to register all annotated workflow
 * operations.
 */
public class RegistryConfiguration {

    private static final String BASE_PACKAGE = "com.iongroup.library.adapter.flowable";

    public static void main(String[] args) {

        // 1. Print all operations
        System.out.println("=== All Registered Operations ===");
        RegistryConfiguration.getAllOperations().forEach(op -> {
            System.out.println("ID: " + op.getId());
            System.out.println("Description: " + op.getDescription());
            System.out.println("Class: " + op.getDelegateClass());
            System.out.println("Type: " + op.getDelegationType());
            System.out.println("Category: " + op.getCategory());
            System.out.println("------------------------------");
        });

        // 2. Print total count
        int count = RegistryConfiguration.getOperationCount();
        //System.out.println("Total operations: " + count);

        // 3. Print operations filtered by a DelegationType (example: SERVICE)
        System.out.println("=== SERVICE Operations ===");
        RegistryConfiguration.getOperationsByType(DelegationType.SERVICE)
                .forEach(op -> System.out.println(op.getId() + " (" + op.getDelegateClass() + ")"));
    }

    public static List<OperationDescriptor> getAllOperations() {
        List<OperationDescriptor> result = new ArrayList<>();

        try {
            // Get the JAR location from the classloader
            URL jarUrl = RegistryConfiguration.class.getProtectionDomain()
                    .getCodeSource().getLocation();

            //System.out.println("JAR location: " + jarUrl);

            String packagePath = BASE_PACKAGE.replace('.', '/');

            try (JarFile jarFile = new JarFile(new File(jarUrl.toURI()))) {
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    // Match only .class files in our target package
                    if (name.startsWith(packagePath) && name.endsWith(".class") && !name.contains("$")) {
                        // Convert path to class name: com/foo/Bar.class -> com.foo.Bar
                        String className = name.replace('/', '.').replace(".class", "");

                        try {
                            Class<?> clazz = Class.forName(className,
                                    false,
                                    Thread.currentThread().getContextClassLoader());

                            if (clazz.isAnnotationPresent(WorkFlowOperation.class)) {
                                WorkFlowOperation anno = clazz.getAnnotation(WorkFlowOperation.class);
                                result.add(new OperationDescriptor(
                                        anno.id(),
                                        anno.description(),
                                        List.of(anno.inputs()),
                                        List.of(anno.outputs()),
                                        clazz.getName(),
                                        anno.category(),
                                        anno.type(),
                                        List.of(anno.selectableFields()),
                                        List.of(anno.customizableFields())));
                                //System.out.println("Registered: " + className);
                            }
                        } catch (ClassNotFoundException e) {
                            System.err.println("Could not load class: " + className);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan for WorkFlowOperation annotations", e);
        }

        //System.out.println("Total operations found: " + result.size());
        return result;
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
