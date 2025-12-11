package real_time_traffic_simulation_with_java.cores;

import java.util.List;
import de.tudresden.sumo.objects.SumoPosition2D;
import real_time_traffic_simulation_with_java.alias.Metrics;

public class LaneData {
    public static final double width = Metrics.DEFAULT_LANE_WIDTH;

    public String laneID;
    public String edgeID;
    public double length;
    public List<SumoPosition2D> coordinates;

    public LaneData(String laneID, String edgeID, double length, List<SumoPosition2D> coordinates) {
        this.laneID = laneID;
        this.edgeID = edgeID;
        this.length = length;
        this.coordinates = coordinates;
    }
}
