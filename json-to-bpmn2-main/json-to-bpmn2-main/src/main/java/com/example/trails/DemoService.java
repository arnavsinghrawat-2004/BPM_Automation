package com.example.trails;

import org.flowable.engine.delegate.DelegateExecution;

public class DemoService {
    /**
     * This is the exact method your BPMN calls via ${demoService.run(execution, ...)}.
     * It returns a String which will be stored in the service task's result variable.
     */
    public String run(DelegateExecution execution, Object inputName) {
        String name = inputName == null ? "unknown" : String.valueOf(inputName);
        System.out.println("[DemoService] run() invoked with name=" + name +
                           ", processInstanceId=" + execution.getProcessInstanceId());
        // You can also set process variables if desired:
        execution.setVariable("greeting", "Hello, " + name + "!");
        return "OK-" + name;
    }
}