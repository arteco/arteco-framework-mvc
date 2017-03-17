package com.arteco.mvc.processor;


import com.arteco.mvc.core.Handler;
import com.arteco.mvc.model.PathExpression;
import com.arteco.mvc.model.RequestVerb;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


/**
 * Created by rarnau on 11/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public class MethodHandler extends AbstractMethodHandler implements Handler {

    private final RequestVerb verb;
    private final PathExpression expression;


    public MethodHandler(Object controller, Method method, RequestVerb verb, PathExpression exp, Method beforeMethod, Method afterMethod) {
        super(controller, method, afterMethod, beforeMethod);
        this.verb = verb;
        this.expression = exp;
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
