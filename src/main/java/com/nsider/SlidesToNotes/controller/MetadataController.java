package com.nsider.SlidesToNotes.controller;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.model.MetadataRequest;
import com.nsider.SlidesToNotes.utils.files.ZipFileCreator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * Controller for handling metadata download requests.
 * <p>
 * This controller provides an endpoint for downloading metadata, including notes and images, in a ZIP file format.
 * </p>
 */
@RestController
@RequestMapping("/api/metadata")
@Slf4j
public class MetadataController {

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    /**
     * Downloads metadata as a ZIP file containing notes and images.
     * Asynchronously processes the download request to handle high traffic.
     *
     * @param metadataRequest The request body containing the metadata, including notes and image URLs.
     * @param origin          The "Origin" header of the request to validate the source.
     * @return A response entity containing the ZIP file as a {@link ByteArrayResource}.
     */
    @PostMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadMetadata(
            @RequestBody @Nonnull MetadataRequest metadataRequest,
            @RequestHeader("Origin") String origin) {

        if (!allowedOrigin.equals(origin)) {
            return ResponseEntity.status(403).body(null);
        }

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" 
                            + (metadataRequest.getFileName() != null ? metadataRequest.getFileName() : "metadata") + ".zip\"")
                    .body(outputStream -> ZipFileCreator.createZipFile(metadataRequest, outputStream));
        } catch (Exception e) {
            log.error("Failed to download metadata: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}