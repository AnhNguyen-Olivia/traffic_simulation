package real_time_traffic_simulation_with_java.cores;

import java.util.ArrayList;
import java.util.List;

import de.tudresden.sumo.objects.SumoPosition2D;

public class Lane {
	public String id;
    public String edgeId;
    public double speed;
    public double length;
    public double width = 3.2;
    public List<SumoPosition2D> shape = new ArrayList<>();

    public Lane(String id) {
        this.id = id;
    }
}