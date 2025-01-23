package com.nsider.SlidesToNotes.utils.files;

import org.apache.poi.xwpf.usermodel.*;

import com.nsider.SlidesToNotes.annotations.Nonnull;

/**
 * Builder class to create and customize a {@link XWPFParagraph} with various styling options.
 * This builder simplifies the process of creating a styled paragraph in a Word document.
 */
public class ParagraphBuilder {

    private final XWPFParagraph paragraph;
    private XWPFRun run;

    /**
     * Constructs a {@link ParagraphBuilder} instance for creating a new paragraph in the given document.
     *
     * @param document the {@link XWPFDocument} to which the paragraph will be added.
     */
    public ParagraphBuilder(@Nonnull XWPFDocument document) {
        this.paragraph = document.createParagraph();
        this.run = paragraph.createRun();
    }

    /**
     * Sets the alignment of the paragraph.
     *
     * @param alignment the {@link ParagraphAlignment} to set for the paragraph.
     * @return the current {@link ParagraphBuilder} instance for method chaining.
     */
    public ParagraphBuilder alignment(@Nonnull ParagraphAlignment alignment) {
        this.paragraph.setAlignment(alignment);
        return this;
    }

    /**
     * Sets the spacing after the paragraph.
     *
     * @param spacingAfter the spacing after the paragraph in points.
     * @return the current {@link ParagraphBuilder} instance for method chaining.
     */
    public ParagraphBuilder spacingAfter(int spacingAfter) {
        this.paragraph.setSpacingAfter(spacingAfter);
        return this;
    }

    /**
     * Sets whether the text should be bold.
     *
     * @param bold true to set the text as bold, false to not.
     * @return the current {@link ParagraphBuilder} instance for method chaining.
     */
    public ParagraphBuilder bold(boolean bold) {
        this.run.setBold(bold);
        return this;
    }

    /**
     * Sets the font size of the text.
     *
     * @param fontSize the font size to set.
     * @return the current {@link ParagraphBuilder} instance for method chaining.
     */
    public ParagraphBuilder fontSize(int fontSize) {
        this.run.setFontSize(fontSize);
        return this;
    }

    /**
     * Sets the color of the text.
     *
     * @param color the color code to set for the text.
     * @return the current {@link ParagraphBuilder} instance for method chaining.
     */
    public ParagraphBuilder color(@Nonnull String color) {
        this.run.setColor(color);
        return this;
    }

    /**
     * Sets the font family of the text.
     *
     * @param fontFamily the font family to set for the text.
     * @return the current {@link ParagraphBuilder} instance for method chaining.
     */
    public ParagraphBuilder fontFamily(@Nonnull String fontFamily) {
        this.run.setFontFamily(fontFamily);
        return this;
    }

    /**
     * Sets the text for the paragraph.
     *
     * @param text the text to set for the paragraph.
     * @return the current {@link ParagraphBuilder} instance for method chaining.
     */
    public ParagraphBuilder text(@Nonnull String text) {
        this.run.setText(text);
        return this;
    }

    /**
     * Builds and returns the created {@link XWPFParagraph}.
     *
     * @return the {@link XWPFParagraph} created using the builder.
     */
    public XWPFParagraph build() {
        return this.paragraph;
    }
}