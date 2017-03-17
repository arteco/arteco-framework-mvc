package com.arteco.mvc.processor;

import com.arteco.mvc.annotation.PathVar;
import com.arteco.mvc.annotation.ReqVar;
import com.arteco.mvc.core.App;
import com.arteco.mvc.core.Converter;
import com.arteco.mvc.core.Model;
import com.arteco.mvc.core.ModelImpl;
import com.arteco.mvc.model.PathExpression;
import org.apache.commons.beanutils.ConvertUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Developed by Arteco Consulting Sl.
 * Author rarnau on 17/3/17.
 */
public abstract class AbstractMethodHandler {

    private final Method method;
    private final Object controller;
    private final Method afterMethod;
    private final Method beforeMethod;

    public AbstractMethodHandler(Object controller, Method method, Method afterMethod, Method beforeMethod) {
        this.controller = controller;
        this.method = method;
        this.afterMethod = afterMethod;
        this.beforeMethod = beforeMethod;
    }

    public Object serve(App app, HttpServletRequest httpReq, HttpServletResponse httpRes) throws InvocationTargetException, IllegalAccessException {
        try {
            Object result = invokeMethod(app, beforeMethod, httpReq, httpRes);
            if (result != null) {
                return result;
            }
            return invokeMethod(app, method, httpReq, httpRes);
        } finally {
            invokeMethod(app, afterMethod, httpReq, httpRes);
        }
    }

    private Object invokeMethod(App app, Method method, HttpServletRequest httpReq, HttpServletResponse httpRes) throws IllegalAccessException, InvocationTargetException {
        if (method == null) {
            return null;
        }
        Class<?>[] argTypes = method.getParameterTypes();
        Annotation[][] argAnns = method.getParameterAnnotations();
        Object[] args = new Object[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            Class<?> targetClass = argTypes[i];
            Annotation[] annotations = argAnns[i];
            if (annotations != null) {
                handleAnnotateParams(app, httpReq, args, i, targetClass, annotations);
            }
            handleKnownTypesParams(targetClass, args, i, httpReq, httpRes);
        }
        try {
            return method.invoke(controller, args);
        } catch (InvocationTargetException ite) {
            Throwable targetExcep = ite.getTargetException();
            if (targetExcep instanceof RuntimeException) {
                throw ((RuntimeException) targetExcep);
            }
            throw ite;
        }
    }

    private void handleKnownTypesParams(Class<?> targetClass, Object[] args, int i, HttpServletRequest httpReq, HttpServletResponse httpRes) {
        Object value = null;
        if (targetClass.isAssignableFrom(HttpServletRequest.class)) {
            value = httpReq;
        } else if (targetClass.isAssignableFrom(HttpServletResponse.class)) {
            value = httpRes;
        } else if (targetClass.isAssignableFrom(Model.class)) {
            value = new ModelImpl(httpReq);
        }
        if (value != null) {
            args[i] = value;
        }
    }

    private void handleAnnotateParams(App app, HttpServletRequest httpReq, Object[] args, int i, Class<?> targetClass, Annotation[] annotations) {
        for (Annotation ann : annotations) {
            Object value = null;
            if (ann instanceof PathVar) {
                value = getPathParam(app, httpReq, targetClass, (PathVar) ann);
            } else if (ann instanceof ReqVar) {
                value = getRequestParam(app, httpReq, targetClass, (ReqVar) ann);
            }
            if (value != null) {
                args[i] = value;
            }
        }
    }


    private Object getPathParam(App app, HttpServletRequest httpReq, Class<?> targetClass, PathVar ann) {
        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) httpReq.getAttribute(PathExpression.ATTR_KEY);
        if (map != null) {
            Object value = map.get(ann.value());
            return convertValue(app, targetClass, value);
        }
        return null;
    }

    private Object getRequestParam(App app, HttpServletRequest httpReq, Class<?> targetClass, ReqVar ann) {
        Object value;

        if (targetClass.isArray()) {
            Class<?> type = targetClass.getComponentType();
            String[] values = httpReq.getParameterValues(ann.value());
            if (values != null) {
                Object[] arr = (Object[]) Array.newInstance(type, values.length);
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = convertValue(app, type, values[i]);
                }
                return arr;
            }
            return null;
        } else {
            value = httpReq.getParameter(ann.value());
            if (value == null) {
                if (ann.obligatory()) {
                    throw new IllegalArgumentException("Parameter " + (ann.value()) + " is null");
                } else {
                    value = ann.defaultValue();
                }
            }
            value = convertValue(app, targetClass, value);
            return value;
        }
    }

    private Object convertValue(App app, Class<?> targetClass, Object value) {
        if (value != null && !targetClass.isAssignableFrom(value.getClass())) {
            Converter converter = app.getConverter();
            if (converter != null) {
                value = converter.convert(value, targetClass);
            } else {
                value = ConvertUtils.convert(value, targetClass);
            }
        }
        return value;
    }

    public Object getController() {
        return controller;
    }
}
