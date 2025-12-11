package real_time_traffic_simulation_with_java.cores;

import java.util.List;
import java.util.ArrayList;

import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;
import real_time_traffic_simulation_with_java.alias.Metrics;

import javafx.geometry.Point2D;

/**
 * @Unfinished
 */

public class EdgeData {
    public static final double lane_width = Metrics.DEFAULT_LANE_WIDTH;

    public String edgeID;
    public int number_of_lanes;
    public double length;
    public List<Point2D> lane_dividers = new ArrayList<>(); // 2 points each lane divider [start_div1, end_div1, start_div2, end_div2, ...]
    public List<Point2D> edge_corners = new ArrayList<>(); // 4 points for 4 corners

    public EdgeData(String edgeID, int number_of_lanes, double length, List<SumoGeometry> coors) {
        this.edgeID = edgeID;
        this.number_of_lanes = number_of_lanes;
        this.length = length;
        for (SumoGeometry pos : coors) {
            this.edge_corners.add(new Point2D(pos.x, pos.y));
        }
    }
}
