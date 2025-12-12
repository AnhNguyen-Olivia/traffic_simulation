package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoColor;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.alias.Metrics;

import javafx.geometry.Point2D;

/**
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */

public class VehicleData {
    private String vehicleID;
    private Point2D top_left_corner;
    private double angle;
    private String color;

    /**
     * Constructor
     * @param color default color is white if invalid input, can modify return String in alias.Color
     */
    public VehicleData(String vehicleID, double x, double y, double angle, SumoColor color) {
        this.vehicleID = vehicleID;
        this.top_left_corner = calculateTopLeftCorner(x, y);
        this.angle = angle;
        this.color = Color.colorToString(color);
    }

    /**
     * Getter
     */
    public String getVehicleID() {
        return vehicleID;
    }
    public Point2D getTopLeftCorner() {
        return top_left_corner;
    }
    public double getAngle() {
        return angle;
    }
    public String getColor() {
        return color;
    }

    /**
     * Private helper method: calculate top-left corner from center position
     */
    private Point2D calculateTopLeftCorner(double x, double y) {
        double translate_vec = Metrics.DEFAULT_VEHICLE_LENGTH/2 - Metrics.DEFAULT_VEHICLE_WIDTH/2;
        return new Point2D(x + translate_vec, y + translate_vec);
    }

    
}
