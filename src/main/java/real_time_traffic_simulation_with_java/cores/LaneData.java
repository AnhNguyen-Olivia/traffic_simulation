package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoGeometry;
import real_time_traffic_simulation_with_java.alias.Metrics;

/**
 * @Finished
 * @Tested
 */
public class LaneData {
    public static final double width = Metrics.DEFAULT_LANE_WIDTH;

    public String laneID;
    public String edgeID;
    public double length;
    public SumoGeometry coordinates;

    public LaneData(String laneID, String edgeID, double length, SumoGeometry coordinates) {
        this.laneID = laneID;
        this.edgeID = edgeID;
        this.length = length;
        this.coordinates = coordinates;
    }
}
