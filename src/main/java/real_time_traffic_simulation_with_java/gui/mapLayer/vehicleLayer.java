package real_time_traffic_simulation_with_java.gui.mapLayer;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.alias.Metrics;

import java.util.List;

import javafx.scene.control.Tooltip;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


/**
 * vehicleLayer class: create vehicle layer including vehicles
 * Add tooltip and mouse events for better interactivity
 * @extends Group
 * @Finished
 * @Test Incomplete
 * @Javadoc Completed
 */
public class vehicleLayer extends Group {
    private SimulationEngine simulationEngine;

    /**
     * Constructor for roadLayer
     * @throws Exception
     */
    public vehicleLayer(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        createVehicleLayer();
    }


    /**
     * Private helper method: Grouping vehicles into 1 group for vehicle layer
     * @throws Exception
     */
    private void createVehicleLayer() throws Exception {
        List<Rectangle> vehicles = this.simulationEngine.getMapVehicles();

        // Add tooltip and mouse events
        addToolTip(vehicles);

        // Add vehicle shapes to the vehicle layer
        this.getChildren().addAll(vehicles);
    }


    /**
     * Private helper method: Add tooltip and mouse events to vehicles
     * @throws Exception
     */
    private void addToolTip(List<Rectangle> vehicles) throws Exception {
        for (Rectangle vehicle : vehicles){
            // Install tooltip
            Tooltip tooltip = new Tooltip(simulationEngine.getVehicleTooltip(vehicle.getId()));
            tooltip.setShowDelay(Duration.ZERO);
            tooltip.setHideDelay(Duration.millis(Metrics.HIDE_DELAY));
            Tooltip.install(vehicle, tooltip);
            // Add mouse entered/exited to know hovering state
            vehicle.setOnMouseEntered(e -> {
                vehicle.setStroke(javafx.scene.paint.Color.AQUA);
                vehicle.setStrokeWidth(0.5);
            });
            vehicle.setOnMouseExited(e -> {
                vehicle.setStroke(null);
            });
        }
    }
    
}
