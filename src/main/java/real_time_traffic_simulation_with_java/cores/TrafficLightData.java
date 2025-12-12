package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;
import real_time_traffic_simulation_with_java.alias.Metrics;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;

/**
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */

public class TrafficLightData {
    private String tlID;
    /**
     * Each represents the coordinates of 4 corners of 1 sub-light for a lane
     */
    private List<Coordinate> coordinates;
    /**
     * Each is a 1-char string representing the color state of corresponding sub-light ("r","g","y")
     */
    private List<String> colorList;

    /**
     * Constructor
     * @param coordinates each SumoGeometry represents the coordinates of incoming lane shape for sub-light
     * @param colorList a string representing the color states of the traffic light's sub-lights
     */
    public TrafficLightData(String tlID, List<SumoGeometry> coordinates, String colorList) {
        this.tlID = tlID;
        this.coordinates = convertSumoGeometryToCoordinateList(coordinates);
        this.colorList = convertColorToList(colorList);
    }

    /**
     * Getter
     */
    public String getTlID() {
        return tlID;
    }
    public List<Coordinate> getCoordinates() {
        return coordinates;
    }
    public List<String> getColorList() {
        return colorList;
    }

    /**
     * Setter
     */
    public void setColorList(String colorList) {
        this.colorList = convertColorToList(colorList);
    }

    /**
     * Inner class to represent 4 JavaFX Point2D coordinates
     */
    public class Coordinate {
        public Point2D p1;
        public Point2D p2;
        public Point2D p3;
        public Point2D p4;

        /**
         * Constructor
         * Get coordinates of 4 corners to draw the sub-light on incoming lane
         * @param geometry SumoGeometry representing the incoming lane shape for the sub-light
         */
        public Coordinate(SumoGeometry geometry) {
            SumoPosition2D start_pos = geometry.coords.get(0); // Get the 1st point of the lane shape
            SumoPosition2D end_pos = geometry.coords.get(geometry.coords.size() - 1); // Get the last point of the lane shape
            Point2D end_point = new Point2D(end_pos.x, end_pos.y);
            // Get vector from start to end and perpendicular vector
            Point2D direction
                = new Point2D(end_pos.x - start_pos.x, end_pos.y - start_pos.y).normalize().multiply(Metrics.DEFAULT_LANE_WIDTH);
            Point2D perpendicular_vec 
                = new Point2D(end_pos.y - start_pos.y, -(end_pos.x - start_pos.x)).normalize().multiply(Metrics.TLS_WIDTH);
            // Calculate 4 corner points
            this.p1 = end_point.add(perpendicular_vec);
            this.p2 = end_point.subtract(perpendicular_vec);
            this.p3 = p2.subtract(direction);
            this.p4 = p1.subtract(direction);
        }

        /**
         * toString method for debugging
         */
        @Override
        public String toString() {
            return "P1: " + p1.toString() + ", P2: " + p2.toString() + ", P3: " + p3.toString() + ", P4: " + p4.toString();
        }
    }

    /**
     * Private helper method: convert list of lanes' coordinations to list of Coordinate objects
     */
    private List<Coordinate> convertSumoGeometryToCoordinateList(List<SumoGeometry> geometries) {
        List<Coordinate> coordinateList = new ArrayList<>();
        for (SumoGeometry geometry : geometries) {
            Coordinate coordinate = new Coordinate(geometry);
            coordinateList.add(coordinate);
        }
        return coordinateList;
    }
    /**
     * Private helper method: convert color string to list of single-char strings for each sub-light
     */
    private List<String> convertColorToList(String colorString) {
        List<String> colorlist = new ArrayList<>();
        for (int i = 0; i < colorString.length(); i++) {
            colorlist.add(String.valueOf(colorString.charAt(i)));
        }
        return colorlist;
    }

}
