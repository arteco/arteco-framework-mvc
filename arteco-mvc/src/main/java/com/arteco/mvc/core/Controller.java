package com.arteco.mvc.core;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by rarnau on 11/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public interface Controller {
	Object serve(App app, HttpServletRequest httpReq, HttpServletResponse httpRes) throws InvocationTargetException, IllegalAccessException;
}
