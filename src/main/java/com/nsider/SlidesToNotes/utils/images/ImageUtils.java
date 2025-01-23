package com.nsider.SlidesToNotes.utils.images;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.Units;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

/**
 * Helper class for handling image-related operations.
 * <p>
 * Provides utility methods for downloading images from URLs (including base64 encoded data URLs) and
 * converting images to different formats.
 * </p>
 */
@Slf4j
public class ImageUtils {

    private static final int PAGE_WIDTH_EMU = Units.toEMU(450);
    private static final int PAGE_HEIGHT_EMU = Units.toEMU(600);

    /**
     * Downloads an image from a URL, supporting both base64-encoded data URLs and regular HTTP/HTTPS URLs.
     *
     * @param imageUrl The URL of the image to be downloaded (can be a data URL or an HTTP/HTTPS URL).
     * @return A byte array containing the image data.
     * @throws IOException If an I/O error occurs while downloading the image.
     */
    public static byte[] downloadImage(@Nonnull String imageUrl) throws IOException {
        if (imageUrl.startsWith("data:")) {
            //Extract base64 content from the data URL
            String[] parts = imageUrl.split(",", 2);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid base64 data URL format.");
            }
            return Base64.getDecoder().decode(parts[1]);
        } else {
            HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(5000); //Set timeout for robustness
            connection.setReadTimeout(5000);

            try (InputStream inputStream = connection.getInputStream()) {
                return inputStream.readAllBytes();
            } catch (IOException e) {
                log.error("Error downloading image from URL: {}", imageUrl, e);
                throw e;
            } finally {
                connection.disconnect();
            }
        }
    }

    /**
     * Converts an image from one format to another.
     *
     * @param imageBytes The image data as a byte array.
     * @param format     The format to convert the image to (e.g., "PNG", "JPG").
     * @return A byte array containing the converted image data.
     * @throws IOException If an I/O error occurs during image conversion.
     */
    public static byte[] convertImageFormat(
            @Nonnull byte[] imageBytes, 
            @Nonnull String format) throws IOException {
            
        try (InputStream in = new ByteArrayInputStream(imageBytes);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            BufferedImage image = ImageIO.read(in);
            if (image == null) {
                throw new IOException("Failed to decode image from byte array.");
            }

            if (!ImageIO.write(image, format, out)) {
                throw new IOException("Unsupported format: " + format);
            }

            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error converting image format to {}", format, e);
            throw e;
        }
    }

    /**
     * Adjusts the dimensions of an image to fit within predefined page limits while maintaining the aspect ratio.
     *
     * @param img The BufferedImage to adjust.
     * @return An array containing the adjusted width and height in EMU.
     */
    public static int[] getAdjustedImageDimensions(@Nonnull BufferedImage img) {
        int originalWidthEMU = Units.toEMU(img.getWidth());
        int originalHeightEMU = Units.toEMU(img.getHeight());

        int widthEMU = originalWidthEMU;
        int heightEMU = originalHeightEMU;

        if (originalWidthEMU > PAGE_WIDTH_EMU) {
            widthEMU = PAGE_WIDTH_EMU;
            heightEMU = (int) ((double) widthEMU / originalWidthEMU * originalHeightEMU);
        }

        if (heightEMU > PAGE_HEIGHT_EMU) {
            heightEMU = PAGE_HEIGHT_EMU;
            widthEMU = (int) ((double) heightEMU / originalHeightEMU * originalWidthEMU);
        }

        log.debug("Adjusted image dimensions: width={} EMU, height={} EMU", widthEMU, heightEMU);
        return new int[]{widthEMU, heightEMU};
    }
}
