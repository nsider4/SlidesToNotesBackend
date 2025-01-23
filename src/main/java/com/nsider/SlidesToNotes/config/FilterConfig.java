package com.nsider.SlidesToNotes.config;

import com.nsider.SlidesToNotes.filter.RateLimitingFilter;
import com.nsider.SlidesToNotes.filter.SecurityHeadersFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nsider.SlidesToNotes.annotations.Nonnull;

/**
 * Configuration class for registering servlet filters.
 */
@Configuration
public class FilterConfig {

    /**
     * Registers the {@link RateLimitingFilter} to intercept requests to API endpoints.
     *
     * @param rateLimitingFilter the rate limiting filter to be registered, must not be {@code null}.
     * @return the filter registration bean for {@link RateLimitingFilter}.
     */
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration(@Nonnull RateLimitingFilter rateLimitingFilter) {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(rateLimitingFilter);
        registration.addUrlPatterns("/api/*");
        return registration;
    }

    /**
     * Registers the {@link SecurityHeadersFilter} to add security headers to all HTTP responses.
     *
     * @return the filter registration bean for {@link SecurityHeadersFilter}.
     */
    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilterRegistration() {
        FilterRegistrationBean<SecurityHeadersFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SecurityHeadersFilter());
        registration.addUrlPatterns("/api/*");
        return registration;
    }
}
