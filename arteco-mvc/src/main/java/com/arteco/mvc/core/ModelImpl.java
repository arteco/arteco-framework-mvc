package com.arteco.mvc.core;

import javax.servlet.http.HttpServletRequest;

/**
 * Developed by Arteco Consulting Sl.
 * Author rarnau on 2/12/16.
 */
public class ModelImpl implements Model{

    private final HttpServletRequest httpReq;

    public ModelImpl(HttpServletRequest httpReq) {
        this.httpReq = httpReq;
    }

    public void addAttribute(String name, Object value) {
        httpReq.setAttribute(name, value);
    }
}
