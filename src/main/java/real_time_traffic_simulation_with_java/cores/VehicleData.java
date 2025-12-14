package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoColor;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.alias.Metrics;

/**
 * Vehicle with simple car silhouette
 */
public class VehicleData {
    private String vehicleID;
    private Rectangle shape; // Keep for compatibility
    private Group carGroup;   // Simple car shape

    /**
     * Constructor - creates a nice looking car
     * @param color default color is white if invalid input, can modify return String in alias.Color
     */
    public VehicleData(String vehicleID, double x, double y, double angle, SumoColor color) {
        this.vehicleID = vehicleID;
        this.shape = createShape(vehicleID, x, y, angle, Color.colorToString(color));
        this.carGroup = createCarShape(vehicleID, x, y, angle, Color.colorToString(color));
    }

    /**
     * Getter
     */
    public String getVehicleID() {
        return vehicleID;
    }
    
    public Rectangle getShape() {
        // Return the car group wrapped as a shape for rendering
        // Actually we need to return Group, but keep Rectangle for compatibility
        // The rendering system should use carGroup instead
        return shape;
    }
    
    public Group getCarGroup() {
        return carGroup;
    }

    /**
     * Private helper method: calculate top-left corner from center position (for compatibility)
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
    
    /**
     * Create game-style car (top-down racing game look)
     */
    private Group createCarShape(String vehicleID, double x, double y, double angle, String color) {
        Group car = new Group();
        car.setId(vehicleID);
        
        double length = Metrics.DEFAULT_VEHICLE_LENGTH;
        double width = Metrics.DEFAULT_VEHICLE_WIDTH;
        double translate_vec = length/2 - width/2;
        
        // Main body - sleek racing style
        Rectangle body = new Rectangle(0, 0, length, width);
        body.setFill(Paint.valueOf(color));
        body.setArcWidth(width * 0.4);
        body.setArcHeight(width * 0.4);
        
        // Front nose (pointed like racing car)
        Polygon nose = new Polygon(
            length * 0.85, width * 0.5,
            length, width * 0.3,
            length, width * 0.7
        );
        nose.setFill(Paint.valueOf(color));
        
        // Windshield - glossy blue
        Rectangle windshield = new Rectangle(length * 0.55, width * 0.2, length * 0.3, width * 0.6);
        windshield.setFill(javafx.scene.paint.Color.rgb(100, 180, 255, 0.7));
        windshield.setArcWidth(width * 0.2);
        windshield.setArcHeight(width * 0.2);
        
        // Headlights glow
        Rectangle headlight1 = new Rectangle(length * 0.92, width * 0.15, length * 0.08, width * 0.18);
        Rectangle headlight2 = new Rectangle(length * 0.92, width * 0.67, length * 0.08, width * 0.18);
        headlight1.setFill(javafx.scene.paint.Color.rgb(255, 255, 220, 0.9));
        headlight2.setFill(javafx.scene.paint.Color.rgb(255, 255, 220, 0.9));
        headlight1.setArcWidth(2);
        headlight1.setArcHeight(2);
        headlight2.setArcWidth(2);
        headlight2.setArcHeight(2);
        
        // Outline/stroke for depth
        body.setStroke(javafx.scene.paint.Color.rgb(0, 0, 0, 0.6));
        body.setStrokeWidth(0.5);
        
        car.getChildren().addAll(body, nose, windshield, headlight1, headlight2);
        
        // Glossy effect - highlight on top
        Rectangle highlight = new Rectangle(length * 0.4, width * 0.25, length * 0.3, width * 0.15);
        highlight.setFill(javafx.scene.paint.Color.rgb(255, 255, 255, 0.3));
        highlight.setArcWidth(width * 0.3);
        highlight.setArcHeight(width * 0.3);
        car.getChildren().add(highlight);
        
        // Game-style shadow
        DropShadow shadow = new DropShadow();
        shadow.setRadius(3.0);
        shadow.setOffsetX(1.0);
        shadow.setOffsetY(1.0);
        shadow.setColor(javafx.scene.paint.Color.rgb(0, 0, 0, 0.5));
        car.setEffect(shadow);
        
        // Position and rotate
        car.setLayoutX(x - translate_vec);
        car.setLayoutY(y - translate_vec);
        car.setRotate(-(angle + 90));
        
        return car;
    }
}
