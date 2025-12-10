package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoColor;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.alias.Metrics;

public class VehicleData {
    public String vehicleID;
    public double x;
    public double y;
    public double length = Metrics.DEFAULT_VEHICLE_LENGTH;
    public double width = Metrics.DEFAULT_VEHICLE_WIDTH;
    public double angle;
    public String color;

    public VehicleData(String vehicleID, double x, double y, double angle, SumoColor color) {
        this.vehicleID = vehicleID;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.color = Color.colorToString(color); // "RED", "GREEN", "BLUE", "BLACK", "WHITE" , WHITE as default
    }
}
