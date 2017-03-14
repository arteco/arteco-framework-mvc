package com.arteco.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Developed by Arteco Consulting Sl.
 * Author rarnau on 11/11/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ReqVar {
    String value();
    boolean obligatory() default true;
    String defaultValue() default "";
}
