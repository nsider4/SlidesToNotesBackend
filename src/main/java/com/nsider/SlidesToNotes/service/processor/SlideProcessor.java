package com.nsider.SlidesToNotes.service.processor;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.annotations.Nullable;
import com.nsider.SlidesToNotes.utils.notes.ChartHelper;

import org.apache.poi.xslf.usermodel.*;

import java.io.Writer;
import java.io.IOException;

public class SlideProcessor {

    /**
     * Processes the given slide and writes its content to the provided writer.
     *
     * @param slide         The slide to process.
     * @param slideWidth    The width of the slide.
     * @param slideHeight   The height of the slide.
     * @param removeImages  Flag to indicate whether images should be removed.
     * @param imageProcessor The image processor used to handle image processing.
     * @param writer        The writer to which the processed content will be written.
     * @throws IOException If an I/O error occurs during writing.
     */
    public void processSlide(
            @Nonnull XSLFSlide slide,
            double slideWidth,
            double slideHeight,
            boolean removeImages,
            @Nonnull ImageProcessor imageProcessor,
            Writer writer) throws IOException {

        String title = slide.getTitle() != null ? slide.getTitle().trim() : null;

        if (title != null && !title.isEmpty()) {
            writer.write("<h2>" + title + "</h2>");
        }

        StringBuilder slideContent = new StringBuilder();

        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                processTextShape((XSLFTextShape) shape, title, slideContent);
            } else if (shape instanceof XSLFPictureShape && !removeImages) {
                slideContent.append(imageProcessor.processImage((XSLFPictureShape) shape, slideWidth, slideHeight, slide));
            } else if (shape instanceof XSLFTable) {
                slideContent.append(processTable((XSLFTable) shape));
            } else if (shape instanceof XSLFGroupShape) {
                processGroupShape((XSLFGroupShape) shape, slideContent, imageProcessor, slideWidth, slideHeight, slide);
            } else if (shape instanceof XSLFDiagram) {
                processDiagram((XSLFDiagram) shape, slideContent);
            } else if (shape instanceof XSLFConnectorShape) {
                slideContent.append("<p>[Connector Shape]</p>");
            } else if (shape instanceof XSLFGraphicFrame) {
                XSLFGraphicFrame graphicFrame = (XSLFGraphicFrame) shape;
                XSLFChart chart = graphicFrame.getChart();
                if (chart != null) {
                    processChart(chart, slideContent);
                } else {
                    slideContent.append(processGraphicFrame(graphicFrame));
                }
            }
        }

        writer.write(slideContent.toString());
        writer.write("<hr/>");
    }

    /**
     * Processes a text shape and appends its content to the provided StringBuilder.
     *
     * @param textShape The text shape to process.
     * @param title     The title of the slide (to exclude from content).
     * @param slideContent The StringBuilder to append the processed content to.
     */
    private void processTextShape(
            @Nonnull XSLFTextShape textShape,
            @Nullable String title,
            @Nonnull StringBuilder slideContent) {

        String content = textShape.getText().trim();

        if (!content.isEmpty() && !content.equals(title)) {
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    slideContent.append("<p>").append(line.trim()).append("</p>");
                }
            }
        }
    }

    /**
     * Processes a table and converts it into an HTML table representation.
     *
     * @param table The table to process.
     * @return A string representing the HTML table.
     */
    private String processTable(@Nonnull XSLFTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        
        for (XSLFTableRow row : table.getRows()) {
            sb.append("<tr>");
            for (XSLFTableCell cell : row.getCells()) {
                sb.append("<td>").append(cell.getText().trim()).append("</td>");
            }
            sb.append("</tr>");
        }
        
        sb.append("</table>");
        return sb.toString();
    }

    /**
     * Processes a chart and appends its content to the slide content.
     *
     * @param chart        The chart to process.
     * @param slideContent The StringBuilder to append the processed content to.
     */
    private void processChart(
            @Nonnull XSLFChart chart, 
            @Nonnull StringBuilder slideContent) {
            
        ChartHelper.processChart(chart, slideContent);
    }

    /**
     * Processes a group shape and appends its contents to the slide content.
     *
     * @param groupShape   The group shape to process.
     * @param slideContent The StringBuilder to append the processed content to.
     * @param imageProcessor The image processor used to handle image processing.
     * @param slideWidth   The width of the slide.
     * @param slideHeight  The height of the slide.
     * @param slide        The slide the group shape belongs to.
     */
    private void processGroupShape(@Nonnull XSLFGroupShape groupShape, 
            @Nonnull StringBuilder slideContent, 
            @Nonnull ImageProcessor imageProcessor, 
            double slideWidth, 
            double slideHeight, 
            @Nonnull XSLFSlide slide) {

        for (XSLFShape shape : groupShape.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                processTextShape((XSLFTextShape) shape, null, slideContent);
            } else if (shape instanceof XSLFPictureShape) {
                slideContent.append(imageProcessor.processImage((XSLFPictureShape) shape, slideWidth, slideHeight, slide));
            } else if (shape instanceof XSLFGraphicFrame) {
                XSLFGraphicFrame graphicFrame = (XSLFGraphicFrame) shape;
                XSLFChart chart = graphicFrame.getChart();
                if (chart != null) {
                    processChart(chart, slideContent);
                }
            } else {
                System.out.println("Unsupported shape in group: " + shape.getClass().getName());
            }
        }
    }

    /**
     * Processes a diagram and appends its text content to the slide content.
     *
     * @param diagram      The diagram to process.
     * @param slideContent The StringBuilder to append the processed content to.
     */
    private void processDiagram(
            @Nonnull XSLFDiagram diagram, 
            @Nonnull StringBuilder slideContent) {

        XSLFGroupShape group = diagram.getGroupShape();

        for (XSLFShape shape : group.getShapes()) {
            if(shape instanceof XSLFTextShape) {
                XSLFTextShape newTextShape = (XSLFTextShape) shape;
                processTextShape(newTextShape, null, slideContent);
            } else if (shape instanceof XSLFTextBox) {
                XSLFTextBox newTextShape = (XSLFTextBox) shape;
                slideContent.append("<p>").append(newTextShape.getText().trim()).append("</p>").append(" ");
            }
        }
    }

    /**
     * Processes a graphic frame and returns its HTML representation.
     *
     * @param graphicFrame The graphic frame to process.
     * @return A string representing the HTML content of the graphic frame.
     */
    private String processGraphicFrame(@Nonnull XSLFGraphicFrame graphicFrame) {
        String frameName = graphicFrame.getShapeName();
        return "<p>[Graphic Frame: " + (frameName != null ? frameName : "Unnamed") + "]</p>";
    }
}