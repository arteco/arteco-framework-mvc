package com.arteco.mvc.core;

import com.arteco.mvc.configuration.AppConfig;
import com.arteco.mvc.model.MimeType;
import com.arteco.mvc.processor.ErrorMethodHandler;
import com.arteco.mvc.utils.DateUtils;
import com.arteco.mvc.utils.SeoUtils;
import com.arteco.mvc.view.ViewResolver;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

//import javax.servlet.annotation.WebFilter;

/**
 * Created by rarnau on 10/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
//@WebFilter(filterName = "MainFilter", urlPatterns = "/*")
public class MainFilter implements Filter {

    public static final String LOCALE_KEY = "locale";
    public static final String LANGUAGE_KEY = "language";
    public static final String REDIRECT_PREFIX = "redirect:";

    private Map<String, byte[]> resourceCache = new HashMap<String, byte[]>();
    private ServletContext servletContext;
    private ViewResolver viewResolver;
    private AppConfig appConfig;
    private App app;

    public void init(FilterConfig config) throws ServletException {
        init(config.getServletContext());
    }

    private void init(ServletContext servletContext) throws ServletException {
        app = (App) servletContext.getAttribute(App.ATTR_KEY);

        if (app == null) {
            String className = servletContext.getInitParameter("mainFilterApp");
            if (StringUtils.isEmpty(className)) {
                throw new ServletException("No init Parameter [mainFilterApp] set with class extending " + App.class.getName());
            }

            System.out.println("Starting App " + className);
            try {
                app = (App) Class.forName(className).newInstance();
                app.onStartup(null, servletContext);
            } catch (Exception e) {
                throw new ServletException(e.getMessage(), e);
            }
        }
        this.appConfig = new AppConfig();
        this.servletContext = servletContext;
        this.viewResolver = app.getViewResolver();
        this.viewResolver.init(app, appConfig);
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest _req, ServletResponse _resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpReq = (HttpServletRequest) _req;
        HttpServletResponse httpRes = (HttpServletResponse) _resp;

        Handler ctrl = app.register.resolve(httpReq);
        Locale locale = app.getLocaleResolver(httpReq);
        if (ctrl != null) {
            try {
                Object result;
                httpReq.setAttribute(LOCALE_KEY, locale);
                httpReq.setAttribute(LANGUAGE_KEY, locale.getLanguage());

                try {
                    result = ctrl.serve(app, httpReq, httpRes, null);
                } catch (Throwable e) {
                    result = handleError(httpReq, httpRes, ctrl, e);
                }

                if (result instanceof String) {
                    serveRedirectOrView(app, httpReq, httpRes, (String) result, locale);
                } else if (result != null) {
                    serveJsonObject(app, httpRes, result);
                }
            } catch (Throwable e) {
                app.error(httpReq, httpRes, ctrl, e);
            }
        } else {
            String uri = SeoUtils.getUriWithouContextPath(httpReq);
            InputStream is = getStaticResource(uri);
            if (is != null) {
                serverResource(httpReq, httpRes, app, is, uri);
            } else {
                httpRes.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String view = app.notfound(httpReq, httpRes);
                if (view != null) {
                    serveRedirectOrView(app, httpReq, httpRes, view, locale);
                }
            }
        }
    }

    private Object handleError(HttpServletRequest httpReq, HttpServletResponse httpRes, Handler handler, Throwable e) throws Throwable {
        Object result = null;
        Object controller = handler.getController();
        List<ErrorMethodHandler> errorHandlers = app.register.getErrorHandlersByController().get(controller);
        boolean found = false;
        if (errorHandlers != null) {
            for (ErrorMethodHandler errHandler : errorHandlers) {
                if (errHandler.canHandle(e)) {
                    found = true;
                    result = errHandler.serve(app, httpReq, httpRes, e);
                }
            }
        }
        if (!found) {
            throw e;
        }
        return result;
    }

    private InputStream getStaticResource(String realUri) throws IOException {
        InputStream is = null;
        byte[] array = resourceCache.get(realUri);
        if (app.isDevel() || !resourceCache.containsKey(realUri)) {
            is = this.getClass().getResourceAsStream("/static" + realUri);
            if (is == null && !realUri.contains("/WEB_INF")) {
                is = servletContext.getResourceAsStream(realUri);
            }
            if (is != null) {
                array = IOUtils.toByteArray(is);
            }
            resourceCache.put(realUri, array);
        }

        if (array != null) {
            is = new ByteArrayInputStream(array);
        }
        return is;
    }

    private void serverResource(HttpServletRequest httpReq, HttpServletResponse httpRes, App app, InputStream is, String resourceFileName) throws IOException {
        boolean serveRes = true;
        String navigatorLastMod = httpReq.getHeader("If-Modified-Since");
        if (navigatorLastMod != null && !app.isDevel()) {
            Date navigatorLastModDate = DateUtils.parse(navigatorLastMod);
            if (navigatorLastModDate != null && navigatorLastModDate.getTime() >= app.getStartDate().getTime()) {
                serveRes = false;
            }
        }
        if (serveRes) {
            String content = MimeType.getMimeByName(StringUtils.substringAfterLast(resourceFileName, "."));
            httpRes.setHeader("Last-Modified", DateUtils.format(app.getStartDate()));
            httpRes.setContentType(content);
            IOUtils.copy(is, httpRes.getOutputStream());
        } else {
            httpRes.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
    }

    private void serveRedirectOrView(App app, HttpServletRequest httpReq, HttpServletResponse httpRes, String view, Locale locale) throws IOException {
        if (view.startsWith(REDIRECT_PREFIX)) {
            String to = StringUtils.substringAfter(view, REDIRECT_PREFIX);

            // añade el context path si el controlador devolvió una url relativa
            if (to.charAt(0) == '/' && to.charAt(1) != '/') {
                String ctxPath = httpReq.getContextPath();
                if (StringUtils.length(ctxPath) > 0 && !StringUtils.startsWith(to, ctxPath)) {
                    to = ctxPath + to;
                }
            }

            httpRes.sendRedirect(to);
        } else if (view.startsWith("notfound")) {
            view = app.notfound(httpReq, httpRes);
            viewResolver.gotoView(app, httpReq, httpRes, view, locale, servletContext);
        } else {
            viewResolver.gotoView(app, httpReq, httpRes, view, locale, servletContext);
        }
    }

    private void serveJsonObject(App app, HttpServletResponse httpRes, Object result) throws IOException {
        ObjectMapper mapper = app.getJsonMapper();
        httpRes.setContentType(MimeType.JSON.getMimeType());
        OutputStream os = httpRes.getOutputStream();
        mapper.writeValue(os, result);
    }

}
