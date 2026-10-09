package com.doritech.api.Entity;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;

@Component
public class JspRenderer {

    private final ServletContext servletContext;

    public JspRenderer(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String renderJspToString(String jspPath, HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringWriter writer = new StringWriter();
        CustomResponseWrapper responseWrapper = new CustomResponseWrapper(response, writer);

        RequestDispatcher dispatcher = servletContext.getRequestDispatcher(jspPath);
        dispatcher.include(request, responseWrapper);

        return writer.toString();
    }
}
