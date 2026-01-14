package real_time_traffic_simulation_with_java.tools;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import tech.tablesaw.api.Table;
import tech.tablesaw.aggregate.AggregateFunctions;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.List;
import java.util.Arrays;

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
            List<String[]> csv_overall_data = retrieveOverallDataFromCSV(csv_table);

            // Write PDF content
            addTitle(document);
            addHeading(document, filter_veh_color, filter_congested_edges, csv_timestamp, csv_overall_data.get(0)[0]);
            addObjectCount(document, filter_veh_color, filter_congested_edges, edge_tls_count[0], edge_tls_count[1], csv_overall_data);
            addEdgeTable(document, edge_table_data, filter_congested_edges, csv_overall_data.get(4));
            
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

    private static void addHeading(Document document, String filter_veh_color, boolean filter_congested_edges, 
                            String csv_timestamp, String simulation_step) {
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
            if(filter_veh_color.equals("") && !filter_congested_edges) {return;} // skip if no filter applied
            StringBuilder heading4_text = new StringBuilder("Filter applied: ");
            if(!filter_veh_color.equals("") && filter_congested_edges) {
                heading4_text.append("Vehicle color = " + filter_veh_color + ", Only congested edges");
            } else if (!filter_veh_color.equals("")) {
                heading4_text.append("Vehicle color = " + filter_veh_color);
            } else {
                heading4_text.append("Only congested edges");
            } 
            Paragraph Heading4 = new Paragraph(heading4_text.toString(), headingFont);
            Heading4.setAlignment(Element.ALIGN_RIGHT);
            document.add(Heading4);
        } catch (DocumentException e) {
            LOGGER.severe("Failed to add heading to PDF: " + e.getMessage());
        }
    }

    private static void addObjectCount(Document document, String filter_veh_color, boolean filter_congested_edges, 
                                            String edgeCount, String tlsCount, List<String[]> csv_overall_data) {
        try {
            // Prepare edge data
            StringBuilder edgeData = new StringBuilder();
            int congestedEdgeCount = csv_overall_data.get(4).length; 
            if(!filter_congested_edges) {
                edgeData.append("Total number of edges: " + edgeCount + "\n");
            }
            edgeData.append("Total number of congested edges: " + congestedEdgeCount + "\n");
            if(congestedEdgeCount > 0) {
                edgeData.append("        Congested edge ID: " + Arrays.toString(csv_overall_data.get(4)));
            }
            // Prepare vehicle data
            String vehicleData;
            String[] colors = csv_overall_data.get(2);
            String[] counts = csv_overall_data.get(3);
            if(filter_veh_color.equals("")) {
                String totalVehicleCount = csv_overall_data.get(1)[0];
                StringBuilder vehicleCountText = new StringBuilder("Total number of vehicles injected: " + totalVehicleCount + "\nVehicle count by color:\n");
                for (int i = 0; i < colors.length; i++) {
                    vehicleCountText.append("        - ").append(colors[i]).append(" vehicles: ").append(counts[i]).append("\n");
                }
                vehicleData = vehicleCountText.toString();
            } else {
                int index = Arrays.asList(colors).indexOf(filter_veh_color);
                vehicleData = "Total number of vehicles injected with color " + filter_veh_color + ": " + counts[index];
            }
            // Create paragraph
            Font normalFont = new Font(Metrics.PDF_FONT, Metrics.PDF_NORMAL_FONT_SIZE, Font.NORMAL);
            Paragraph objectCount = new Paragraph("\n" + edgeData +
                                                    "\nTotal number of traffic lights: " + tlsCount
                                                    + "\n" + vehicleData,
                                                    normalFont);
            objectCount.setAlignment(Element.ALIGN_LEFT);
            document.add(objectCount);
        } catch (DocumentException e) {
            LOGGER.severe("Failed to add object count to PDF: " + e.getMessage());
        }
    }

    private static void addEdgeTable(Document document, List<String[]> edge_table_data,
                                         boolean filter_congested_edges, String[] congested_edges) {
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
                if(filter_congested_edges && !Arrays.asList(congested_edges).contains(edgeData[0])) {
                    continue;
                }
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
    /**
     * Retrieve overall data from CSV file for summary
     * @param csv_table
     * @return List<String[]> {exportSimulationStep, totalVehicleCount, [color], [count by color], [congested edges]}
     */
    private static final List<String[]> retrieveOverallDataFromCSV(Table csv_table) {
        List<String[]> data = new java.util.ArrayList<>();
        // Last simulation step
        int exportSimulationStep = csv_table.row(csv_table.rowCount() - 1).getInt("Simulation step");
        data.add(new String[]{String.valueOf(exportSimulationStep)});
        // Total vehicle injected
        Table uniqueVehicles = csv_table.selectColumns("vehicle id", "vehicle color").dropDuplicateRows();  // keep one row per vehicle
        int uniqueVehicleCount = uniqueVehicles.rowCount();
        data.add(new String[]{String.valueOf(uniqueVehicleCount)});
        // Total vehicle injected by color
        Table countByColor = uniqueVehicles.summarize("vehicle id", AggregateFunctions.count)
                                .by("vehicle color");
        String[] colors = countByColor.stringColumn("vehicle color").asObjectArray();
        Double[] counts_double = countByColor.doubleColumn("Count [vehicle id]").asObjectArray();
        String[] counts = Arrays.stream(counts_double)
                         .map(d -> String.format("%.0f", d))
                         .toArray(String[]::new);
        data.add(colors);
        data.add(counts);
        // Edge congested at least once
        Table congestedEdgesTable = csv_table.where(csv_table.booleanColumn("edge congestion status").isTrue());
        String[] congestedEdges = congestedEdgesTable.stringColumn("vehicle is on edge").unique().asObjectArray();
        Arrays.sort(congestedEdges);
        data.add(congestedEdges);
        return data;
    }

    // private static void retrieveColorVehicleCountChartDataFromCSV(String csv_path) {

    // }

    // private static void retrieveCongestedEdgeCountChartDataFromCSV(String csv_path) {

    // }
}
