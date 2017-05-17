package com.arteco.mvc.sample;


import com.arteco.mvc.annotation.BeforeMethod;
import com.arteco.mvc.annotation.ErrorHandler;
import com.arteco.mvc.annotation.RequestMethod;
import com.arteco.mvc.core.Model;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by rarnau on 10/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
@SuppressWarnings("unused")
public class IndexController {


    @RequestMethod("/")
    public String index() {
        return "index";
    }

    @RequestMethod("/redirect")
    public String redirect() {
        return "redirect:/aviso-legal";
    }

    @RequestMethod("/throws")
    public String throwable() {
        throw new IllegalArgumentException("Hi hi");
    }

    @RequestMethod("/aviso-legal")
    public String avisoLegal() {
        return "section/aviso-legal";
    }


    @BeforeMethod
    public void before(Model model, HttpServletRequest request) {
        System.out.println("Executing before method " + request.getRequestURI());
    }

    @ErrorHandler(IllegalArgumentException.class)
    public String catchException(Model model, HttpServletRequest request, Exception e, Throwable t) {
        System.out.println("Exception Cached " + request.getRequestURI());
        return "index";
    }
}
