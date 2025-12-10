package real_time_traffic_simulation_with_java.cores;

public class VehicleData {
    public String vehicleID;
    public double x;
    public double y;
    public double angle;
    public String color;

    public VehicleData(String vehicleID, double x, double y, double angle, String color) {
        this.vehicleID = vehicleID;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.color = color;
    }
}
