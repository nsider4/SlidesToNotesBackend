package com.nsider.SlidesToNotes.utils.files;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.utils.images.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Helper class for adding headers, paragraphs, and images to a {@link XWPFDocument}.
 * This class provides methods for creating various content in the Word document.
 */
public class DocumentHelper {

    private static final String DEFAULT_FONT_FAMILY = "Arial";
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final int HEADER_FONT_SIZE = 16;
    private static final String HEADER_COLOR = "6495ED";

    /**
     * Adds a header with level 2 styling to the document.
     *
     * @param document the {@link XWPFDocument} to add the header to.
     * @param text     the header text.
     * @throws IllegalArgumentException if document or text is null.
     */
    public void addHeader2(
            @Nonnull XWPFDocument document, 
            @Nonnull String text) {

        if (document == null || text == null) {
            throw new IllegalArgumentException("Document and text must not be null");
        }

        createBlankLines(document, 2);

        new ParagraphBuilder(document)
            .alignment(ParagraphAlignment.LEFT)
            .spacingAfter(200)
            .bold(true)
            .fontSize(HEADER_FONT_SIZE)
            .color(HEADER_COLOR)
            .fontFamily(DEFAULT_FONT_FAMILY)
            .text(text)
            .build();
    }

    /**
     * Adds a paragraph to the document.
     *
     * @param document the {@link XWPFDocument} to add the paragraph to.
     * @param text     the paragraph text.
     * @throws IllegalArgumentException if document or text is null.
     */
    public void addParagraph(
            @Nonnull XWPFDocument document, 
            @Nonnull String text) {

        if (document == null || text == null) {
            throw new IllegalArgumentException("Document and text must not be null");
        }

        // Add "- " at the beginning of the text
        String bulletText = "- " + text;

        // Create the paragraph with the modified text
        new ParagraphBuilder(document)
                .alignment(ParagraphAlignment.LEFT)
                .fontSize(DEFAULT_FONT_SIZE)
                .fontFamily(DEFAULT_FONT_FAMILY)
                .text(bulletText)
                .build();
    }

    /**
     * Adds an image to the document.
     *
     * @param document the {@link XWPFDocument} to add the image to.
     * @param imageUrl the URL of the image.
     * @param format   the format of the image (e.g., "png", "jpeg").
     * @throws IOException if an error occurs while processing the image.
     * @throws IllegalArgumentException if document, imageUrl, or format is null.
     */
    public void addImage(
            @Nonnull XWPFDocument document, 
            @Nonnull String imageUrl, 
            @Nonnull String format) throws IOException {

        if (document == null || imageUrl == null || format == null) {
            throw new IllegalArgumentException("Document, imageUrl, and format must not be null");
        }

        byte[] imageBytes = ImageUtils.downloadImage(imageUrl);
        if (imageBytes == null) {
            throw new IOException("Failed to download image from URL: " + imageUrl);
        }

        imageBytes = ImageUtils.convertImageFormat(imageBytes, format);
        if (imageBytes == null) {
            throw new IOException("Failed to convert image format: " + format);
        }

        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (img == null) {
            throw new IOException("Invalid image format: " + format);
        }

        int[] dimensions = ImageUtils.getAdjustedImageDimensions(img);
        if (dimensions == null || dimensions.length != 2) {
            throw new IOException("Failed to calculate image dimensions");
        }

        int imageType = getImageType(format);
        createImageParagraph(document, imageBytes, imageType, dimensions[0], dimensions[1]);
    }

    /**
     * Creates a centered paragraph with an image in the document.
     *
     * @param document   the {@link XWPFDocument} to add the image to.
     * @param imageBytes the byte array of the image.
     * @param imageType  the type of the image (e.g., PNG, JPEG).
     * @param widthEMU   the width of the image in EMUs.
     * @param heightEMU  the height of the image in EMUs.
     * @throws IOException if an error occurs while adding the image to the document.
     */
    private void createImageParagraph(
        @Nonnull XWPFDocument document, 
        @Nonnull byte[] imageBytes, 
        int imageType, 
        int widthEMU, 
        int heightEMU) throws IOException {

        XWPFParagraph imageParagraph = document.createParagraph();
        imageParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun imageRun = imageParagraph.createRun();

        try (ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes)) {
            imageRun.addPicture(
                imageStream,
                imageType,
                "image",
                widthEMU,
                heightEMU
            );
        } catch (InvalidFormatException e) {
            throw new IOException("Invalid image format: Unable to add picture to document", e);
        }
    }

    /**
     * Adds the table to the file
     *
     * @param document the document to modify.
     * @param tableRows the list of rows to add to the table.
     * 
     * @throws IllegalArgumentException if the document or tableRows are null.
     */
    public void addTable(
            @Nonnull XWPFDocument document, 
            @Nonnull List<List<String>> tableRows) {

        if (document == null || tableRows == null || tableRows.isEmpty()) {
            throw new IllegalArgumentException("Document and tableRows must not be null or empty");
        }
    
        XWPFTable table = document.createTable();
    
        for (int rowIndex = 0; rowIndex < tableRows.size(); rowIndex++) {
            List<String> row = tableRows.get(rowIndex);
    
            XWPFTableRow tableRow;
            if (rowIndex == 0) {
                tableRow = table.getRow(0);
            } else {
                tableRow = table.createRow();
            }
    
            while (tableRow.getTableCells().size() > row.size()) {
                tableRow.removeCell(tableRow.getTableCells().size() - 1);
            }
    
            for (int i = 0; i < row.size(); i++) {
                XWPFTableCell cell = tableRow.getCell(i);
                if (cell == null) {
                    cell = tableRow.addNewTableCell();
                }
    
                cell.setText(row.get(i));
    
                TableCellHelper.setFontStyle(cell);
                TableCellHelper.setCellWidth(cell, 5000);
                TableCellHelper.setCellBorders(cell);
            }
        }

        createBlankLines(document, 2);
    }

    /**
     * Creates blank lines in the document.
     *
     * @param document the {@link XWPFDocument} to add blank lines to.
     * @param lines    the number of blank lines to create.
     * @throws IllegalArgumentException if document is null or lines is non-positive.
     */
    private void createBlankLines(
            @Nonnull XWPFDocument document, 
            int lines) {

        if (document == null || lines <= 0) {
            throw new IllegalArgumentException("Document must not be null, and lines must be greater than 0");
        }

        for (int i = 0; i < lines; i++) {
            document.createParagraph();
        }
    }

    /**
     * Determines the image type based on the format.
     *
     * @param format the image format (e.g., "jpeg", "png").
     * @return the image type constant from {@link XWPFDocument}.
     * @throws IllegalArgumentException if the image format is unsupported.
     */
    private int getImageType(@Nonnull String format) {
        if (format == null) {
            throw new IllegalArgumentException("Image format must not be null");
        }

        switch (format.toLowerCase()) {
            case "jpeg":
            case "jpg":
                return XWPFDocument.PICTURE_TYPE_JPEG;
            case "png":
                return XWPFDocument.PICTURE_TYPE_PNG;
            default:
                throw new IllegalArgumentException("Unsupported image format: " + format);
        }
    }
}