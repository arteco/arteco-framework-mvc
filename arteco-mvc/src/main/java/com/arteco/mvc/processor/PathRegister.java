package com.arteco.mvc.processor;


import com.arteco.mvc.annotation.AfterMethod;
import com.arteco.mvc.annotation.BeforeMethod;
import com.arteco.mvc.annotation.ErrorHandler;
import com.arteco.mvc.annotation.RequestMethod;
import com.arteco.mvc.core.Handler;
import com.arteco.mvc.model.PathExpression;
import com.arteco.mvc.model.RequestVerb;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by rarnau on 10/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public class PathRegister {

    private List<MethodHandler> handlers = new ArrayList<MethodHandler>();
    private Map<Object, List<ErrorMethodHandler>> errorHandlersByController = new HashMap<Object, List<ErrorMethodHandler>>();

    private Comparator<? super MethodHandler> comparator = new Comparator<MethodHandler>() {

        public int compare(MethodHandler o1, MethodHandler o2) {
            return o1.getPath().compareTo(o2.getPath());
        }

    };

    public Handler resolve(HttpServletRequest req) {
        for (MethodHandler handler : handlers) {
            if (handler.canHandle(req)) {
                return handler;
            }
        }
        return null;
    }

    public void add(Object ctrl) {
        if (ctrl != null) {
            Class<?> controllerClass = ctrl.getClass();
            Method[] methods = controllerClass.getMethods();
            Method beforeMethod = null;
            Method afterMethod = null;
            String ctrlPath = resolveControllerPath(ctrl);

            for (Method method : methods) {
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
                    appendHandler(ctrl, method, ann, beforeMethod, afterMethod, ctrlPath);
                }
            }
            for (Method method : methods) {
                ErrorHandler ann = method.getAnnotation(ErrorHandler.class);
                if (ann != null) {
                    appendErrorHandler(ctrl, method, ann);
                }
            }
            Collections.sort(handlers, comparator);
        }
    }


    private void appendErrorHandler(Object ctrl, Method method, ErrorHandler ann) {
        Class<? extends Throwable> baseType = ann.value();

        List<ErrorMethodHandler> errorHandlers = errorHandlersByController.get(ctrl);
        if (errorHandlers == null) {
            errorHandlers = new ArrayList<ErrorMethodHandler>();
            errorHandlersByController.put(ctrl, errorHandlers);
        }
        errorHandlers.add(new ErrorMethodHandler(ctrl, method, baseType));
    }

    private void appendHandler(Object ctrl, Method method, RequestMethod ann, Method beforeMethod, Method afterMethod, String ctrlPath) {
        String[] paths = ann.value();
        RequestVerb verb = ann.method();
        for (String path : paths) {
            PathExpression exp = new PathExpression(ctrlPath + addPathSlash(path));
            handlers.add(new MethodHandler(ctrl, method, verb, exp, beforeMethod, afterMethod));
        }
    }

    protected String resolveControllerPath(Object ctrl) {
        List<String> prefixList = new ArrayList<String>();
        Class<?> ctrlClass = ctrl.getClass();

        while(!Object.class.equals(ctrlClass)) {
            RequestMethod classAnn = ctrlClass.getAnnotation(RequestMethod.class);
            if (classAnn != null) {
                prefixList.add(classAnn.value()[0]);
            }
            ctrlClass = ctrlClass.getSuperclass();
        }
        String prefix = StringUtils.EMPTY;

        for (int i = prefixList.size() - 1; i >= 0; i--) {
            String value = StringUtils.removeEnd(prefixList.get(i), "/");
            prefix += addPathSlash(value);
        }
        return prefix;
    }

    private String addPathSlash(String path) {
        return path.startsWith("/") ? path : ("/" + path);
    }

    public Map<Object, List<ErrorMethodHandler>> getErrorHandlersByController() {
        return errorHandlersByController;
    }
}
