package com.arteco.mvc.processor;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.arteco.mvc.model.RequestVerb;
import org.junit.Test;

import com.arteco.mvc.controller.TestController;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by amalagraba on 20/03/2017.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
public class PathRegisterTest {

    @Test
    public void resolveControllerPathTest_1() {
        PathRegister register = new PathRegister();
        String controllerPath = register.resolveControllerPath(new TestController());
        String expectedPath = "/parentPath/path";

        assertEquals(controllerPath, expectedPath);
    }

    @Test
    public void methodHandlerHierarchyTest_1() {
        PathRegister register = new PathRegister();
        register.add(new TestController());

        // Mock request
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(RequestVerb.GET.toString());
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn("/parentPath/path/method");

        assertNotNull(register.resolve(request));
    }

    @Test
    public void methodHandlerHierarchyTest_2() {
        PathRegister register = new PathRegister();
        register.add(new TestController());

        // Mock request
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn(RequestVerb.GET.toString());
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn("/parentPath/path/test");

        assertNotNull(register.resolve(request));
    }
}
