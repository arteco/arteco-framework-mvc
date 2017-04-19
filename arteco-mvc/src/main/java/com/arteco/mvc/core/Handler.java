package com.arteco.mvc.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by rarnau on 11/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public interface Handler {
    Object serve(App app, HttpServletRequest httpReq, HttpServletResponse httpRes) throws Throwable;

    Object getController();
}
