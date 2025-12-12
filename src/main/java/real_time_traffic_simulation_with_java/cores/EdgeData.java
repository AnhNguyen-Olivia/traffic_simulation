package real_time_traffic_simulation_with_java.cores;

import java.util.List;
import java.util.ArrayList;

import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;
import real_time_traffic_simulation_with_java.alias.Metrics;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;

/**
 * @Unfinished
 * TODO: finish edge corners and lane dividers calculation
 * @Test Incomplete
 * @Javadoc Incomplete
 */

public class EdgeData {
    private String edgeID;
    private List<Line> lane_dividers = new ArrayList<>(); // start and end coordinates for each lane divider
    private List<Point2D> edge_corners = new ArrayList<>(); // 4 points for 4 corners
    private int number_of_lanes;
    private double width;


    /**
     * Constructor
     * @param coordinates each SumoGeometry represents the coordinates of 1 lane within the edge
     */
    public EdgeData(String edgeID, int number_of_lanes, List<SumoGeometry> coordinates) {
        this.edgeID = edgeID;
        this.number_of_lanes = number_of_lanes;
        this.width = number_of_lanes * Metrics.DEFAULT_LANE_WIDTH;
        for (SumoGeometry pos : coordinates) {
            this.edge_corners.add(new Point2D(pos.x, pos.y));
        }
    }

    /**
     * Getter
     */
    public String getEdgeID() {
        return edgeID;
    }
    public List<Point2D> getLaneDividers() {
        return lane_dividers;
    }
    public List<Point2D> getEdgeCorners() {
        return edge_corners;
    }
    public int getNumberOfLanes() {
        return number_of_lanes;
    }
    public double getWidth() {
        return width;
    }

    /**
     * Inner class representing 2 JavaFX Point2D coordinates to draw line
     */
    public class Line {
        public Point2D start;
        public Point2D end;
        public Line(Point2D start, Point2D end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Private helper method:Calculate midpoint of the edge (start and end point of the center line of the edge)
     */
    private Line calculateMidPoint(int number_of_lanes, List<SumoGeometry> coordinates) {
        SumoPosition2D start_pos;
        SumoPosition2D end_pos;
        if(number_of_lanes == 1) {
            // edge with only 1 lane
            SumoGeometry center_lane = coordinates.get(0);
            start_pos = center_lane.coords.get(0);
            end_pos = center_lane.coords.get(center_lane.coords.size() - 1);
        } else {
            // edge with multiple lanes
            // Get the leftmost and rightmost lane start & end coordinations to calculate the center line
            SumoGeometry leftmost_lane = coordinates.get(0);
            SumoGeometry rightmost_lane = coordinates.get(coordinates.size() - 1);
            SumoPosition2D left_start_pos = leftmost_lane.coords.get(0);
            SumoPosition2D left_end_pos = leftmost_lane.coords.get(leftmost_lane.coords.size() - 1);
            SumoPosition2D right_start_pos = rightmost_lane.coords.get(0);
            SumoPosition2D right_end_pos = rightmost_lane.coords.get(rightmost_lane.coords.size() - 1);
            // Calculate midpoints
            start_pos = new SumoPosition2D(
                (left_start_pos.x + right_start_pos.x) / 2,
                (left_start_pos.y + right_start_pos.y) / 2
            );
            end_pos = new SumoPosition2D(
                (left_end_pos.x + right_end_pos.x) / 2,
                (left_end_pos.y + right_end_pos.y) / 2
            );
        }
        return new Line(new Point2D(start_pos.x, start_pos.y), new Point2D(end_pos.x, end_pos.y));
    }

    /**
     * Private helper method: Calculate parallel and perpendicular unit vectors of the edge
     * Reuse Line class to represent the vectors
     * Start point represents parallel vector, end point represents perpendicular vector
     * @param midLine Line object representing the mid line of the edge
     * @return Line(parallel_vector, perpendicular_vector)
     */
    private Line calculateVectors(Line midLine) {
        // Calculate parallel vector
        Point2D parallel_vector = midLine.end.subtract(midLine.start).normalize();
        // Calculate perpendicular vector
        Point2D perpendicular_vector = new Point2D(-parallel_vector.getY(), parallel_vector.getX());
        return new Line(parallel_vector, perpendicular_vector);
    }
}
