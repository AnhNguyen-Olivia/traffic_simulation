package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * Stress test component with input field and edge selection
 */
public class StressTestPane extends VBox {
    
    private final TextField inputVnumber;
    private final ComboBox<String> startEdge;
    private final Button stressButton;
    private final SimulationEngine simulationEngine;
    
    public StressTestPane(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
        this.setSpacing(10);
        
        // Vehicle number input
        inputVnumber = ButtonAndTooltip.createTextField("100", 105, "Enter the number of vehicle you want to inject. Default is 100");
        
        // Start edge selection
        startEdge = new ComboBox<>();
        startEdge.setPromptText("Start edge");
        startEdge.setPrefWidth(105);
        startEdge.setMaxWidth(105);
        try {
            startEdge.getItems().addAll(simulationEngine.getAllEdgeIDs());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ButtonAndTooltip.addTooltip(startEdge, "Select your start edge");
        
        HBox inputRow = new HBox(10, inputVnumber, startEdge);
        
        // Stress test button
        stressButton = ButtonAndTooltip.createButton("Stress test tool", 220, 
            "Stress test tool that allow user to enter stress test mode. Default number is 100, but you can change to any number you want.", "#6A6733");
        stressButton.setOnAction(e -> handleStressTest());
        
        this.getChildren().addAll(inputRow, stressButton);
    }
    
    private void handleStressTest() {
        try {
            String vehicleNumber = inputVnumber.getText().trim();
            String stressEdge = startEdge.getValue();
            
            if (vehicleNumber.isEmpty()) vehicleNumber = "100";
            int vNumber = Integer.parseInt(vehicleNumber);
            
            if (stressEdge == null || stressEdge.isEmpty()) {
                System.out.println("Error, start edge is empty");
                return;
            }
            
            simulationEngine.stressTest(vNumber, stressEdge);
            
        } catch (NumberFormatException ex) {
            System.out.println("Error: Please enter a positive integer number.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}