package com.nsider.SlidesToNotes.utils.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.github.bucket4j.Bucket;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the RateLimitingUtils service that integrates with CORS configuration.
 * This class uses Spring's context to load beans and test the interaction between rate-limiting and CORS policies.
 */
@SpringBootTest
class RateLimitingUtilsTest {

    @Autowired
    private RateLimitingUtils rateLimitingUtils;

    @MockBean
    private HttpServletRequest httpServletRequest;

    @MockBean
    private HttpServletResponse httpServletResponse;

    @MockBean
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test for setting CORS headers.
     * This verifies that the CORS headers are correctly set in the HTTP response.
     */
    @Test
    void testSetCorsHeaders() {
        rateLimitingUtils.setCorsHeaders(httpServletResponse);

        //Assert: Verify that the correct CORS headers are set
        verify(httpServletResponse).setHeader("Access-Control-Allow-Origin", "https://slides-to-notes.vercel.app");
        verify(httpServletResponse).setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        verify(httpServletResponse).setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    /**
     * Test for handling OPTIONS request.
     * Verifies that the response status is set to HTTP 200 when an OPTIONS request is received.
     */
    @Test
    void testHandleOptionsRequest() {
        rateLimitingUtils.handleOptionsRequest(httpServletResponse);

        //Assert: Verify that the OPTIONS request is handled by setting status to 200 (OK)
        verify(httpServletResponse).setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Test for checking if the HTTP request is an OPTIONS request.
     * This simulates a check to see if the request method is OPTIONS.
     */
    @Test
    void testIsOptionsRequest() {
        when(httpServletRequest.getMethod()).thenReturn("OPTIONS");

        boolean result = rateLimitingUtils.isOptionsRequest(httpServletRequest);

        assertTrue(result, "Should return true for OPTIONS request");
    }

    /**
     * Test for processing an HTTP request.
     * This verifies that the FilterChain is correctly processed when a request is passed through.
     */
    @Test
    void testProcessRequest() throws Exception {
        rateLimitingUtils.processRequest(filterChain, httpServletRequest, httpServletResponse);

        //Assert: Verify that the request is passed through the filter chain
        verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * Test for handling rate-limit exceeded scenario.
     * Verifies that the response status is set to 429 when the rate limit is exceeded.
     */
    @Test
    void testHandleRateLimitExceeded() throws Exception {
        //Arrange: Mock the getWriter method to avoid NullPointerException
        PrintWriter printWriter = mock(PrintWriter.class);
        when(httpServletResponse.getWriter()).thenReturn(printWriter);

        rateLimitingUtils.handleRateLimitExceeded(httpServletResponse);

        //Assert: Verify that the status is set to 429 and the appropriate message is written
        verify(httpServletResponse).setStatus(429);
        verify(printWriter).write("Too many requests - please try again later.");
    }

    /**
     * Test for retrieving the client IP address.
     * This test simulates the presence of the "X-Forwarded-For" header and the fallback to remote address if it's missing.
     */
    @Test
    void testGetClientIpWithForwardedHeader() {
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn("192.168.0.1");

        String clientIp = rateLimitingUtils.getClientIp(httpServletRequest);

        assertEquals("192.168.0.1", clientIp, "Should return the forwarded IP address");
    }

    @Test
    void testGetClientIpWithoutForwardedHeader() {
        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpServletRequest.getRemoteAddr()).thenReturn("192.168.0.2");

        String clientIp = rateLimitingUtils.getClientIp(httpServletRequest);


        assertEquals("192.168.0.2", clientIp, "Should return the remote address if no forwarded header is present");
    }

    /**
     * Test for rate-limiting bucket creation.
     * Verifies that a rate-limiting bucket is created for a client IP address.
     */
    @Test
    void testGetBucketForClient() {
        String clientIp = "192.168.0.1";

        Bucket bucket = rateLimitingUtils.getBucketForClient(clientIp);

        assertNotNull(bucket, "The rate-limiting bucket should not be null");
    }
}