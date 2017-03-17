package com.arteco.mvc.processor;


import com.arteco.mvc.core.Handler;

import java.lang.reflect.Method;


/**
 * Created by rarnau on 11/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public class ErrorMethodHandler extends AbstractMethodHandler implements Handler {


    private final Class<? extends Throwable> baseType;

    public ErrorMethodHandler(Object controller, Method method, Class<? extends Throwable> baseType) {
        super(controller, method, null, null);
        this.baseType = baseType;
    }


    @Override
    public String toString() {
        return "ErrorMethodHandler{" +
                "baseType=" + baseType +
                '}';
    }

    public boolean canHandle(Throwable throwable) {
        return throwable != null && baseType.isAssignableFrom(throwable.getClass());
    }
}
