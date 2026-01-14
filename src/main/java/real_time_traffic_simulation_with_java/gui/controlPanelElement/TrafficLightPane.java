package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * Traffic light toggle component with a single button
 */
public class TrafficLightPane extends VBox {
    
    private final Button toggleButton;
    private final SimulationEngine simulationEngine;
    
    public TrafficLightPane(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
        
        toggleButton = new Button("Toggle all traffic light");
        toggleButton.setPrefWidth(220);
        toggleButton.setMaxWidth(220);
        toggleButton.setStyle("-fx-background-color:  #6A6733; -fx-text-fill: white;");
        Tooltip tooltip = new Tooltip("Press to toggle all traffic light.");
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(toggleButton, tooltip);
        
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