package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D; 
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.alias.Metrics;

import javafx.scene.shape.Rectangle;

public class VehicleData {
    private String vehicleID;
    private Rectangle shape;
    private String color;
    
    // NEW FIELDS for Simulation State
    private SumoPosition2D position;
    private double angle;
    
    private String laneId;
    private double s; // meters along lane
	private Lane lane;

    /**
     * Constructor
     * Initializes the vehicle's state and creates the centered JavaFX shape.
     * @param vehicleID The unique identifier.
     * @param x Initial X position (meters).
     * @param y Initial Y position (meters).
     * @param angle Initial angle (degrees).
     * @param color Initial color.
     */
    public VehicleData(String vehicleID, double x, double y, double angle, SumoColor color) {
        this.vehicleID = vehicleID;
        this.color = Color.colorToString(color);
        
        // Initialize state variables
        this.position = new SumoPosition2D(x, y);
        this.angle = angle;
        
        // Create the shape, centered at (0,0) for MapPanel's efficient rendering
        this.shape = createShape(vehicleID, this.color);
    }

    /**
     * Getter methods
     */
    public String getVehicleID() {
        return vehicleID;
    }
    public Rectangle getShape() {
        return shape;
    }
    public String getColor() {
        return color;
    }
    public SumoPosition2D getPosition() {
        return position;
    }
    public double getAngle() {
        return angle;
    }

    /**
     * Setter methods: Used by SumoNetworkLoader to update the vehicle's state
     */
    public void setPosition(SumoPosition2D position) {
        this.position = position;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
    
    // You may also want a setter for color if it changes during simulation
    public void setColor(SumoColor color) {
         this.color = Color.colorToString(color);
    }

    /**
     * Private helper method: Creates a Rectangle shape centered at (0, 0).
     * The MapPanel handles the actual translation and rotation in the view layer.
     */
    private Rectangle createShape(String vehicleID, String color) {
        // Create the rectangle with its top-left corner at (-L/2, -W/2)
        // This effectively centers the rectangle at (0, 0).
        Rectangle Shape = new Rectangle(
            -Metrics.DEFAULT_VEHICLE_LENGTH/2, 
            -Metrics.DEFAULT_VEHICLE_WIDTH/2, 
            Metrics.DEFAULT_VEHICLE_LENGTH, 
            Metrics.DEFAULT_VEHICLE_WIDTH
        );
        Shape.setFill(javafx.scene.paint.Paint.valueOf(color));
        // Add border/stroke for clear visibility against the road
        Shape.setStroke(javafx.scene.paint.Color.BLACK); 
        Shape.setStrokeWidth(0.2); 
        Shape.setId(vehicleID);                            
        return Shape;
    }
    
    public String getLaneId() { 
    	return laneId;
    }
    public double getS() { 
    	return s; 
    }

    public void setLanePosition(String laneId, double s) {
        this.laneId = laneId;
        this.s = s;
    }
    
    public void setLane(Lane lane) {
    	this.lane = lane;
    }
}