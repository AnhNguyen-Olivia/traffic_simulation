package real_time_traffic_simulation_with_java.tools.PDFmethod;

import com.lowagie.text.*;

import java.util.logging.Logger;
import java.util.List;
import java.util.Arrays;

import real_time_traffic_simulation_with_java.alias.Metrics;


/** 
 * Class to add object count information to PDF document <br>
 * Information includes: 
 *      total edge count (if not filtering congested edges) + total congested edge count + congested edge IDs (if any), 
 *      traffic light count, 
 *      total vehicle count (if not filtering by color) + vehicle count by color (only 1 specific color if filtering)
 */
public final class ObjectCount {
    private static final Logger LOGGER = Logger.getLogger(ObjectCount.class.getName());

    private ObjectCount() {
        // Private constructor to prevent instantiation
    }

    public static void addObjectCount(Document document, String filter_veh_color, boolean filter_congested_edges, 
                                            String edgeCount, String tlsCount, List<String[]> csv_overall_data) {
        try {
            Font normalFont = new Font(Metrics.PDF_FONT, Metrics.PDF_NORMAL_FONT_SIZE, Font.NORMAL);
            Paragraph objectCount = new Paragraph(
                        "\nTotal number of traffic lights: " + tlsCount +
                        "\n" + getVehicleString(filter_veh_color, csv_overall_data) +
                        "\n" + getEdgeString(edgeCount, filter_congested_edges, csv_overall_data.get(3)),
                        normalFont);
            objectCount.setAlignment(Element.ALIGN_LEFT);
            document.add(objectCount);
        } catch (DocumentException e) {
            LOGGER.severe("Failed to add object count to PDF: " + e.getMessage());
        }
    }


    private static String getEdgeString(String edgeCount, boolean filter_congested_edges, String[] congestedEdgeIDs) {
        StringBuilder edgeData = new StringBuilder();
        int congestedEdgeCount = congestedEdgeIDs.length; 
        // If filtering congested edges, edgeCount is not shown
        if(!filter_congested_edges) {
            edgeData.append("Total number of edges: " + edgeCount + "\n");
        }
        // Always show congested edge count
        edgeData.append("Total number of congested edges: " + congestedEdgeCount);
        // Show congested edge IDs if any
        if(congestedEdgeCount > 0) {
            edgeData.append("\n        Congested edge ID: " + Arrays.toString(congestedEdgeIDs));
        }
        return edgeData.toString();
    }

    private static String getVehicleString(String filter_veh_color, List<String[]> csv_overall_data) {
        String vehicleData;
        // If no vehicle injected
        if(csv_overall_data.get(0)[0].equals("0")) {  
            vehicleData = "No vehicle has been injected.";
        } else {
            String[] colors = csv_overall_data.get(1);
            String[] counts = csv_overall_data.get(2);
            // If no filter on vehicle color: show total vehicle count and count by each color
            if(filter_veh_color.equals("")) {
                String totalVehicleCount = csv_overall_data.get(0)[0];
                StringBuilder vehicleCountText = new StringBuilder(
                        "Total number of vehicles injected: " + 
                        totalVehicleCount + 
                        "\nVehicle count by color:\n");
                for (int i = 0; i < colors.length; i++) {
                    vehicleCountText.append("        - ").
                            append(colors[i]).
                            append(" vehicles: ").
                            append(counts[i]).
                            append("\n");
                }
                vehicleData = vehicleCountText.toString();
            } else {
                // If filtering by specific vehicle color: show count for that color only
                int index = Arrays.asList(colors).indexOf(filter_veh_color);
                vehicleData = "Total number of vehicles injected with color " + filter_veh_color + ": " + counts[index];
            }
        }
        return vehicleData;
    }
}
