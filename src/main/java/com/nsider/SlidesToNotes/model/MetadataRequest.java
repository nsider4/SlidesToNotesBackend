package com.nsider.SlidesToNotes.model;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.annotations.Nullable;
import lombok.Data;
import java.util.List;

/**
 * Represents the metadata request containing information for processing metadata.
 * <p>
 * This class holds the details for notes, image format, file name, and image URLs, which are used when creating
 * or processing metadata for a ZIP file.
 * </p>
 */
@Data
public class MetadataRequest {
    
    /**
     * The notes associated with the metadata request.
     * <p>
     * This field holds the textual notes as a string, typically representing the content of a presentation.
     * </p>
     */
    @Nonnull
    private String notes;
    
    /**
     * The format of the image to be used (e.g., PNG, JPG).
     * <p>
     * This field specifies the image format to which images should be converted during processing.
     * </p>
     */
    @Nonnull
    private String imageFormat;
    
    /**
     * The name of the file to be generated for the metadata.
     * <p>
     * This field holds the name that should be used for the output file, typically the name of the ZIP file.
     * </p>
     */
    @Nullable
    private String fileName;
    
    /**
     * A list of image URLs to be included in the metadata.
     * <p>
     * This field holds URLs to the images that should be included in the ZIP file alongside the notes.
     * </p>
     */
    @Nonnull
    private List<String> imageUrls;
}