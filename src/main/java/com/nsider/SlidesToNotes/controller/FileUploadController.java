package com.nsider.SlidesToNotes.controller;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for handling file uploads and processing them.
 * <p>
 * This controller provides an endpoint for uploading PPTX files and processing them.
 * It also provides an option to remove images from the PPTX during processing.
 * </p>
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class FileUploadController {

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    /**
     * The service used to process the uploaded files.
     */
    @Nonnull
    private final FileService fileService;

    /**
     * Handles file uploads and processes the uploaded PPTX file.
     *
     * @param file          The file to be uploaded (must be a PPTX file).
     * @param removeImages  Flag indicating whether images should be removed from the file.
     * @param imageFormat   The format of the image (e.g., "png", "jpeg").
     * @return A response entity containing the extracted notes or an error message.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
                @RequestParam("file") @Nonnull MultipartFile file,
                @RequestParam(value = "removeImages", defaultValue = "true") boolean removeImages,
                @RequestHeader("Origin") String origin) {
                
        if (!allowedOrigin.equals(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Invalid origin.");
        }
        
                    try {
            String notes = fileService.processPPTX(file, removeImages);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            log.error("Failed to process file: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to process file: " + e.getMessage());
        }
    }
}