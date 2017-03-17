package com.arteco.mvc.core;

import com.arteco.mvc.processor.PathRegister;
import com.arteco.mvc.utils.DateUtils;
import com.arteco.mvc.view.JstlViewResolver;
import com.arteco.mvc.view.ViewResolver;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by rarnau on 10/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public abstract class App {

    public static final String ATTR_KEY = "com.arteco.mvc.core.App";

    protected PathRegister register = new PathRegister();
    protected ObjectMapper jsonMapper = new ObjectMapper();

    private Date startDate = DateUtils.parse(DateUtils.format(new Date()));
    private boolean isDevel;

    public abstract Locale getDefaultLocale();

    public abstract void registerPaths(PathRegister register);

    protected abstract Collection<Locale> getAvailableLocales();

    protected ViewResolver getViewResolver() {
        return new JstlViewResolver();
    }


    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        registerPaths(register);
        ctx.setAttribute(ATTR_KEY, this);
    }

    public void before(HttpServletRequest req, HttpServletResponse res) {
    }

    public void after(HttpServletRequest req, HttpServletResponse res) {
    }

    public void error(HttpServletRequest req, HttpServletResponse res, Handler ctrl, Exception e) {
        e.printStackTrace();
    }

    public ObjectMapper getJsonMapper() {
        return jsonMapper;
    }


    public Converter getConverter() {
        return null;
    }

    public Locale getLocaleResolver(HttpServletRequest httpReq) {
        Enumeration<Locale> locales = httpReq.getLocales();
        Collection<Locale> availLocales = getAvailableLocales();
        while (locales.hasMoreElements()) {
            Locale locale = locales.nextElement();
            if (availLocales.contains(locale)) {
                return locale;
            }

        }
        return getDefaultLocale();
    }


    public String notfound(HttpServletRequest httpReq, HttpServletResponse httpRes) throws IOException {
        httpRes.setContentType("text/html");
        httpRes.getWriter().append("<html><body>Ops, Sorry this url was not found!</body></html>");
        return null;
    }


    public boolean isDevel() {
        return isDevel;
    }

    public void setDevel(boolean isDevel) {
        this.isDevel = isDevel;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getMessagesEncoding() {
        return "utf-8";
    }
}
