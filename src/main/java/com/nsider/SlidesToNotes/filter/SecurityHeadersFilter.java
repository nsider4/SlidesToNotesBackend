package com.nsider.SlidesToNotes.filter;

import org.springframework.stereotype.Component;

import com.nsider.SlidesToNotes.annotations.Nonnull;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to add security-related HTTP headers to responses.
 * This filter enhances the security of HTTP responses by setting headers like 
 * Content Security Policy, X-Content-Type-Options, and Strict Transport Security.
 */
@Component
public class SecurityHeadersFilter implements Filter {

    /**
     * Adds security headers to the HTTP response.
     *
     * @param request  the {@link ServletRequest} representing the client request.
     * @param response the {@link ServletResponse} representing the response to send to the client.
     * @param chain    the {@link FilterChain} to pass the request and response along to the next filter or resource.
     * @throws IOException      if an I/O error occurs during filtering.
     * @throws ServletException if a servlet-related error occurs during filtering.
     */
    @Override
    public void doFilter(@
            Nonnull ServletRequest request, 
            @Nonnull ServletResponse response, 
            @Nonnull FilterChain chain) throws IOException, ServletException {

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Content-Security-Policy", "default-src 'self'");
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        }
        chain.doFilter(request, response);
    }
}