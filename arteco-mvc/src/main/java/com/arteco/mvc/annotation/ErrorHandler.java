package com.arteco.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rarnau on 11/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ErrorHandler {
    Class<? extends Throwable> value() default Throwable.class;
}
