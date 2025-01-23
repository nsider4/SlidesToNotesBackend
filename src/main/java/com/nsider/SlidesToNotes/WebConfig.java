package com.nsider.SlidesToNotes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nsider.SlidesToNotes.annotations.Nonnull;

/**
 * Configuration class for setting up CORS (Cross-Origin Resource Sharing) policies.
 * <p>
 * This class configures which origins are allowed to make requests to the backend,
 * the HTTP methods permitted, allowed headers, and whether credentials like cookies are allowed.
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    /**
     * Adds custom CORS mappings for the application.
     * <p>
     * The configuration allows cross-origin requests from the specified frontend URL,
     * enables the listed HTTP methods, allows all headers, and permits credentials.
     * </p>
     *
     * @param registry the CORS registry to configure
     */
    @Override
    public void addCorsMappings(@Nonnull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigin)  //allowedOrigin value from properties
                .allowedMethods("GET", "POST", "PUT", "DELETE")  //Allowed HTTP methods
                .allowedHeaders("*")  //Allow all headers
                .allowCredentials(true);  //Allow credentials (like cookies)
    }
}