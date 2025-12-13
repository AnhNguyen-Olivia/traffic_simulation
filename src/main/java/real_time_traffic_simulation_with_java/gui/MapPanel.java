package real_time_traffic_simulation_with_java.gui;

import javafx.scene.layout.Pane;
import javafx.scene.Group;

import real_time_traffic_simulation_with_java.cores.SimulationEngine;

public class MapPanel extends Pane {
    private SimulationEngine simulationEngine;

    /**
     * Constructor for MapPanel
     * @throws Exception
     */
    public MapPanel(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        this.setStyle("-fx-background-color: transparent;");
        createMapPanel(engine);
    }


    /**
     * Private helper method: Create map panel by grouping 3 layers: road layer, vehicle layer, traffic light layer
     * @throws Exception
     */
    public void createMapPanel(SimulationEngine engine) throws Exception {
        // Generate road layer for map panel
        roadLayer RoadLayer = new roadLayer(engine);
        vehicleLayer VehicleLayer = new vehicleLayer(engine);
        trafficlightLayer TrafficLightLayer = new trafficlightLayer(engine);

        // Group 3 layers together: road layer, vehicle layer, traffic light layer (top-most)
        Group mapPanel = new Group();
        mapPanel.getChildren().addAll(RoadLayer, VehicleLayer, TrafficLightLayer);

        this.getChildren().add(mapPanel);
    }

    public void refresh() throws Exception {
        // Refresh vehicle layer
        Group temp = (Group) this.getChildren().get(0);
        temp.getChildren().set(1, new vehicleLayer(this.simulationEngine));
        // Refresh traffic light layer
        ((trafficlightLayer)temp.getChildren().get(2)).refreshTrafficLightLayer();
    }

}
