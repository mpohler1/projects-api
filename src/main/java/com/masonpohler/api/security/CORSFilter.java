package com.masonpohler.api.security;

import com.masonpohler.api.environment.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter implements Filter {
    private static final String APPLICATION_URL_VARIABLE_NAME = "APPLICATION_URL";

    @Autowired
    private Environment environment;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String applicationURL = environment.getEnv(APPLICATION_URL_VARIABLE_NAME);

        response.setHeader("Access-Control-Allow-Origin", applicationURL);
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "180");

        filterChain.doFilter(request, response);
    }
}
