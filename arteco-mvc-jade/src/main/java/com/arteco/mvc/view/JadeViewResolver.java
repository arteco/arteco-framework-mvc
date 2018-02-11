package com.arteco.mvc.view;

import com.arteco.mvc.configuration.AppConfig;
import com.arteco.mvc.core.App;
import com.arteco.mvc.model.MimeType;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by mterrasa on 09/02/2018.
 * Arteco Consulting SL
 * mailto: info@arteco-consulting.com
 */
public class JadeViewResolver implements ViewResolver {

    private static final String TEMPLATE_FOLDER = "src/main/webapp/templates/";
    private static final String ENCODING = "UTF-8";
    private static final String EXTENSION = "jade";

    private JadeConfiguration config;

    @Override
    public void init(App app, AppConfig appConfig) {
        config = new JadeConfiguration();
        config.setTemplateLoader(new FileTemplateLoader(TEMPLATE_FOLDER, ENCODING, EXTENSION));
        config.setCaching(!app.isDevel());
    }

    @Override
    public void gotoView(App app, HttpServletRequest request, HttpServletResponse response, String view, Locale locale, ServletContext servletContext) throws IOException {
        response.setContentType(MimeType.HTML.getMimeType());
        response.setCharacterEncoding(ENCODING);

        JadeTemplate template = config.getTemplate(view);

        config.renderTemplate(template, propagateAttributes(request), response.getWriter());
    }

    private Map<String, Object> propagateAttributes(HttpServletRequest request){
        Map<String, Object> attributes = new HashMap<String, Object>();

        Enumeration attrs =  request.getAttributeNames();
        while(attrs.hasMoreElements()) {
            String name = (String) attrs.nextElement();
            attributes.put(name, request.getAttribute(name));
        }

        attributes.put("contextPath", request.getContextPath());
        return attributes;
    }

}
