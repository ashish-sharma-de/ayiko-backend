package com.ayiko.backend.config;

import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Component
public class RawRequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String method = httpServletRequest.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            String body = readBody(httpServletRequest);
            System.out.println("Incoming " + method + " request to " + httpServletRequest.getRequestURI() + " with payload: " + body);
        }
        chain.doFilter(request, response);
    }

    private String readBody(HttpServletRequest request) {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (Exception e) {
            // Handle the exception or log it
        }
        return body.toString();
    }
}
