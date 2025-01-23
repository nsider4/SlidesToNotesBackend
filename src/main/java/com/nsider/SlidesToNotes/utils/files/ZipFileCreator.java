package com.nsider.SlidesToNotes.utils.files;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.model.MetadataRequest;
import com.nsider.SlidesToNotes.utils.images.ImageUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for creating ZIP files containing notes and images.
 */
@Slf4j
public class ZipFileCreator {

    /**
     * Creates a ZIP file containing notes and images.
     *
     * @param metadataRequest The metadata request containing notes and image URLs.
     * @param outputStream    The output stream to write the ZIP file.
     * @throws IOException If an error occurs during ZIP file creation.
     */
    public static void createZipFile(
            @Nonnull MetadataRequest metadataRequest,
            @Nonnull OutputStream outputStream) throws IOException {

        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            addNotesToZip(metadataRequest, zos);
            addImagesToZip(metadataRequest, zos);
        } catch (IOException e) {
            log.error("Error while creating ZIP file: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Adds notes to the ZIP file.
     *
     * @param metadataRequest The metadata request containing the notes.
     * @param zos             The ZIP output stream to write the notes.
     * @throws IOException If an error occurs while adding notes to the ZIP.
     */
        private static void addNotesToZip(
            @Nonnull MetadataRequest metadataRequest,
            @Nonnull ZipOutputStream zos) throws IOException {

        String notes = metadataRequest.getNotes();
        if (notes == null || notes.isBlank()) {
            log.warn("No notes available to add to ZIP.");
            return;
        }

        zos.putNextEntry(new ZipEntry("notes.txt"));
        zos.write(notes.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    /**
     * Adds images to the ZIP file.
     *
     * @param metadataRequest The metadata request containing image URLs and format.
     * @param zos             The ZIP output stream to write the images.
     * @throws IOException If an error occurs while adding images to the ZIP.
     */
    private static void addImagesToZip(
            @Nonnull MetadataRequest metadataRequest,
            @Nonnull ZipOutputStream zos) throws IOException {

        List<String> imageUrls = metadataRequest.getImageUrls();
        String imageFormat = metadataRequest.getImageFormat();

        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        int imageIndex = 1;
        for (String imageUrl : imageUrls) {
            try {
                addImageToZip(imageUrl, imageIndex, imageFormat, zos);
            } catch (IOException e) {
                log.error("Failed to add image {} to ZIP: {}", imageIndex, e);
            }
            imageIndex++;
        }
    }

    /**
     * Adds a single image to the ZIP file.
     *
     * @param imageUrl    The URL of the image.
     * @param imageIndex  The index of the image (used for file naming).
     * @param imageFormat The desired format for the image.
     * @param zos         The ZIP output stream to write the image.
     * @throws IOException If an error occurs while adding the image to the ZIP.
     */
    private static void addImageToZip(
            @Nonnull String imageUrl,
            int imageIndex,
            @Nonnull String imageFormat,
            @Nonnull ZipOutputStream zos) throws IOException {

        if (imageFormat == null || imageFormat.isBlank()) {
            throw new IllegalArgumentException("Invalid image format: " + imageFormat);
        }

        byte[] imageBytes = ImageUtils.downloadImage(imageUrl);
        if (imageBytes == null) {
            throw new IOException("Failed to download image from URL");
        }

        byte[] convertedImageBytes = ImageUtils.convertImageFormat(imageBytes, imageFormat);
        if (convertedImageBytes == null) {
            throw new IOException("Failed to convert image format for URL");
        }

        zos.putNextEntry(new ZipEntry("image_" + imageIndex + "." + imageFormat));
        zos.write(convertedImageBytes);
        zos.closeEntry();
    }
}