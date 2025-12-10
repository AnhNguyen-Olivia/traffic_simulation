package real_time_traffic_simulation_with_java.cores;
import java.util.List;

import de.tudresden.sumo.objects.SumoPosition2D;

public class LaneData {
    public String laneID;
    public String edgeID;
    public double length;
    public double width = 3.2;
    public List<SumoPosition2D> coordinates;

    public LaneData(String laneID, String edgeID, double length, List<SumoPosition2D> coordinates) {
        this.laneID = laneID;
        this.edgeID = edgeID;
        this.length = length;
        this.coordinates = coordinates;
    }
}
