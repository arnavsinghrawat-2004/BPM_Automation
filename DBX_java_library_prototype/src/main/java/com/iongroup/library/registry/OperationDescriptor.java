package com.iongroup.library.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Metadata descriptor for a reusable workflow operation (delegate).
 * Used by frontends (e.g., React Flow) to dynamically populate UI nodes.
 */
public class OperationDescriptor {

    private String id; // Unique operation ID (e.g., "StartLoanApplication")
    private String description; // Short description of what the operation does
    private List<String> inputs; // Expected input variable names
    private List<String> outputs; // Output variable names produced by the operation
    private String delegateClass; // Fully qualified class name of the JavaDelegate
    private String category; // Optional category (e.g., "loan", "card", "common")
    private DelegationType delegationType; // Type of delegation (SERVICE, SCRIPT, USER_TASK)
    private List<String> selectableFields;
    private List<String> customizableFields;

    public OperationDescriptor() {
    }

    public OperationDescriptor(String id, String description, List<String> inputs, List<String> outputs) {
        this.id = id;
        this.description = description;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public OperationDescriptor(String id, String description, List<String> inputs, List<String> outputs,
            String delegateClass) {
        this.id = id;
        this.description = description;
        this.inputs = inputs;
        this.outputs = outputs;
        this.delegateClass = delegateClass;
    }

    public OperationDescriptor(String id, String description, List<String> inputs, List<String> outputs,
            String delegateClass, String category) {
        this.id = id;
        this.description = description;
        this.inputs = inputs;
        this.outputs = outputs;
        this.delegateClass = delegateClass;
        this.category = category;
    }

    public OperationDescriptor(String id, String description, List<String> inputs, List<String> outputs,
            String delegateClass, String category, DelegationType delegationType) {
        this.id = id;
        this.description = description;
        this.inputs = inputs;
        this.outputs = outputs;
        this.delegateClass = delegateClass;
        this.category = category;
        this.delegationType = delegationType;
    }

    public OperationDescriptor(String id, String description, List<String> inputs, List<String> outputs,
            String delegateClass, String category, DelegationType delegationType, List<String> selectableFields) {
        this.id = id;
        this.description = description;
        this.inputs = inputs;
        this.outputs = outputs;
        this.delegateClass = delegateClass;
        this.category = category;
        this.delegationType = delegationType;
        this.selectableFields = selectableFields;
    }

    public OperationDescriptor(String id, String description, List<String> inputs, List<String> outputs,
            String delegateClass, String category, DelegationType delegationType, List<String> selectableFields, List<String> customizableFields) {
        this.id = id;
        this.description = description;
        this.inputs = inputs;
        this.outputs = outputs;
        this.delegateClass = delegateClass;
        this.category = category;
        this.delegationType = delegationType;
        this.selectableFields = selectableFields;
        this.customizableFields = customizableFields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getInputs() {
        return inputs;
    }

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }

    public String getDelegateClass() {
        return delegateClass;
    }

    public void setDelegateClass(String delegateClass) {
        this.delegateClass = delegateClass;
    }

    public List<String> getSelectableFields() {
        if (this.selectableFields == null) {
            selectableFields = new ArrayList<>();
        }
        return selectableFields;
    }

    public List<String> getCustomizableFields() {
        if (this.customizableFields == null) {
            customizableFields = new ArrayList<>();
        }
        return customizableFields;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public DelegationType getDelegationType() {
        return delegationType;
    }

    public void setDelegationType(DelegationType delegationType) {
        this.delegationType = delegationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OperationDescriptor that = (OperationDescriptor) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OperationDescriptor{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", inputs=" + inputs +
                ", outputs=" + outputs +
                ", category='" + category + '\'' +
                ", delegationType=" + delegationType +
                '}';
    }
}
