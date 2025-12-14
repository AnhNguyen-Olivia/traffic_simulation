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

public class VehicleData extends Rectangle {
    /**
     * Constructor
     * @param color default color is white if invalid input, can modify return String in alias.Color
     */
    public VehicleData(String vehicleID, double x, double y, double angle, SumoColor color) {
        String colorString =  Color.colorToString(color);

        // Top-left corner of JavaFX Rectangle is bottom-left corner of SUMO vehicle
        double translate_vec = Metrics.DEFAULT_VEHICLE_LENGTH/2 - Metrics.DEFAULT_VEHICLE_WIDTH/2;
        this.setX(x - translate_vec);
        this.setY(y - translate_vec);
        this.setWidth(Metrics.DEFAULT_VEHICLE_LENGTH);
        this.setHeight(Metrics.DEFAULT_VEHICLE_WIDTH);
        this.setArcWidth(Metrics.DEFAULT_VEHICLE_ARC);
        this.setArcHeight(Metrics.DEFAULT_VEHICLE_ARC);

        // JavaFX rotation is (anti-clockwise when show in cartesian coordinate), SUMO rotation is clockwise when show in cartesian coordinate
        this.setRotate(-(angle + 90)); // JavaFx 0 degree is to the right, SUMO 0 degree is to the top
        this.setFill(javafx.scene.paint.Paint.valueOf(colorString));
        this.setId(vehicleID);                            
    }
}
