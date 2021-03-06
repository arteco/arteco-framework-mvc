package com.arteco.mvc.view;

import com.arteco.mvc.configuration.AppConfig;
import com.arteco.mvc.core.App;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by amalagraba on 08/03/2017.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
public interface ViewResolver {

    void init(App app, AppConfig appConfig);

    void gotoView(App app, HttpServletRequest httpReq, HttpServletResponse httpRes, String view, Locale locale,
                  ServletContext servletContext) throws IOException;
}
