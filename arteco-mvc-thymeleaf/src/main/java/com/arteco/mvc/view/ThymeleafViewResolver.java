package com.arteco.mvc.view;

import com.arteco.mvc.MessageResolver;
import com.arteco.mvc.configuration.AppConfig;
import com.arteco.mvc.core.App;
import com.arteco.mvc.model.MimeType;
import com.arteco.mvc.utils.SeoUtils;
import ognl.OgnlRuntime;
import org.apache.commons.lang.BooleanUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Created by amalagraba on 08/03/2017.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
public class ThymeleafViewResolver implements ViewResolver {

    private TemplateEngine templateEngine = new TemplateEngine();
    private ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

    public void init(App app, AppConfig appConfig) {
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(new MessageResolver(app,
                BooleanUtils.toBoolean(appConfig.getProperty("thymeleaf.messages.logUnresolved"))));
//        templateEngine.addDialect(new LayoutDialect());
        templateResolver.setTemplateMode(StandardTemplateModeHandlers.HTML5.getTemplateModeName());
        String prefix = appConfig.getProperty("thymeleaf.template.prefix");
        templateResolver.setPrefix(prefix != null ? prefix : "/templates/");
        String suffix = appConfig.getProperty("thymeleaf.template.suffix");
        templateResolver.setSuffix(suffix != null ? suffix : ".html");
        templateResolver.setCharacterEncoding("utf-8");
        templateResolver.setCacheable(true);
        OgnlRuntime.setSecurityManager(null);
    }

    public void gotoView(App app, HttpServletRequest httpReq, HttpServletResponse httpRes, String view, Locale locale,
                         ServletContext servletContext) throws IOException {
        if (view == null) {
            return;
        }
        httpRes.setContentType(MimeType.HTML.getMimeType());
        httpRes.setCharacterEncoding("utf-8");
        if (app.isDevel()) {
            templateResolver.setCacheable(false);
        }

        WebContext context = new WebContext(httpReq, httpRes, servletContext, locale);
        propagateAttributes(context, httpReq);
        this.templateEngine.process(view, context, httpRes.getWriter());
    }

    private void propagateAttributes(AbstractContext context, HttpServletRequest httpReq) {
        Enumeration enumer = httpReq.getAttributeNames();
        while (enumer.hasMoreElements()) {
            Object name = enumer.nextElement();
            context.setVariable((String) name, httpReq.getAttribute((String) name));
        }
        context.setVariable("seoUtils", new SeoUtils());
    }
}
