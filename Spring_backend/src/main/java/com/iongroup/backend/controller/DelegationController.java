package com.iongroup.backend.controller;

import com.iongroup.backend.service.DelegationService;
import com.iongroup.library.registry.DelegationType;
import com.iongroup.library.registry.OperationDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/delegations")
public class DelegationController {

    private final DelegationService delegationService;

    public DelegationController(DelegationService delegationService) {
        this.delegationService = delegationService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDelegations() {
        try {
            List<OperationDescriptor> delegations = delegationService.getAllDelegations();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully retrieved all delegations",
                    "data", delegations
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error retrieving delegations: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getDelegationsByType(@PathVariable String type) {
        try {
            DelegationType delegationType = DelegationType.valueOf(type.toUpperCase());
            List<OperationDescriptor> delegations = delegationService.getDelegationsByType(delegationType);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully retrieved " + delegations.size() + " delegations of type " + type,
                    "data", delegations
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", "Invalid delegation type: " + type,
                            "validTypes", delegationService.getValidDelegationTypes()
                    ));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> getDelegationCount() {
        try {
            int count = delegationService.getDelegationCount();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Total delegations count: " + count,
                    "count", count
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error retrieving delegation count: " + e.getMessage()
                    ));
        }
    }

    @GetMapping("/types")
    public ResponseEntity<?> getValidDelegationTypes() {
        try {
            DelegationType[] types = delegationService.getValidDelegationTypes();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Valid delegation types",
                    "types", types
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error retrieving delegation types: " + e.getMessage()
                    ));
        }
    }
}
