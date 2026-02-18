package com.iongroup.backend.controller;

import com.iongroup.backend.service.DelegationService;
import com.iongroup.library.registry.DelegationType;
import com.iongroup.library.registry.OperationDescriptor;
import com.iongroup.library.registry.RegistryConfiguration;
import com.iongroup.library.registry.WorkFlowOperation;

import java.io.File;
import java.net.URL;

import org.reflections.util.ClasspathHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delegations")
public class DelegationController {

//     private final DelegationService delegationService;

//     public DelegationController(DelegationService delegationService) {
//         this.delegationService = delegationService;
//     }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDelegations() {
        List<OperationDescriptor> delegations = delegationService.getAllDelegations();
        System.out.println(delegations);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully retrieved all delegations",
                "data", delegations));
    }

    @GetMapping("/debug2")
    public ResponseEntity<?> debug2() throws Exception {
        URL jarUrl = RegistryConfiguration.class.getProtectionDomain()
                .getCodeSource().getLocation();

        String packagePath = "com/iongroup/library/adapter/flowable";
        List<String> foundClasses = new ArrayList<>();
        List<String> annotatedClasses = new ArrayList<>();

        try (JarFile jarFile = new JarFile(new File(jarUrl.toURI()))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.startsWith(packagePath) && name.endsWith(".class")) {
                    foundClasses.add(name);
                    String className = name.replace('/', '.').replace(".class", "");
                    try {
                        Class<?> clazz = Class.forName(className, false,
                                Thread.currentThread().getContextClassLoader());
                        if (clazz.isAnnotationPresent(WorkFlowOperation.class)) {
                            annotatedClasses.add(className);
                        }
                    } catch (Exception e) {
                        foundClasses.add("ERROR loading: " + className + " -> " + e.getMessage());
                    }
                }
            }
        }

        return ResponseEntity.ok(Map.of(
                "jarLocation", jarUrl.toString(),
                "classesInPackage", foundClasses,
                "annotatedClasses", annotatedClasses));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getDelegationsByType(@PathVariable String type) {
        try {
            DelegationType delegationType = DelegationType.valueOf(type.toUpperCase());
            List<OperationDescriptor> delegations = delegationService.getDelegationsByType(delegationType);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully retrieved " + delegations.size() + " delegations of type " + type,
                    "data", delegations));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid delegation type: " + type,
                    "validTypes", delegationService.getValidDelegationTypes()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getDelegationCount() {
        int count = delegationService.getDelegationCount();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Total delegations count: " + count,
                "count", count));
    }

    @GetMapping("/types")
    public ResponseEntity<?> getValidDelegationTypes() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Valid delegation types",
                "types", delegationService.getValidDelegationTypes()));
    }

    @GetMapping("/debug")
    public ResponseEntity<?> debug() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader libraryClassLoader = RegistryConfiguration.class.getClassLoader();

        Set<URL> urls = new HashSet<>();
        urls.addAll(ClasspathHelper.forPackage("com.iongroup.library.adapter.flowable", contextClassLoader));
        urls.addAll(ClasspathHelper.forPackage("com.iongroup.library.adapter.flowable", libraryClassLoader));

        return ResponseEntity.ok(Map.of(
                "contextClassLoader", contextClassLoader.toString(),
                "libraryClassLoader", libraryClassLoader.toString(),
                "urlsFound", urls.stream().map(URL::toString).collect(Collectors.toList()),
                "urlCount", urls.size()));
    }
}
