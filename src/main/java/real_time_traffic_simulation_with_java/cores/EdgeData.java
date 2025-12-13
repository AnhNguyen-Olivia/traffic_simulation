package real_time_traffic_simulation_with_java.cores;

import java.util.List;
import java.util.ArrayList;

import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;
import real_time_traffic_simulation_with_java.alias.Metrics;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.Group;

/**
 * @Unfinished
 * @Test Completed
 * @Javadoc Completed
 */

public class EdgeData {
    private String edgeID;
    /**
     * Lines object representing lane dividers within the edge
     * @return List<Line> using library javafx.scene.shape.Line
     */
    /**
     * Grouping a Polygon edge and Line objects representing lane dividers within the edge
     */
    private Group edge_group;
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
        this.edge_group = createEdgeGroup(edgeID, number_of_lanes, coordinates);
    }

    /**
     * Getter
     */
    public String getEdgeID() {
        return edgeID;
    }
    public Group getShape() {
        return edge_group;
    }
    public int getNumberOfLanes() {
        return number_of_lanes;
    }
    public double getWidth() {
        return width;
    }


    /**
     * Private helper method: Grouping the edge shape and lane dividers together
     */
    private Group createEdgeGroup(String edgeID, int number_of_lanes, List<SumoGeometry> coordinates) {
        // Draw edge shape
        Polygon edge_shape = createPolygon(number_of_lanes, number_of_lanes * Metrics.DEFAULT_LANE_WIDTH, coordinates);
        edge_shape.setFill(javafx.scene.paint.Color.SLATEGRAY);
        // Draw lane dividers
        List<Line> lane_dividers = calculateLaneDividers(number_of_lanes, number_of_lanes * Metrics.DEFAULT_LANE_WIDTH, coordinates);
        for(Line lane_divider: lane_dividers) {
            lane_divider.setStroke(javafx.scene.paint.Color.WHITE);
            lane_divider.getStrokeDashArray().addAll(10.0, 5.0);
        }
        // Grouping
        Group edgeGroup = new Group();
        edgeGroup.getChildren().add(edge_shape);
        edgeGroup.getChildren().addAll(lane_dividers);
        edgeGroup.setId(edgeID);
        return edgeGroup;
    }


    /**
     * Private helper method: Create Polygon by calculating coordinations of the edge corners
     */
    private Polygon createPolygon(int number_of_lanes, double edge_width, List<SumoGeometry> coordinates) {
        Line midLine = calculateMidPoint(number_of_lanes, coordinates);
        Point2D start = new Point2D(midLine.getStartX(), midLine.getStartY());
        Point2D end = new Point2D(midLine.getEndX(), midLine.getEndY());
        Point2D scaled_perpendicular_vector = calculateVectors(midLine).multiply(edge_width/2);
        // Calculate 4 corners
        Point2D p1 = start.add(scaled_perpendicular_vector);
        Point2D p2 = start.subtract(scaled_perpendicular_vector);
        Point2D p3 = end.subtract(scaled_perpendicular_vector);
        Point2D p4 = end.add(scaled_perpendicular_vector);
        return new Polygon(
            p1.getX(), p1.getY(),
            p2.getX(), p2.getY(),
            p3.getX(), p3.getY(),
            p4.getX(), p4.getY()
        );
    }

    /**
     * Private helper method: Calculate coordinations of lane dividers within the edge
     */
    private List<Line> calculateLaneDividers(int number_of_lanes, double edge_width, List<SumoGeometry> coordinates) {
        List<Line> lane_dividers = new ArrayList<>();
        Line midLine = calculateMidPoint(number_of_lanes, coordinates);
        Point2D start = new Point2D(midLine.getStartX(), midLine.getStartY());
        Point2D end = new Point2D(midLine.getEndX(), midLine.getEndY());
        Point2D perpendicular_vector = calculateVectors(midLine);
        double scale_factor = edge_width/2 - Metrics.DEFAULT_LANE_WIDTH;
        for(int i = 1; i < number_of_lanes; i++) {
            Point2D scaled_vector = perpendicular_vector.multiply(scale_factor);
            Point2D start_point = start.add(scaled_vector);
            Point2D end_point = end.add(scaled_vector);
            lane_dividers.add(new Line(
                start_point.getX(), start_point.getY(),
                 end_point.getX(), end_point.getY()
            ));
            scale_factor -= Metrics.DEFAULT_LANE_WIDTH;
        }
        return lane_dividers;
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
        return new Line(start_pos.x, start_pos.y, end_pos.x, end_pos.y);
    }

    /**
     * Private helper method: Calculate perpendicular unit vectors of the edge
     * @param midLine Line object representing the mid line of the edge
     */
    private Point2D calculateVectors(Line midLine) {
        // Calculate perpendicular vector
        Point2D perpendicular_vector = new Point2D(midLine.getStartY() - midLine.getEndY(), 
                                                    midLine.getEndX() - midLine.getStartX()).normalize();
        return perpendicular_vector;
    }
}
