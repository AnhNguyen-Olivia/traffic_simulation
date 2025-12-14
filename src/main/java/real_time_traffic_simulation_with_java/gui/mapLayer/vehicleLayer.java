package real_time_traffic_simulation_with_java.gui.mapLayer;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

import java.util.List;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;


/**
 * vehicleLayer class: create vehicle layer including vehicles
 * No tooltip or mouse events needed, vehicle run too fast to interact
 * @extends Group
 * @Finished
 * @Test Completed
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
        // Add vehicle shapes to the vehicle layer
        this.getChildren().addAll(vehicles);
    }
}
