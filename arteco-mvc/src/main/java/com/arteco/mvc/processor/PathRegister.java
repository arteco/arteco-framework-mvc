package com.arteco.mvc.processor;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.arteco.mvc.annotation.AfterMethod;
import com.arteco.mvc.annotation.BeforeMethod;
import com.arteco.mvc.annotation.RequestMethod;
import com.arteco.mvc.model.PathExpression;
import com.arteco.mvc.model.RequestVerb;
import com.arteco.mvc.core.Controller;

/**
 * Created by rarnau on 10/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public class PathRegister {

	private List<MethodHandler> handlers = new ArrayList<MethodHandler>();

	private Comparator<? super MethodHandler> comparator = new Comparator<MethodHandler>() {

		public int compare(MethodHandler o1, MethodHandler o2) {
			return o1.getPath().compareTo(o2.getPath());
		}

	};

	public Controller resolve(HttpServletRequest req) {
		for (MethodHandler handler : handlers) {
			if (handler.canHandle(req)) {
				return handler;
			}
		}
		return null;
	}

	public void add(Object ctrl) {
		if (ctrl != null) {
			Method[] methods = ctrl.getClass().getMethods();
			Method beforeMethod = null;
			Method afterMethod = null;
			for (Method method : methods){
				BeforeMethod bann = method.getAnnotation(BeforeMethod.class);
				if (bann != null) {
					if (beforeMethod != null) {
						throw new IllegalArgumentException("Only one @BeforeMethod allowed per Class");
					}
					beforeMethod = method;
				}

				AfterMethod aann = method.getAnnotation(AfterMethod.class);
				if (aann != null) {
					if (afterMethod != null) {
						throw new IllegalArgumentException("Only one @AfterMethod allowed per Class");
					}
					afterMethod = method;
				}
			}
			for (Method method : methods) {
				RequestMethod ann = method.getAnnotation(RequestMethod.class);
				if (ann != null) {
					appendHandler(ctrl, method, ann, beforeMethod, afterMethod);
				}
			}
			Collections.sort(handlers, comparator);
		}
	}

	private void appendHandler(Object ctrl, Method method, RequestMethod ann, Method beforeMethod, Method afterMethod) {
		String[] paths = ann.value();
		RequestVerb verb = ann.method();
		for (String path : paths) {
			PathExpression exp = new PathExpression(path);
			handlers.add(new MethodHandler(ctrl, method, verb, exp, beforeMethod, afterMethod));
		}
	}
}
