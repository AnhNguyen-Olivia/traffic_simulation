package real_time_traffic_simulation_with_java.tools.PDFmethod;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.util.logging.Logger;
import java.util.List;
import java.util.Arrays;

import real_time_traffic_simulation_with_java.alias.Metrics;


/**
 * Class to create and add an edge table to a PDF document <br>
 * 3 columns: Edge ID, No. of Lanes, Length (m)
 *      with option to filter only congested edges
 */
public final class EdgeTable {
    private static final Logger LOGGER = Logger.getLogger(EdgeTable.class.getName());
    private static final Font headerFont = new Font(Metrics.PDF_FONT, Metrics.PDF_NORMAL_FONT_SIZE, Font.BOLD);

    /**
     * Add an edge table to a PDF document <br>
     * 3 columns: Edge ID, No. of Lanes, Length (m)
     *      with option to filter only congested edges
     */
    private EdgeTable() {
        // Private constructor to prevent instantiation
    }

    public static void addEdgeTable(Document document, List<String[]> edge_table_data,
                                         boolean filter_congested_edges, String[] congested_edges) {
        try {
            // Create table with 3 columns: Edge ID, Lane Count, Length
            PdfPTable table = new PdfPTable(3);
            setTableProperties(table);
            addTableHeader(table, new String[]{"Edge ID", "No. of Lanes", "Length (m)"});
            addTableRow(table, filter_congested_edges, congested_edges, edge_table_data);
            // Add table to document
            document.add(table);
        } catch (DocumentException e) {
            LOGGER.severe("Failed to add edge table to PDF: " + e.getMessage());
        }
    }


    /**
     * Set properties for the PDF table
     */
    private static void setTableProperties(PdfPTable table) {
        // Ratios of width for each column
        table.setWidths(new float[]{0.75f, 1f, 1f});
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        // Overall table width percentage compared to usable page width
        table.setWidthPercentage(50);
        // Space above and under the table
        table.setSpacingBefore(15f);
        table.setSpacingAfter(20f);
    }

    /**
     * Add table header row
     */
    private static void addTableHeader(PdfPTable table, String[] headers) {
        if(headers == null) {
            LOGGER.warning("Header array is null.");
            return;
        }
        if(headers.length != table.getNumberOfColumns()) {
            LOGGER.warning("Header array length does not match table column count.");
            return;
        }
        for (String header : headers) {
            addHeaderCell(table, header);
        }
    }

    /**
     * Add table content row
     */
    private static void addTableRow(PdfPTable table, boolean filter_congested_edges, 
                                                String[] congested_edges, List<String[]> edge_table_data) {
        if(edge_table_data == null) {
            LOGGER.warning("Row data array is null.");
            return;
        }
        for (String[] edgeData : edge_table_data) {
            if(edgeData.length != table.getNumberOfColumns()) {
                LOGGER.warning("Row data length does not match table column count.");
                continue;
            }
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
    }

    /**
     * Add header cell to the table
     */
    private static void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        table.addCell(cell);
    }
}
