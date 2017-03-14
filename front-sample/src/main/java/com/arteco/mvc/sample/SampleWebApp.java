package com.arteco.mvc.sample;


import com.arteco.mvc.core.App;
import com.arteco.mvc.processor.PathRegister;
import com.arteco.mvc.view.JstlViewResolver;
import com.arteco.mvc.view.ViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by rarnau on 10/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public class SampleWebApp extends App {


    @Override
    public Locale getDefaultLocale() {
        return new Locale("es");
    }

    @Override
    public void registerPaths(PathRegister register) {
        register.add(new IndexController());
    }

    @Override
    protected Collection<Locale> getAvailableLocales() {
        return Collections.singleton(getDefaultLocale());
    }

    @Override
    protected ViewResolver getViewResolver() {
        return new JstlViewResolver();
    }

    @Override
    public String notfound(HttpServletRequest httpReq, HttpServletResponse httpRes) throws IOException {
        return "notfound";
    }
}
