package com.nsider.SlidesToNotes.utils.images;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Helper class to compress images for better memory usage and performance.
 */
@Slf4j
public class ImageCompressor {

    private static final int SMALL_IMAGE_THRESHOLD = 500;
    private static final int RESIZE_THRESHOLD = 2000;
    private static final String OUTPUT_FORMAT = "png";

    /**
     * Compresses the image bytes for better memory usage by resizing large images.
     * <p>
     * If the image is small (less than 500x500 pixels), it is returned without compression.
     * Large images (greater than 2000x2000 pixels) are resized to half their original size.
     * </p>
     *
     * @param imageBytes the image bytes to compress, must not be {@code null}.
     * @return the compressed image bytes, or the original image bytes if compression fails or is not necessary.
     */
    public byte[] compressImage(@Nonnull byte[] imageBytes) {
        try {
            BufferedImage originalImage = convertBytesToImage(imageBytes);
            if (originalImage == null) {
                log.error("Failed to decode image from byte array.");
                return imageBytes;
            }

            //Check if the image is small
            if (isSmallImage(originalImage)) {
                return convertImageToBytes(originalImage);
            }

            BufferedImage resizedImage = resizeImageIfNeeded(originalImage);
            return convertImageToBytes(resizedImage);
        } catch (IOException e) {
            log.error("Error during image compression", e);
            return imageBytes;
        }
    }

    /**
     * Converts the byte array into a BufferedImage.
     *
     * @param imageBytes the image bytes to convert.
     * @return the BufferedImage representation of the image bytes, or {@code null} if decoding fails.
     */
    private BufferedImage convertBytesToImage(@Nonnull byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(inputStream);
        }
    }

    /**
     * Checks whether the image is small (less than 500x500 pixels).
     *
     * @param image the image to check.
     * @return {@code true} if the image is small, otherwise {@code false}.
     */
    private boolean isSmallImage(@Nonnull BufferedImage image) {
        return image.getWidth() < SMALL_IMAGE_THRESHOLD && image.getHeight() < SMALL_IMAGE_THRESHOLD;
    }

    /**
     * Resizes the image if its dimensions exceed 2000x2000 pixels.
     *
     * @param originalImage the original image to resize.
     * @return the resized image.
     */
    private BufferedImage resizeImageIfNeeded(@Nonnull BufferedImage originalImage) {
        int targetWidth = originalImage.getWidth();
        int targetHeight = originalImage.getHeight();

        if (originalImage.getWidth() > RESIZE_THRESHOLD || originalImage.getHeight() > RESIZE_THRESHOLD) {
            targetWidth = originalImage.getWidth() / 2;
            targetHeight = originalImage.getHeight() / 2;
            log.info("Resizing image from {}x{} to {}x{}", originalImage.getWidth(), originalImage.getHeight(), targetWidth, targetHeight);
        }

        return resizeImage(originalImage, targetWidth, targetHeight);
    }

    /**
     * Resizes the given image to the specified dimensions.
     *
     * @param originalImage the original image to resize.
     * @param width the target width for the resized image.
     * @param height the target height for the resized image.
     * @return the resized image.
     */
    private BufferedImage resizeImage(
            @Nonnull BufferedImage originalImage, 
            int width, 
            int height) {

        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(originalImage, 0, 0, width, height, null);
        } finally {
            g2d.dispose();
        }
        return resizedImage;
    }

    /**
     * Converts a BufferedImage to a byte array.
     *
     * @param image the image to convert.
     * @return the byte array representation of the image.
     * @throws IOException if an error occurs while converting the image.
     */
    private byte[] convertImageToBytes(@Nonnull BufferedImage image) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, OUTPUT_FORMAT, outputStream)) {
                throw new IOException("Failed to write image in format: " + OUTPUT_FORMAT);
            }
            return outputStream.toByteArray();
        }
    }
}