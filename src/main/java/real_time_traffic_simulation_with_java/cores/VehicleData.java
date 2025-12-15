package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoColor;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.alias.Metrics;

import javafx.scene.shape.Rectangle;


/**
 * Represents the visual data of a vehicle in the simulation.
 */
public class VehicleData extends Rectangle {
    /**
     * Represents the visual data of a vehicle in the simulation.
     * @param vehicleID The unique identifier for the vehicle.
     * @param x The x-coordinate of the vehicle's position.
     * @param y The y-coordinate of the vehicle's position.
     * @param angle The orientation angle of the vehicle.
     * @param color The color of the vehicle, represented as a SumoColor.
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
