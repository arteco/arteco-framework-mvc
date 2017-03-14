package com.arteco.mvc.view;

import com.arteco.mvc.configuration.AppConfig;
import com.arteco.mvc.core.App;
import org.apache.commons.lang.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by amalagraba on 08/03/2017.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
public class JstlViewResolver implements ViewResolver {


    public void init(App app, AppConfig appConfig) {

    }

    public void gotoView(App app, HttpServletRequest httpReq, HttpServletResponse httpRes, String view, Locale locale, ServletContext servletContext) throws IOException {
        try {
            if (!StringUtils.endsWith(view, "jsp")) {
                view = view + ".jsp";
            }
            view = "/WEB-INF/jsp/" + view;
            RequestDispatcher dispatcher = servletContext.getRequestDispatcher(view);
            dispatcher.forward(httpReq, httpRes);
        } catch (ServletException se) {
            throw new IOException(se.getMessage(), se);
        }
    }
}
