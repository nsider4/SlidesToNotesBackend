package com.nsider.SlidesToNotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * The main entry point for the Spring Boot application.
 * <p>
 * This class initializes and runs the Spring Boot application using the {@link SpringApplication#run(Class, String[])} method.
 * </p>
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SlidesToNotesApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SlidesToNotesApplication.class, args);
    }
}