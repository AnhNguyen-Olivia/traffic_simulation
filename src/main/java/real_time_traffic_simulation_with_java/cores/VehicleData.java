package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoColor;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.alias.Metrics;

import javafx.scene.shape.Rectangle;

/**
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */

public class VehicleData {
    private String vehicleID;
    private Rectangle shape;

    /**
     * Constructor
     * @param color default color is white if invalid input, can modify return String in alias.Color
     */
    public VehicleData(String vehicleID, double x, double y, double angle, SumoColor color) {
        this.vehicleID = vehicleID;
        this.shape = createShape(vehicleID, x, y, angle, Color.colorToString(color));
    }

    /**
     * Getter
     */
    public String getVehicleID() {
        return vehicleID;
    }
    public Rectangle getShape() {
        return shape;
    }

    /**
     * Private helper method: calculate top-left corner from center position
     */
    private Rectangle createShape(String vehicleID, double x, double y, double angle, String color) {
        // Top-left corner of JavaFX Rectangle is bottom-left corner of SUMO vehicle
        double translate_vec = Metrics.DEFAULT_VEHICLE_LENGTH/2 - Metrics.DEFAULT_VEHICLE_WIDTH/2;
        Rectangle Shape = new Rectangle(x - translate_vec, 
                                        y - translate_vec, 
                                        Metrics.DEFAULT_VEHICLE_LENGTH, 
                                        Metrics.DEFAULT_VEHICLE_WIDTH
                                        );  
        Shape.setRotate(-(angle + 90)); // JavaFx 0 degree is to the right, SUMO 0 degree is to the top
        Shape.setFill(javafx.scene.paint.Paint.valueOf(color));
        Shape.setArcWidth(Metrics.DEFAULT_VEHICLE_WIDTH/10);
        Shape.setArcHeight(Metrics.DEFAULT_VEHICLE_WIDTH/10);
        Shape.setId(vehicleID);                            
        return Shape;
    }

    
}
