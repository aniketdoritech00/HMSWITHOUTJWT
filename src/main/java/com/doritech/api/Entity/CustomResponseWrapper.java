package com.doritech.api.Entity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CustomResponseWrapper extends HttpServletResponseWrapper {

    private final StringWriter stringWriter;

    public CustomResponseWrapper(HttpServletResponse response, StringWriter writer) {
        super(response);
        this.stringWriter = writer;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(stringWriter);
    }

    public String getOutput() {
        return stringWriter.toString();
    }
}
 	