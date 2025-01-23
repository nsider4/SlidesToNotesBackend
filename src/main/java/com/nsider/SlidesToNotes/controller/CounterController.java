package com.nsider.SlidesToNotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.nsider.SlidesToNotes.service.NoteCounterService;

/**
 * Controller for handling actions related to the note counter.
 * Provides an endpoint for retrieving the current total note count.
 */
@RestController
public class CounterController {

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Autowired
    private NoteCounterService noteCounterService;

    /**
     * Endpoint to retrieve the current total note count.
     * This returns the number of notes generated as a string.
     *
     * @return A ResponseEntity with the total number of notes or a forbidden status.
     */
    @GetMapping("/get-note-count")
    public ResponseEntity<String> getNoteCount(@RequestHeader("Origin") String origin) {
        if (!allowedOrigin.equals(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
        }
        long count = noteCounterService.getCounter();
        return ResponseEntity.ok(Long.toString(count));
    }
}