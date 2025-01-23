package com.nsider.SlidesToNotes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nsider.SlidesToNotes.repository.NoteCounterRepository;
import com.nsider.SlidesToNotes.utils.counter.NoteCounter;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to manage the note counter.
 * Provides methods to retrieve, increment, and create a counter if it does not exist.
 */
@Service
@Slf4j
public class NoteCounterService {

    private static final String COUNTER_ID = "note-counter"; // The ID for the counter in MongoDB

    @Autowired
    private NoteCounterRepository noteCounterRepository;

    /**
     * Retrieves the current counter value. Creates the counter if it does not exist.
     *
     * @return the current counter value.
     */
    public long getCounter() {
        NoteCounter counter = getOrCreateCounter();
        return counter.getCounter();
    }

    /**
     * Increments the counter and returns the new value.
     *
     * @return the incremented counter value.
     */
    public synchronized long incrementCounter() {
        NoteCounter counter = getOrCreateCounter();
        counter.setCounter(counter.getCounter() + 1);

        try {
            noteCounterRepository.save(counter);
        } catch (Exception e) {
            log.error("Failed to save counter: {}", e.getMessage());
            throw new RuntimeException("Could not increment counter", e);
        }

        return counter.getCounter();
    }

    /**
     * Retrieves the counter from the repository or creates a new one if not found.
     *
     * @return the existing or newly created counter.
     */
    private NoteCounter getOrCreateCounter() {
        return noteCounterRepository.findById(COUNTER_ID).orElseGet(() -> {
            NoteCounter newCounter = new NoteCounter();
            newCounter.setId(COUNTER_ID);

            try {
                noteCounterRepository.save(newCounter);
            } catch (Exception e) {
                log.error("Failed to create new counter: {}", e.getMessage());
                throw new RuntimeException("Could not create counter", e);
            }

            return newCounter;
        });
    }
}
