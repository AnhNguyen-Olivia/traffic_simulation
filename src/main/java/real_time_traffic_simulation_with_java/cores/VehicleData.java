package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoColor;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.alias.Metrics;

import javafx.geometry.Point2D;

/**
 * @Unfinished
 */

public class VehicleData {
    public static final double length = Metrics.DEFAULT_VEHICLE_LENGTH;
    public static final double width = Metrics.DEFAULT_VEHICLE_WIDTH;

    public String vehicleID;
    public Point2D top_left_corner;
    public double angle;
    public String color;

    public VehicleData(String vehicleID, double x, double y, double angle, SumoColor color) {
        this.vehicleID = vehicleID;
        this.angle = angle;
        this.color = Color.colorToString(color); // default color is white if no input, can modify return String in alias.Color
    }

    
}
