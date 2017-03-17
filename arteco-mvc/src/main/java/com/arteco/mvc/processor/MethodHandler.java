package com.arteco.mvc.processor;


import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.arteco.mvc.annotation.PathVar;
import com.arteco.mvc.annotation.ReqVar;
import com.arteco.mvc.core.App;
import com.arteco.mvc.core.Controller;
import com.arteco.mvc.core.Converter;
import com.arteco.mvc.core.Model;
import com.arteco.mvc.core.ModelImpl;
import com.arteco.mvc.model.PathExpression;
import com.arteco.mvc.model.RequestVerb;
import org.apache.commons.beanutils.ConvertUtils;


/**
 * Created by rarnau on 11/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public class MethodHandler implements Controller {

	private final Method method;
	private final Object controller;
	private final RequestVerb verb;
	private final PathExpression expression;
	private final Method afterMethod;
	private final Method beforeMethod;

	public MethodHandler(Object controller, Method method, RequestVerb verb, PathExpression exp, Method beforeMethod, Method afterMethod) {
		this.controller = controller;
		this.method = method;
		this.verb = verb;
		this.expression = exp;
		this.beforeMethod = beforeMethod;
		this.afterMethod = afterMethod;
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
		if (method == null){
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
		return method.invoke(controller, args);
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

		if (targetClass.isArray()){
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

	public String getPath() {
		return expression.getPath();
	}

	@Override
	public String toString() {
		return "MethodHandler{" +
				"verb=" + verb +
				", expression=" + expression +
				'}';
	}

	public boolean canHandle(HttpServletRequest req) {
		return verb.is(req.getMethod()) && expression.match(req);
	}
}
