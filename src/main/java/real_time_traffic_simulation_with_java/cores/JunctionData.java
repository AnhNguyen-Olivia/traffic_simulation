package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoGeometry;

import javafx.scene.shape.Polygon;

/**
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */
public class JunctionData extends Polygon {
    /**
     * Constructor
     * @param coordinates SumoGeometry representing the coordinates of the junction shape
     */
    public JunctionData(String junctionID, SumoGeometry coordinates){
        for (de.tudresden.sumo.objects.SumoPosition2D pos : coordinates.coords) {
            this.getPoints().addAll(new Double[]{pos.x, pos.y});
        }
        this.setFill(javafx.scene.paint.Color.DIMGRAY);
        this.setId(junctionID);
    }
}
