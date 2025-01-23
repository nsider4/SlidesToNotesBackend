package com.nsider.SlidesToNotes.utils.files;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.math.BigInteger;

/**
 * Utility class that provides helper methods for manipulating table cells in Word documents using Apache POI.
 * <p>
 * This class allows for setting the font style, width, and borders for table cells within a Word document.
 * It provides static methods that can be used to configure the appearance of table cells in the document.
 * </p>
 */
public class TableCellHelper {

    /**
     * Sets the font style (Arial, size 12) for a cell.
     *
     * @param cell the table cell to style.
     */
    public static void setFontStyle(XWPFTableCell cell) {
        XWPFParagraph para = cell.getParagraphs().get(0);
        XWPFRun run;
    
        if (para.getRuns().isEmpty()) {
            run = para.createRun();
        } else {
            run = para.getRuns().get(0);
        }
    
        run.setFontFamily("Arial");
        run.setFontSize(12);
    }
    /**
     * Sets the width of the cell.
     *
     * @param cell the table cell to set the width.
     * @param width the width in DXA (twentieths of a point).
     */
    public static void setCellWidth(XWPFTableCell cell, long width) {
        CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();
        cellWidth.setType(STTblWidth.DXA);
        cellWidth.setW(BigInteger.valueOf(width));
    }

    /**
     * Adds a border to all sides of the table cell.
     *
     * @param cell the table cell to add the borders to.
     */
    public static void setCellBorders(XWPFTableCell cell) {
        CTTcPr cellProperties = cell.getCTTc().addNewTcPr();

        CTTblBorders borders = CTTblBorders.Factory.newInstance();

        CTBorder border = CTBorder.Factory.newInstance();
        border.setSz(BigInteger.valueOf(4));
        border.setSpace(BigInteger.valueOf(0));
        border.setVal(STBorder.SINGLE);

        borders.setTop(border);
        borders.setBottom(border);
        borders.setLeft(border);
        borders.setRight(border);

        cellProperties.addNewTcBorders().set(borders);
    }
}
