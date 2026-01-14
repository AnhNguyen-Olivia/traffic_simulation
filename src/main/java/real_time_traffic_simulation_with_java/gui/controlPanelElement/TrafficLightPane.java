package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * Traffic light toggle component with a single button
 */
public class TrafficLightPane extends VBox {
    
    private final Button toggleButton;
    private final SimulationEngine simulationEngine;
    
    public TrafficLightPane(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
        
        toggleButton = ButtonAndTooltip.createButton("Toggle all traffic light", 220, 
            "Press to toggle all traffic light.", "#6A6733");
        toggleButton.setOnAction(e -> handleToggle());
        
        this.getChildren().add(toggleButton);
    }
    
    private void handleToggle() {
        try {
            simulationEngine.toggleAllTls();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}