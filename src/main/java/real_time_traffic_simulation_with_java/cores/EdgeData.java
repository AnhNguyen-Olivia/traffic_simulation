package real_time_traffic_simulation_with_java.cores;

import java.util.List;
import java.util.ArrayList;

import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.alias.Color;

import javafx.geometry.Point2D;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.Group; 


/**
 * Represents the visual data of an edge in the simulation, 
 *      grouping a rectangle representing the edge and dashed lines representing lane dividers.
 */
public class EdgeData extends Group {
    /** Congestion status */
    private boolean congested = false;
    /** High travel time index status */
    private boolean highHaltingRate = false;
    /** Duration of high travel time index */
    private int highHaltingDuration = 0;
    /**
     * Represents the visual data of an edge in the simulation, 
     *      grouping a rectangle representing the edge and dashed lines representing lane dividers.
     * @param edgeID ID of the edge
     * @param number_of_lanes number of lanes within the edge
     * @param coordinates List of SumoGeometry objects representing the coordinates of each lane within the edge
     */
    public EdgeData(String edgeID, int number_of_lanes, List<SumoGeometry> coordinates) {
        // Draw edge shape
        Polygon edge_shape = createPolygon(number_of_lanes, number_of_lanes * Metrics.DEFAULT_LANE_WIDTH, coordinates);
        edge_shape.setFill(Color.ROAD);
        edge_shape.setStroke(Color.ROAD_BORDER);
        edge_shape.setStrokeWidth(Metrics.EDGE_DIVIDER_WEIGHT);
        // Draw lane dividers
        List<Line> lane_dividers = calculateLaneDividers(number_of_lanes, number_of_lanes * Metrics.DEFAULT_LANE_WIDTH, coordinates);
        for(Line lane_divider: lane_dividers) {
            lane_divider.setStroke(Color.LANE_DIVIDER);
            lane_divider.setStrokeWidth(Metrics.LANE_DIVIDER_WEIGHT);
            lane_divider.getStrokeDashArray().addAll(Metrics.LANE_DASHED_LENGTH, Metrics.LANE_DASHED_GAP);
        }
        // Grouping
        this.getChildren().add(edge_shape);
        this.getChildren().addAll(lane_dividers);
        this.setId(edgeID);
        this.setPickOnBounds(false); // Allow mouse event only on the shape, not the bounding box
    }


    /** Getter for congestion status */
    public boolean isCongested() {
        return congested;
    }

    /** */
    public void setColor() {
        Polygon edge_shape = (Polygon)this.getChildren().get(0);
        if(isCongested() && edge_shape.getFill() != Color.CONGESTED_ROAD) {
            edge_shape.setFill(Color.CONGESTED_ROAD);
        } else if (!isCongested() && edge_shape.getFill() != Color.ROAD) {
            edge_shape.setFill(Color.ROAD);
        }
    }


    /**
     * Update congested status based on average number of halting vehicles/edge's lane
     * An edge is considered congested if number of halting vehicles/lane 
     *      maintain above a certain threshold for a certain duration.
     * @param haltingRate current average number of halting vehicles per lane
     */
    public void updateCongestedStatus(double haltingRate) {
        if(haltingRate >= Metrics.HIGH_HALTING_RATE_THRESHOLD) {
            highHaltingDuration++;
            if(highHaltingRate == false)
                highHaltingRate = true;
            if(highHaltingDuration >= Metrics.HIGH_HALTING_DURATION_THRESHOLD)
                congested = true;
        } else {
            if(highHaltingRate == true) {
                if(congested == true)
                    congested = false;
                highHaltingRate = false;
                highHaltingDuration = 0;
            }
        }
    }


    // ---------------------------------------------------------
    // Private helper methods for drawing edge shape and lane dividers
    // ---------------------------------------------------------
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
     * Private helper method: Calculate coordinations of lane dividers within the edge.
     * Lines object representing lane dividers within the edge
     * @return List<Line> using library javafx.scene.shape.Line
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
