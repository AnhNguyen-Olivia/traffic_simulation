package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoGeometry;

import java.util.List;
import javafx.geometry.Point2D;

/**
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */
public class JunctionData {
    private String junctionID;
    /**
     * Coordinates of the junction shape
     */
    private List<Point2D> coordinates;

    /**
     * Constructor
     * @param coordinates SumoGeometry representing the coordinates of the junction shape
     */
    public JunctionData(String junctionID, SumoGeometry coordinates){
        this.junctionID = junctionID;
        this.coordinates = get_coordinates(coordinates);
    }
    
    /**
     * Getter
     */
    public String getJunctionID() {
        return junctionID;
    }
    public List<Point2D> getCoordinates() {
        return coordinates;
    }

    /**
     * Private helper method: convert SumoGeometry to List<Point2D>
     * @return List<Point2D> of coordinates to draw junction shape
     */
    private List<Point2D> get_coordinates(SumoGeometry sumo_coords) {
        List<Point2D> coordinates = new java.util.ArrayList<>();
        for (de.tudresden.sumo.objects.SumoPosition2D pos : sumo_coords.coords) {
            coordinates.add(new Point2D(pos.x, pos.y));
        }
        return coordinates;
    }
}
