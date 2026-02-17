package com.iongroup.library.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WorkFlowOperation {

    String id();

    String description();

    String category();

    DelegationType type();

    String[] inputs() default {};

    String[] outputs() default {};

    String[] selectableFields() default {};

    String[] customizableFields() default {};
}
