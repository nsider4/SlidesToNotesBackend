package com.nsider.SlidesToNotes.service.processor;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.utils.images.ImageCompressor;

import java.awt.geom.Rectangle2D;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFSlide;

/**
 * A class for processing images within PowerPoint slides.
 * It handles image compression and conversion to HTML format, while also determining background images.
 */
public class ImageProcessor {

    private final ImageCompressor imageCompressor;

    /**
     * Constructs an ImageProcessor instance with an ImageCompressor.
     */
    public ImageProcessor() {
        this.imageCompressor = new ImageCompressor();
    }

    /**
     * Processes an image shape and returns its HTML representation.
     * This method compresses the image and converts it to base64 format to be embedded in HTML.
     * 
     * @param pictureShape The picture shape to process, must not be {@code null}.
     * @param slideWidth   The width of the slide.
     * @param slideHeight  The height of the slide.
     * @param slide        The slide containing the picture, must not be {@code null}.
     * @return The HTML representation of the image, or an empty string if the image is a background.
     */
    @Nonnull
    public String processImage(
            @Nonnull XSLFPictureShape pictureShape, 
            double slideWidth, 
            double slideHeight, 
            @Nonnull XSLFSlide slide) {

        if (isBackgroundImage(slideWidth, slideHeight, pictureShape, slide)) {
            return "";
        }
    
        XSLFPictureData pictureData = pictureShape.getPictureData();
        byte[] imageBytes = pictureData.getData();
    
        byte[] compressedImageBytes = imageCompressor.compressImage(imageBytes);
        String base64Image = Base64.encodeBase64String(compressedImageBytes);
        String mimeType = "image/png";
    
        return "<img src=\"data:" + mimeType + ";base64," + base64Image +
               "\" alt=\"Image\" style=\"max-width: 100%; height: auto;\"/>";
    }
    
    /**
     * Determines if a given picture shape is likely to be a background image in a slide.
     * A background image is identified by its size relative to the slide's dimensions.
     * 
     * @param slideWidth   The width of the slide in points.
     * @param slideHeight  The height of the slide in points.
     * @param pictureShape The picture shape object to check, must not be {@code null}.
     * @param slide        The slide object containing the picture shape, must not be {@code null}.
     * @return True if it's a background image, false otherwise.
     */
    private boolean isBackgroundImage(
            double slideWidth, 
            double slideHeight, 
            @Nonnull XSLFPictureShape pictureShape, 
            @Nonnull XSLFSlide slide) {

        Rectangle2D pictureBounds = pictureShape.getAnchor();

        double minWidth = slideWidth * 0.15;
        double minHeight = slideHeight * 0.15;
        double largeImageThresholdWidth = slideWidth * 0.7;
        double largeImageThresholdHeight = slideHeight * 0.7;

        boolean coversSignificantArea = pictureBounds.getWidth() > largeImageThresholdWidth ||
                                        pictureBounds.getHeight() > largeImageThresholdHeight;

        boolean isLastShape = getZOrder(slide, pictureShape) == slide.getShapes().size() - 1;

        boolean isSmallImage = (pictureBounds.getWidth() < minWidth && pictureBounds.getHeight() < minHeight);

        if (coversSignificantArea && !isLastShape) {
            return true;
        }

        if (isSmallImage && !isLastShape) {
            return true;
        }

        return false;
    }

    /**
     * Gets the Z-order position of a given shape in a slide.
     * Z-order refers to the stacking order of shapes in a slide, where a larger number indicates a higher stacking order.
     * 
     * @param slide The slide object containing the shapes, must not be {@code null}.
     * @param shape The picture shape object to find the Z-order position for, must not be {@code null}.
     * @return The Z-order position of the shape, or -1 if the shape is not found.
     */
    private int getZOrder(
            @Nonnull XSLFSlide slide, 
            @Nonnull XSLFPictureShape shape) {

        for (int i = 0; i < slide.getShapes().size(); i++) {
            if (slide.getShapes().get(i) == shape) {
                return i;
            }
        }
        return -1;
    }
}