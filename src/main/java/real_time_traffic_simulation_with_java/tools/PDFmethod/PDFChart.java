package real_time_traffic_simulation_with_java.tools.PDFmethod;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import tech.tablesaw.api.Table;

import java.util.logging.Logger;
import java.util.List;
import java.util.Arrays;
import java.awt.image.BufferedImage;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.markers.SeriesMarkers;

import real_time_traffic_simulation_with_java.alias.Metrics;


/**
 * Provides methods to add charts to a PDF document using data from a CSV log file.
 * Charts supported: 
 * - Vehicle Count Over Time (with optional color filtering)
 * - Congested Edge Count Over Time
 */
public final class PDFChart {
    private static final Logger LOGGER = Logger.getLogger(PDFChart.class.getName());

    private PDFChart() {
        // Private constructor to prevent instantiation
    }


    /**
     * Add Vehicle Count Over Time chart to PDF document
     * @param csv_table
     * @param document
     * @param writer
     * @param filter_veh_color If empty string, include all colors + total;
     */
    public static void addVehicleCountChart(Table csv_table, Document document, PdfWriter writer, String filter_veh_color) {
        List<String> series_names = new java.util.ArrayList<>();
        List<List<Integer>> series = new java.util.ArrayList<>();
        List<List<Integer>> simulationStep = new java.util.ArrayList<>();
        boolean show_series_names;

        // Series names (vehicle colors in simulation) & show legend
        if(filter_veh_color.equals("")) { // Without filtering (all colors + total number) [color1, color2, ..., total]
            series_names = csv_table.stringColumn("vehicle color").unique().asList();
            show_series_names = true;
        } else {
            series_names.add(filter_veh_color);
            show_series_names = false;
        }

        // Vehicles count by color and simulation step
        for (String color : series_names) {
            Table vehicle_by_color_by_time = csv_table.where(csv_table.stringColumn("vehicle color").isEqualTo(color))
                                                        .countBy("simulation step");
            simulationStep.add(vehicle_by_color_by_time.intColumn("simulation step").asList());
            series.add(vehicle_by_color_by_time.intColumn("Count").asList());
        }

        // Data for total number of vehicles (only when no filter applied)
        if(filter_veh_color.equals("")) {
            series_names.add("All");
            Table vehicle_per_step = csv_table.countBy("simulation step");
            simulationStep.add(vehicle_per_step.intColumn("simulation step").asList());
            series.add(vehicle_per_step.intColumn("Count").asList());
        }
        
        addChart(document, writer,
                    "Number of Vehicles Over Time", 
                    "Number of Vehicles", 
                    series_names, 
                    simulationStep, 
                    series,
                    show_series_names);
    }


    /**
     * Add Congested Edge Count Over Time chart to PDF document
     * @param csv_table
     * @param document
     * @param writer
     * @param exported_simulation_step Used to fill data if no data available (no edges has been congested)
     */
    public static void addCongestedEdgeCountChart(Table csv_table, Document document, PdfWriter writer, String exported_simulation_step) {
        // Prepare data: simulation step vs congested edge count
        Table edge_congested = csv_table.where(csv_table.booleanColumn("edge congestion status").isTrue());
        edge_congested = edge_congested.selectColumns("simulation step", "vehicle is on edge")
                                                .dropDuplicateRows().countBy("simulation step");
        List<Integer> simulationStep = edge_congested.intColumn("simulation step").asList();
        List<Integer> congestedEdgeCount = edge_congested.intColumn("Count").asList();

        // If no data available, fill with 0 up to exported_simulation_step
        if(simulationStep.size() == 0) {
            int exported_step = (int) Double.parseDouble(exported_simulation_step);
            for (int step = 0; step <= exported_step; step++) {
                simulationStep.add(step);
                congestedEdgeCount.add(0);
            }
        }
        
        addChart(document, writer,
                    "Number of Congested Edges Over Time", 
                    "Number of Congested Edges", 
                    Arrays.asList("Congested Edges"), 
                    Arrays.asList(simulationStep),
                    Arrays.asList(congestedEdgeCount),
                    false);
    }


    /**
     * Private helper method to add chart to PDF document
     * @param document PDF document to add the chart to
     * @param writer PDF writer associated with the document to write buffered image without saving to file
     * @param chart_title title of the chart
     * @param y_title title for y axis
     * @param series_names List<String> representing the names of each series
     * @param simulationStep List<List<Integer>> representing the x values (simulation steps) for each series
     * @param series List<List<Integer>> representing the y values for each series
     * @param show_series_names boolean indicating whether to show series names in legend
     */
    private static final void addChart(Document document, PdfWriter writer,
                                        String chart_title, String y_title, List<String> series_names,
                                        List<List<Integer>> simulationStep, List<List<Integer>> series,
                                        boolean show_series_names) {

        // If no data, skip draw chart
        if(simulationStep.size() == 0 || series_names.size() == 0 || series.size() == 0) {
            LOGGER.warning("No data available for chart: " + chart_title);
            return;
        }

        // Validate data size: number of series names and series data should match
        if(series_names.size() != series.size()) {
            LOGGER.warning("Series names and data size mismatch for chart: " + chart_title);
            return;
        }

        // Calculate chart width and height, 
        //     height of image that is not chart is estimated to be 88.75 when scaled to this width.
        // Preferred height is based on max y value to avoid excessive white space,
        //     but capped to usable height in case of too many y units.
        float usable_width = PageSize.A4.getWidth() - document.leftMargin() - document.rightMargin();
        float usable_height = (PageSize.A4.getHeight() - document.topMargin() - document.bottomMargin())/2 - 30; // half page minus 30 for spacing
        int max_y_value;
        try{
            max_y_value = java.util.Collections.max(series.get(series.size() - 1));
        } catch (Exception e) {
            max_y_value = 0; // if no data present for y axis
        }
        float preferred_chart_height = Metrics.SIZE_PER_Y_UNIT * max_y_value + 88.75 > usable_height ? 
                    (int)(usable_height) : (int)(Metrics.SIZE_PER_Y_UNIT * max_y_value + 88.75);

        // Create chart using XChart
        XYChart chart = new XYChartBuilder().width((int)usable_width).height((int)preferred_chart_height)
                                .title(chart_title)
                                .xAxisTitle("Simulation Step")
                                .yAxisTitle(y_title)
                                .build();
        for (int i = 0; i < series_names.size(); i++) {
            chart.addSeries(series_names.get(i), simulationStep.get(i), series.get(i)).setMarker(SeriesMarkers.NONE);
        }
        chart.getStyler().setLegendVisible(show_series_names);

        // Convert to BufferedImage (not saving to file) and add to PDF
        BufferedImage chartBuffer = BitmapEncoder.getBufferedImage(chart);
        Image chartImage;
        try{
            // 1.0f, no compress, max quality
            chartImage = Image.getInstance(writer, chartBuffer, 1.0f);
        } catch (Exception e) {
            LOGGER.severe("Failed to create chart image for PDF: " + e.getMessage());
            return;
        }

        document.add(chartImage);
    }
}
