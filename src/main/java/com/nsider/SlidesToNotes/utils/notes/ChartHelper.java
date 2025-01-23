package com.nsider.SlidesToNotes.utils.notes;

import com.nsider.SlidesToNotes.annotations.Nonnull;
import com.nsider.SlidesToNotes.annotations.Nullable;

import org.apache.poi.xslf.usermodel.XSLFChart;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for processing charts in PowerPoint slides.
 */
public class ChartHelper {

    /**
     * Processes a given chart and appends its content to the slide content.
     *
     * @param chart The chart to process. Must not be null.
     * @param slideContent The StringBuilder to append the chart content to. Must not be null.
     */
    public static void processChart(
            @Nonnull XSLFChart chart, 
            @Nonnull StringBuilder slideContent) {
            
        try {
            appendChartTitle(chart, slideContent);
            List<XDDFChartData> chartDataList = chart.getChartSeries();
            List<String> seriesTitles = extractSeriesTitlesFromChart(chart);
    
            for (int dataIndex = 0; dataIndex < chartDataList.size(); dataIndex++) {
                processChartData(chartDataList.get(dataIndex), dataIndex, seriesTitles, slideContent);
            }
        } catch (Exception e) {
            slideContent.append("<p>Error processing chart: ").append(e.getMessage()).append("</p>");
        }
    }

    /**
     * Appends the chart's title to the slide content.
     *
     * @param chart The chart whose title will be appended.
     * @param slideContent The StringBuilder to append the title to.
     */
    private static void appendChartTitle(
            @Nonnull XSLFChart chart, 
            @Nonnull StringBuilder slideContent) {
            
        XSLFTextShape title = chart.getTitleShape();
        slideContent.append("<h2>")
                    .append(title != null ? title.getText().trim() : "Untitled Chart")
                    .append("</h2>").append(" ");
    }

    /**
     * Processes a single chart data set and appends the corresponding content to the slide.
     *
     * @param chartData The chart data to process.
     * @param dataIndex The index of the current chart data.
     * @param seriesTitles The titles of the chart series.
     * @param slideContent The StringBuilder to append the content to.
     */
    private static void processChartData(
            @Nonnull XDDFChartData chartData, 
            int dataIndex, 
            @Nonnull List<String> seriesTitles, 
            @Nonnull StringBuilder slideContent) {
            
        slideContent.append("<h4>").append("Chart ").append(dataIndex + 1).append(":</h4>").append(" ");
        
        List<XDDFNumericalDataSource<?>> seriesValues = new ArrayList<>();
        XDDFDataSource<?> categoryData = null;
        try {
            categoryData = extractSeriesData(chartData, seriesValues);
            appendCategoryData(categoryData, seriesValues, seriesTitles, slideContent);
        } catch (Exception e) {
            slideContent.append("<p>Error processing chart data: ").append(e.getMessage()).append("</p>");
        }
    }

    /**
     * Extracts the series data from the chart data and returns the category data.
     *
     * @param chartData The chart data to extract series from.
     * @param seriesValues The list to store the series values.
     * @return The category data.
     */
    private static XDDFDataSource<?> extractSeriesData(
            @Nonnull XDDFChartData chartData, 
            @Nonnull List<XDDFNumericalDataSource<?>> seriesValues) {
            
        XDDFDataSource<?> categoryData = null;

        for (XDDFChartData.Series series : chartData.getSeries()) {
            if (categoryData == null) {
                categoryData = series.getCategoryData();
            }
            seriesValues.add(series.getValuesData());
        }

        return categoryData;
    }

    /**
     * Appends the category data and series values to the slide content.
     *
     * @param categoryData The category data to append.
     * @param seriesValues The list of series values to append.
     * @param seriesTitles The titles of the series.
     * @param slideContent The StringBuilder to append the content to.
     */
    private static void appendCategoryData(
            @Nullable XDDFDataSource<?> categoryData, 
            @Nonnull List<XDDFNumericalDataSource<?>> seriesValues, 
            @Nonnull List<String> seriesTitles, 
            @Nonnull StringBuilder slideContent) {
            
        if (categoryData != null) {
            for (int i = 0; i < categoryData.getPointCount(); i++) {
                slideContent.append("<p>Category: ").append(categoryData.getPointAt(i)).append("</p>").append(" ");
                for (int j = 0; j < seriesValues.size(); j++) {
                    XDDFNumericalDataSource<?> valuesData = seriesValues.get(j);
                    String seriesTitle = j < seriesTitles.size() ? seriesTitles.get(j) : "Untitled Series";
                    slideContent.append("<p>  Series (").append(seriesTitle).append("): ")
                                .append(valuesData.getPointAt(i)).append("</p>").append(" ");
                }
                slideContent.append("<br/>");
            }
        }
    }

    /**
     * Extracts series titles from the given chart.
     *
     * @param chart The chart from which to extract series titles. Must not be null.
     * @return A list of series titles.
     */
    private static List<String> extractSeriesTitlesFromChart(@Nonnull XSLFChart chart) {
        List<String> seriesTitles = new ArrayList<>();
        try {
            CTPlotArea plotArea = chart.getCTChart().getPlotArea();

            if (plotArea.sizeOfLineChartArray() > 0) {
                List<CTLineSer> lineSeries = plotArea.getLineChartArray(0).getSerList();
                for (CTLineSer ser : lineSeries) {
                    extractSeriesTitleFromSer(ser.getTx(), seriesTitles);
                }
            }

            if (plotArea.sizeOfBarChartArray() > 0) {
                List<CTBarSer> barSeries = plotArea.getBarChartArray(0).getSerList();
                for (CTBarSer ser : barSeries) {
                    extractSeriesTitleFromSer(ser.getTx(), seriesTitles);
                }
            }

            if (plotArea.sizeOfScatterChartArray() > 0) {
                List<CTScatterSer> scatterSeries = plotArea.getScatterChartArray(0).getSerList();
                for (CTScatterSer ser : scatterSeries) {
                    extractSeriesTitleFromSer(ser.getTx(), seriesTitles);
                }
            }

            if (plotArea.sizeOfPieChartArray() > 0) {
                List<CTPieSer> pieSeries = plotArea.getPieChartArray(0).getSerList();
                for (CTPieSer ser : pieSeries) {
                    extractSeriesTitleFromSer(ser.getTx(), seriesTitles);
                }
            }

        } catch (Exception e) {
        }
        return seriesTitles;
    }

    /**
     * Extracts the series title from a CTSerTx object and adds it to the list of series titles.
     *
     * @param tx The CTSerTx object containing the title information. May be null.
     * @param seriesTitles The list to which the extracted title will be added. Must not be null.
     */
    private static void extractSeriesTitleFromSer(
            @Nullable CTSerTx tx, 
            @Nonnull List<String> seriesTitles) {
            
        if (tx != null) {
            if (tx.isSetStrRef()) {
                String title = tx.getStrRef().getStrCache().getPtArray(0).getV();
                seriesTitles.add(title);
            } else if (tx.isSetV()) {
                String title = tx.getV();
                seriesTitles.add(title);
            } else {
                seriesTitles.add("Untitled Series");
            }
        } else {
            seriesTitles.add("Untitled Series");
        }
    }
}