package com.nsider.SlidesToNotes.utils.counter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nsider.SlidesToNotes.annotations.Nonnull;

/**
 * Represents a note counter entity stored in MongoDB.
 * This class is used to track a counter for notes.
 */
@Document(collection = "note_counter")
public class NoteCounter {

    @Id
    private String id;
    private long counter;

    /**
     * Default constructor initializes the counter to 0.
     */
    public NoteCounter() {
        this.counter = 0;
    }

    /**
     * Constructor to initialize the counter with a specific value.
     *
     * @param counter the initial value of the counter.
     */
    public NoteCounter(long counter) {
        this.counter = counter;
    }

    /**
     * Gets the ID of the note counter.
     *
     * @return the ID of the note counter.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the note counter.
     *
     * @param id the new ID of the note counter.
     */
    public void setId(@Nonnull String id) {
        this.id = id;
    }

    /**
     * Gets the current counter value.
     *
     * @return the current counter value.
     */
    public long getCounter() {
        return counter;
    }

    /**
     * Sets the counter value.
     *
     * @param counter the new counter value.
     */
    public void setCounter(long counter) {
        this.counter = counter;
    }
}