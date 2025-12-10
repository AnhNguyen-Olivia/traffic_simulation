package real_time_traffic_simulation_with_java.cores;
import java.util.List;

import de.tudresden.sumo.objects.SumoPosition2D;

public class LaneData {
    public String laneID;
    public String edgeID;
    public double length;
    public double width;
    public List<SumoPosition2D> coordinates;

    public LaneData(String laneID, String edgeID, double length, double width, List<SumoPosition2D> coordinates) {
        this.laneID = laneID;
        this.edgeID = edgeID;
        this.length = length;
        this.width = width;
        this.coordinates = coordinates;
    }
}
