package com.nsider.SlidesToNotes.utils.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nsider.SlidesToNotes.annotations.Nonnull;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling rate limiting and CORS configuration in the application.
 */
@Component
public class RateLimitingUtils {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingUtils.class);

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Value("${rate.limit.requests}")
    private int rateLimitRequests;

    @Value("${rate.limit.duration.minutes}")
    private int rateLimitDuration;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Sets CORS headers for the HTTP response.
     *
     * @param httpResponse the HTTP response to configure, must not be {@code null}.
     */
    public void setCorsHeaders(@Nonnull HttpServletResponse httpResponse) {
        httpResponse.setHeader("Access-Control-Allow-Origin", allowedOrigin);
        httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        logger.debug("CORS headers set for allowed origin: {}", allowedOrigin);
    }

    /**
     * Checks if the incoming HTTP request is an OPTIONS request.
     *
     * @param httpRequest the HTTP request to check, must not be {@code null}.
     * @return {@code true} if the request is an OPTIONS request, {@code false} otherwise.
     */
    public boolean isOptionsRequest(@Nonnull HttpServletRequest httpRequest) {
        return "OPTIONS".equalsIgnoreCase(httpRequest.getMethod());
    }

    /**
     * Handles an OPTIONS HTTP request by setting the status to OK (200).
     *
     * @param httpResponse the HTTP response to configure, must not be {@code null}.
     */
    public void handleOptionsRequest(@Nonnull HttpServletResponse httpResponse) {
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        logger.debug("Handled OPTIONS request.");
    }

    /**
     * Retrieves or creates a rate-limiting bucket for a specific client IP.
     *
     * @param clientIp the client's IP address, must not be {@code null}.
     * @return the rate-limiting bucket for the client.
     */
    @Nonnull
    public Bucket getBucketForClient(@Nonnull String clientIp) {
        return buckets.computeIfAbsent(clientIp, this::createBucket);
    }

    /**
     * Processes an HTTP request by passing it through the filter chain.
     *
     * @param chain    the filter chain, must not be {@code null}.
     * @param request  the incoming HTTP request, must not be {@code null}.
     * @param response the outgoing HTTP response, must not be {@code null}.
     */
    public void processRequest(
            @Nonnull FilterChain chain, 
            @Nonnull ServletRequest request, 
            @Nonnull ServletResponse response) {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("Error processing request", e);
        }
    }

    /**
     * Handles requests that exceed the rate limit by returning a 429 status.
     *
     * @param httpResponse the HTTP response to configure, must not be {@code null}.
     */
    public void handleRateLimitExceeded(@Nonnull HttpServletResponse httpResponse) {
        httpResponse.setStatus(429);
        try {
            httpResponse.getWriter().write("Too many requests - please try again later.");
        } catch (IOException e) {
            logger.error("Error writing rate-limit response", e);
        }
    }

    /**
     * Creates a new rate-limiting bucket for a specific client IP.
     *
     * @param clientIp the client's IP address.
     * @return the newly created rate-limiting bucket.
     */
    private Bucket createBucket(@Nonnull String clientIp) {
        logger.debug("Creating rate-limiting bucket for IP: {}", clientIp);
        return Bucket.builder()
                .addLimit(Bandwidth.simple(rateLimitRequests, Duration.ofMinutes(rateLimitDuration)))
                .build();
    }

    /**
     * Retrieves the client IP address from the HTTP request.
     *
     * @param request the HTTP request, must not be {@code null}.
     * @return the client's IP address.
     */
    @Nonnull
    public String getClientIp(@Nonnull HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        String clientIp = (forwarded != null) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
        logger.debug("Client IP resolved as: {}", clientIp);
        return clientIp;
    }
}
