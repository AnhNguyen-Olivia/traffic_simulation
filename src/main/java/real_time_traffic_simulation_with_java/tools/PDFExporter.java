package real_time_traffic_simulation_with_java.tools;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import tech.tablesaw.api.Table;
import tech.tablesaw.aggregate.AggregateFunctions;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.List;
import java.util.Collections;

import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.alias.Metrics;

public final class PDFExporter {
    private static final Logger LOGGER = Logger.getLogger(PDFExporter.class.getName());
    /** 
     * Simple counter to name the PDF files uniquely <br>
     * Represent number of PDF files has been generated in current simulation <br>
     * Since multiple PDF files may be generated in one simulation,
     *      but when simulation stop, index is reset to 0. <br>
     * PDF file name format: {csv_timestamp} Summary {index}.pdf (index starts from 1)
     */
    private static int index = 0;

    /**
     * Export summary PDF file from data from SUMO and CSV log file, 
     *      allow filtering options: vehicle color and congested edges only <br>
     * @param csv_path Path to the CSV log file of current simulation
     * @param csv_timestamp Timestamp when the simulation started (used in CSV file name)
     * @param filter_veh_color Color of vehicle to filter, empty string for no filter
     * @param filter_congested_edges Whether to filter only congested edges
     * @param data_from_simulation_engine List<String[]>, 1st element is {edgeCount, tlsCount}, each next are {edgeID, laneCount, length}
     */
    public static void exportSummary(String csv_path, String csv_timestamp, 
                                    String filter_veh_color, 
                                    boolean filter_congested_edges,
                                    List<String[]> data_from_simulation_engine) {
        try{
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(generatePath(csv_timestamp)));
            document.open();
            // Preparing data from simulation engine
            String[] edge_tls_count = data_from_simulation_engine.get(0);
            List<String[]> edge_table_data = data_from_simulation_engine.subList(1, data_from_simulation_engine.size());
            // Preparing data from CSV file
            Table csv_table = Table.read().csv(csv_path);

            // Write PDF content
            addTitle(document);
            // TODO: retrieve simulation step from CSV file and fix parameter list
            addHeading(document, csv_timestamp, csv_timestamp, filter_veh_color, filter_congested_edges);
            // TODO: retrieve vehicle count and pass to parameter list
            addObjectCount(document, edge_tls_count[0], edge_tls_count[1]);
            addEdgeTable(document, edge_table_data);
            
            document.close();
            LOGGER.info(String.format("PDF summary {%d} created successfully.", index));
        } catch (Exception e) {
            LOGGER.severe("Failed to create PDF summary: " + e.getMessage());
        }
    }



    // ----------------------------------------------------
    // WRITE PDF CONTENT
    // ----------------------------------------------------

    private static String generatePath(String csv_timestamp) {
        index += 1;
        String filePath = Path.PdfLogFolder + csv_timestamp + " Summary " + index + ".pdf";
        return filePath;
    }

    private static void addTitle(Document document) {
        try {
            Font titleFont = new Font(Metrics.PDF_FONT, Metrics.PDF_TITLE_FONT_SIZE, Font.BOLD);
            Paragraph title = new Paragraph("Simulation Summary", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
        } catch (DocumentException e) {
            LOGGER.severe("Failed to add title to PDF: " + e.getMessage());
        }
    }

    private static void addHeading(Document document, String csv_timestamp, String simulation_step, 
                                    String filter_veh_color,
                                    boolean filter_congested_edges) {
        try {
            Font headingFont = new Font(Metrics.PDF_FONT, Metrics.PDF_HEADING_FONT_SIZE, Font.ITALIC);
            // Heading 1: Timestamp simulation started
            Paragraph Heading1 = new Paragraph("\nSimulation started at: " + 
                        csv_timestamp.replaceAll("_", ":"), 
                        headingFont);
            Heading1.setAlignment(Element.ALIGN_RIGHT);
            document.add(Heading1);
            // Heading 2: Created timestamp
            String created_timestamp = LocalDateTime.now().toString().substring(0, 19).replace("T", " ");
            Paragraph Heading2 = new Paragraph("Exported at: " + created_timestamp, headingFont);
            Heading2.setAlignment(Element.ALIGN_RIGHT);
            document.add(Heading2);
            // Heading 3: Simulation step
            Paragraph Heading3 = new Paragraph("at simulation step: " + simulation_step, headingFont);
            Heading3.setAlignment(Element.ALIGN_RIGHT);
            document.add(Heading3);
            // Heading 4 (optional): Filter applied
            if(filter_veh_color.equals("") || !filter_congested_edges) {return;} // skip if no filter applied
            String heading4_text = "Filter applied: ";
            if(!filter_veh_color.equals("") && filter_congested_edges) {
                heading4_text += "Vehicle color = " + filter_veh_color + ", Only congested edges";
            } else if (!filter_veh_color.equals("")) {
                heading4_text += "Vehicle color = " + filter_veh_color;
            } else {
                heading4_text += "Only congested edges";
            } 
            Paragraph Heading4 = new Paragraph(heading4_text, headingFont);
            Heading4.setAlignment(Element.ALIGN_RIGHT);
            document.add(Heading4);
        } catch (DocumentException e) {
            LOGGER.severe("Failed to add heading to PDF: " + e.getMessage());
        }
    }

    private static void addObjectCount(Document document, String edgeCount, String tlsCount) {
        try {
            Font normalFont = new Font(Metrics.PDF_FONT, Metrics.PDF_NORMAL_FONT_SIZE, Font.NORMAL);
            Paragraph objectCount = new Paragraph("\nTotal number of edges: " + edgeCount + 
                                                    "\nTotal number of traffic lights: " + tlsCount,
                                                    normalFont);
            objectCount.setAlignment(Element.ALIGN_LEFT);
            document.add(objectCount);
        } catch (DocumentException e) {
            LOGGER.severe("Failed to add object count to PDF: " + e.getMessage());
        }
    }

    private static void addEdgeTable(Document document, List<String[]> edge_table_data) {
        try {
            // Create table with 3 columns: Edge ID, Lane Count, Length
            PdfPTable table = new PdfPTable(3);
            table.setWidths(new float[]{0.75f, 1f, 1f});
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.setWidthPercentage(50);
            table.setSpacingBefore(15f);
            table.setSpacingAfter(20f);
            // Table header
            Font headerFont = new Font(Metrics.PDF_FONT, Metrics.PDF_NORMAL_FONT_SIZE, Font.BOLD);
            addHeaderCell(table, "Edge ID", headerFont);
            addHeaderCell(table, "No. of Lanes", headerFont);
            addHeaderCell(table, "Length (m)", headerFont);
            // Table data
            for (String[] edgeData : edge_table_data) {
                table.addCell(edgeData[0]);
                // Right align numeric cells
                PdfPCell laneCountCell = new PdfPCell(new Phrase(edgeData[1]));
                laneCountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(laneCountCell);
                PdfPCell lengthCell = new PdfPCell(new Phrase(edgeData[2]));
                lengthCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(lengthCell);
            }
            // Add table to document
            document.add(table);
        } catch (DocumentException e) {
            LOGGER.severe("Failed to add edge table to PDF: " + e.getMessage());
        }
    }
    // Helper method for table headers
    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        table.addCell(cell);
    }



    // ----------------------------------------------------
    // RETRIEVE PDF CONTENT FROM CSV
    // ----------------------------------------------------
    private static void readCSV(String csv_path) {

    }

    /**
     * Retrieve overall data from CSV file for summary
     * @param csv_table
     * @return List<String[]> 1st element is {lastSimulationStep, totalVehicleCount},
     */
    private static final List<String[]> retrieveOverallDataFromCSV(Table csv_table) {
        // Last simulation step
        int lastSimulationStep = csv_table.row(csv_table.rowCount() - 1).getInt("Simulation step");


        // Total vehicle injected
        Table uniqueVehicles = csv_table.selectColumns("vehicle id", "vehicle color").dropDuplicateRows();  // keep one row per vehicle
        int uniqueVehicleCount = uniqueVehicles.rowCount();
        // Total vehicle injected by color
        Table countByColor = uniqueVehicles.summarize("vehicle id", AggregateFunctions.count)
                                .by("vehicle color");
        List<String> colors = countByColor.stringColumn("vehicle color").asList();
        List<Double> counts = countByColor.doubleColumn("Count [vehicle id]").asList();


        // Edge congested at least once
        Table congestedEdgesTable = csv_table.where(csv_table.booleanColumn("edge congestion status").isTrue());
        List<String> congestedEdges = congestedEdgesTable.stringColumn("vehicle is on edge").unique().asList();
        Collections.sort(congestedEdges);
        return new java.util.ArrayList<>();
    }

    private static void retrieveColorVehicleCountChartDataFromCSV(String csv_path) {

    }

    private static void retrieveCongestedEdgeCountChartDataFromCSV(String csv_path) {

    }

    private static void closeCSV() {

    }

    
}
