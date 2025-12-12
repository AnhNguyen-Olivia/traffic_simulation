package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoGeometry;

import javafx.scene.shape.Polygon;

/**
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */
public class JunctionData {
    private String junctionID;
    private Polygon shape = new Polygon();

    /**
     * Constructor
     * @param coordinates SumoGeometry representing the coordinates of the junction shape
     */
    public JunctionData(String junctionID, SumoGeometry coordinates){
        this.junctionID = junctionID;
        this.shape = createPolygon(coordinates);
    }
    
    /**
     * Getter
     */
    public String getJunctionID() {
        return junctionID;
    }
    public Polygon getShape() {
        return shape;
    }

    /**
     * Private helper method: convert SumoGeometry to Polygon
     * @return Polygon representing the junction shape
     */
    private Polygon createPolygon(SumoGeometry sumo_coords) {
        for (de.tudresden.sumo.objects.SumoPosition2D pos : sumo_coords.coords) {
            shape.getPoints().addAll(new Double[]{pos.x, pos.y});
        }
        return shape;
    }
}
