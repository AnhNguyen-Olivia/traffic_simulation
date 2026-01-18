package real_time_traffic_simulation_with_java.tools;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import tech.tablesaw.api.Table;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.List;
import java.nio.file.Paths;

import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.tools.PDFmethod.*;

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

    private PDFExporter() {
        // Private constructor to prevent instantiation
    }

    /**
     * Export summary PDF file from data from SUMO and CSV log file, 
     *      allow filtering options: vehicle color and congested edges only <br>
     * @param csv_path Path to the CSV log file of current simulation
     * @param csv_timestamp Timestamp when the simulation started (used in CSV file name)
     * @param filter_veh_color Color of vehicle to filter, empty string for no filter
     * @param filter_congested_edges Whether to filter only congested edges
     * @param data_from_simulation_engine List<String[]>, 1st element is {edgeCount, tlsCount}, 2nd element is {exportedSimulationStep}, each next are {edgeID, laneCount, length}
     */
    public static void exportSummary(String csv_path, String csv_timestamp, 
                                    String filter_veh_color, 
                                    boolean filter_congested_edges,
                                    List<String[]> data_from_simulation_engine) {
        // Preparing data from CSV file
        // Overall data: {totalVehicleCount, [color], [count by color], [congested edges]}
        Table csv_table = Table.read().csv(csv_path);
        List<String[]> csv_overall_data = OverallFromCSV.retrieveOverallDataFromCSV(csv_table);
        
        // Preparing data from simulation engine
        // 1st element: {edgeCount, tlsCount}, 2nd element: {exportedSimulationStep}, next elements: {edgeID, laneCount, length}
        String[] edge_tls_count = data_from_simulation_engine.get(0);
        List<String[]> edge_table_data = data_from_simulation_engine.subList(2, data_from_simulation_engine.size());

        try{
            Document document = new Document(PageSize.A4);
            Files.createDirectories(Paths.get(Path.PdfLogFolder));
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(generatePath(csv_timestamp)));
            document.open();

            // Write PDF content
            addTitle(document);
            Heading.addHeading(document, filter_veh_color, filter_congested_edges, csv_timestamp, data_from_simulation_engine.get(1)[0]);
            ObjectCount.addObjectCount(document, filter_veh_color, filter_congested_edges, edge_tls_count[0], edge_tls_count[1], csv_overall_data);
            EdgeTable.addEdgeTable(document, edge_table_data, filter_congested_edges, csv_overall_data.get(3));

            // Add chart image
            if(csv_table.rowCount() > 0) {
                PDFChart.addVehicleCountChart(csv_table, document, writer, filter_veh_color);
                PDFChart.addCongestedEdgeCountChart(csv_table, document, writer, data_from_simulation_engine.get(1)[0]);
            }
            
            document.close();
            LOGGER.info(String.format("PDF summary {%s [%d]} created successfully.", csv_timestamp, index));
        } catch (Exception e) {
            LOGGER.severe("Failed to create PDF summary: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Private helper method: Generate unique file path for PDF file <br>
     * Format: {csv_timestamp} Summary {index}.pdf <br>
     * @param csv_timestamp Timestamp when the simulation started (used in CSV file name)
     */
    private static String generatePath(String csv_timestamp) {
        index += 1;
        String filePath = Path.PdfLogFolder + csv_timestamp + " Summary " + index + ".pdf";
        return filePath;
    }

    /**
     * Private helper method: Add title to the PDF document
     * Title: "Simulation Summary", centered at the top of the first page, bold font
     */
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
}