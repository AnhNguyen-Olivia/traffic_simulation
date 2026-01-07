package real_time_traffic_simulation_with_java.gui.mapLayer;

import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.cores.VehicleData;

import java.util.List;

import javafx.scene.Group;


/**
 * Create vehicle layer including vehicles. <br>
 * No tooltip or mouse events needed, vehicle run too fast to interact.
 */
public class vehicleLayer extends Group {
    private SimulationEngine simulationEngine;

    /**
     * Create vehicle layer including vehicles. <br>
     * No tooltip or mouse events needed, vehicle run too fast to interact.
     * @param engine SimulationEngine instance
     * @throws Exception
     */
    public vehicleLayer(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        List<VehicleData> vehicles = this.simulationEngine.getMapVehicles();
        // Add vehicle shapes to the vehicle layer
        this.getChildren().addAll(vehicles);
    }
}
