package real_time_traffic_simulation_with_java.tools.PDFmethod;

import com.lowagie.text.*;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import real_time_traffic_simulation_with_java.alias.Metrics;


/**
 * This class provides a method to add headings to a PDF document.
 * The headings include: 
 *      simulation start time, 
 *      export time, simulation step,
 *      simulation step,
 *      applied filters (if any).
 */
public final class Heading {
    private static final Logger LOGGER = Logger.getLogger(Heading.class.getName());
    private static final Font headingFont = new Font(Metrics.PDF_FONT, Metrics.PDF_HEADING_FONT_SIZE, Font.ITALIC);

    private Heading() {
        // Private constructor to prevent instantiation
    }

    /**
     * Add headings to a PDF document (aligned to the right, italic font): 
     *      simulation start time stamp, 
     *      export time stamp,
     *      simulation step when exported,
     *      applied filters (if any).
     */
    public static void addHeading(Document document, String filter_veh_color, boolean filter_congested_edges, 
                            String csv_timestamp, String simulation_step) {
        try {
            // Empty line for spacing
            addHeadingAlignedRight(document, "");

            // Heading 1: Timestamp when simulation started
            addHeadingAlignedRight(document, "Simulation started at: " + csv_timestamp.replaceAll("_", ":"));

            // Heading 2: Created timestamp
            String created_timestamp = LocalDateTime.now().toString().substring(0, 19).replace("T", " ");
            addHeadingAlignedRight(document, "Exported at: " + created_timestamp);

            // Heading 3: Simulation step
            addHeadingAlignedRight(document, "at simulation step: " + simulation_step);

            // Heading 4 (optional): Filter applied
            if(!filter_veh_color.equals("") && filter_congested_edges) {
                addHeadingAlignedRight(document, getFilterAppliedString(filter_veh_color, filter_congested_edges));
            } 

        } catch (DocumentException e) {
            LOGGER.severe("Failed to add heading to PDF: " + e.getMessage());
        }
    }


    /**
     * Private helper method to add headings to a PDF document.
     */
    private static void addHeadingAlignedRight(Document document, String headingText) throws DocumentException {
        Paragraph heading = new Paragraph(headingText, headingFont);
        heading.setAlignment(Element.ALIGN_RIGHT);
        document.add(heading);
    }

    /**
     * Private helper method to get string for filter applied.
     */
    private static String getFilterAppliedString(String filter_veh_color, boolean filter_congested_edges) {
        StringBuilder filter_applied = new StringBuilder("Filter applied: ");
        if(!filter_veh_color.equals("") && filter_congested_edges) {
            filter_applied.append("Vehicle color = " + filter_veh_color + ", Only congested edges");
        } else if (!filter_veh_color.equals("")) {
            filter_applied.append("Vehicle color = " + filter_veh_color);
        } else {
            filter_applied.append("Only congested edges");
        } 
        return filter_applied.toString();
    }
}
