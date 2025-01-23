package com.nsider.SlidesToNotes.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.utils.counter.NoteCounter;

/**
 * Repository interface for accessing and manipulating NoteCounter entities in the MongoDB database.
 * This extends the MongoRepository interface, providing built-in CRUD operations.
 */
public interface NoteCounterRepository extends MongoRepository<NoteCounter, String> {

    /**
     * Finds a NoteCounter entity by its ID.
     * 
     * @param id The ID of the NoteCounter to find, must not be {@code null}.
     * @return An Optional containing the found NoteCounter, or an empty Optional if no entity is found.
     */
    Optional<NoteCounter> findById(@Nonnull String id);
}