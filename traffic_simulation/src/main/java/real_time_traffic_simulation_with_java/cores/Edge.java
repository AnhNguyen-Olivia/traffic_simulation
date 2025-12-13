package real_time_traffic_simulation_with_java.cores;

import java.util.ArrayList;
import java.util.List;

public class Edge {
	public String id;
    public List<Lane> lanes = new ArrayList<>();

    public Edge(String id) {
        this.id = id;
    }

    public void addLane(Lane lane) {
        lanes.add(lane);
    }
}
