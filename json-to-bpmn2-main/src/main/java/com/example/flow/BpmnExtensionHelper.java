package com.example.flow;

import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;

public final class BpmnExtensionHelper {

    private BpmnExtensionHelper() {}

    public static void addStringExtension(
            FlowElement element,
            String name,
            String value
    ) {
        if (value == null || value.isBlank()) return;

        ExtensionElement ext = new ExtensionElement();
        ext.setName(name);
        ext.setNamespace("http://flowable.org/bpmn");
        ext.setNamespacePrefix("flowable");
        ext.setElementText(value);

        // ✅ CORRECT Flowable API
        element.addExtensionElement(ext);
    }

    public static void addCsvExtension(
            FlowElement element,
            String name,
            Iterable<String> values
    ) {
        String joined = String.join(",", values);
        addStringExtension(element, name, joined);
    }
}
