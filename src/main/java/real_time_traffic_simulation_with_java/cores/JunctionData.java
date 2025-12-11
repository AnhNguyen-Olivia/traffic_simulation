package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoGeometry;

import java.util.List;
import javafx.geometry.Point2D;

/**
 * @Finished
 * @Tested
 */
public class JunctionData {
    public String junctionID;
    public List<Point2D> coordinates;

    public JunctionData(String junctionID, SumoGeometry coordinates){
        this.junctionID = junctionID;
        this.coordinates = get_coordinates(coordinates);
    }

    private List<Point2D> get_coordinates(SumoGeometry sumo_coords) {
        List<Point2D> coordinates = new java.util.ArrayList<>();
        for (de.tudresden.sumo.objects.SumoPosition2D pos : sumo_coords.coords) {
            coordinates.add(new Point2D(pos.x, pos.y));
        }
        return coordinates;
    }
}
