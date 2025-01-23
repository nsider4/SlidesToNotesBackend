package com.nsider.SlidesToNotes.model;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Represents the request payload for generating a DOCX file.
 * Contains the necessary information including the file name, image format, and content nodes.
 */
@Getter
@Setter
public class DocxRequest {

    /**
     * The name of the file to be generated. Must not be {@code null}.
     */
    @Nonnull
    private String fileName;

    /**
     * The desired image format (e.g., "png", "jpeg"). Must not be {@code null}.
     */
    @Nonnull
    private String imageFormat;

    /**
     * The list of content nodes to be included in the document. Must not be {@code null}.
     */
    @Nonnull
    private List<Node> nodes;

    /**
     * Represents a content node in the document.
     * A node can be a header, paragraph, or image depending on the type.
     */
    @Getter
    @Setter
    public static class Node {

        /**
         * The type of the node (e.g., "H2", "P", "IMG"). Must not be {@code null}.
         */
        @Nonnull
        private String type;

        /**
         * The text content for text nodes (optional for non-text nodes).
         */
        private String text;

        /**
         * The source URL for image nodes (optional for non-image nodes).
         */
        private String src;

        /**
         * The table rows for a table.
         */
        private List<List<String>> tableRows;
    }
}