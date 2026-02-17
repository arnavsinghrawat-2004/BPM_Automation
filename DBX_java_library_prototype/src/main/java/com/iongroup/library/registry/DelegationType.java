package com.iongroup.library.registry;

/**
 * Enum representing the type of delegation operation.
 * 
 * Each delegation type indicates where and how the task is executed:
 * - SERVICE: Java code interacts with other software/external systems to perform work
 * - SCRIPT: Task executes within the company system only (internal processing)
 * - USER_TASK: Task is executed on the user end (user interaction required)
 */
public enum DelegationType {
    /**
     * Service delegation: Java code interacts with other software/external systems.
     * Examples: calling external APIs, database operations, third-party integrations.
     */
    SERVICE("Service"),
    
    /**
     * Script delegation: Task happens in the company system only.
     * Examples: internal calculations, data transformations, internal business logic.
     */
    SCRIPT("Script"),
    
    /**
     * User task delegation: Task happens on the user end.
     * Examples: user approvals, form submissions, user-initiated actions.
     */
    USER_TASK("User Task");

    private final String displayName;

    DelegationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
