package com.nsider.SlidesToNotes.filter;

import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.utils.security.RateLimitingUtils;

/**
 * Servlet filter to apply rate limiting to incoming HTTP requests.
 * This filter ensures that clients do not exceed a predefined request rate.
 */
@Component
public class RateLimitingFilter implements Filter {

    @Autowired
    private RateLimitingUtils rateLimitingUtils;

    /**
     * Filters incoming requests, applying rate limiting logic.
     *
     * @param request  the incoming HTTP request, must not be {@code null}.
     * @param response the outgoing HTTP response, must not be {@code null}.
     * @param chain    the filter chain, must not be {@code null}.
     */
    @Override
    public void doFilter(
            @Nonnull ServletRequest request, 
            @Nonnull ServletResponse response, 
            @Nonnull FilterChain chain) {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        rateLimitingUtils.setCorsHeaders(httpResponse);

        if (rateLimitingUtils.isOptionsRequest(httpRequest)) {
            rateLimitingUtils.handleOptionsRequest(httpResponse);
            return;
        }

        //Retrieve or create a bucket for the client IP
        String clientIp = rateLimitingUtils.getClientIp(httpRequest);
        Bucket bucket = rateLimitingUtils.getBucketForClient(clientIp);

        //Process the request if within the rate limit
        if (bucket.tryConsume(1)) {
            rateLimitingUtils.processRequest(chain, request, response);
        } else {
            //Handle requests exceeding the rate limit
            rateLimitingUtils.handleRateLimitExceeded(httpResponse);
        }
    }
}