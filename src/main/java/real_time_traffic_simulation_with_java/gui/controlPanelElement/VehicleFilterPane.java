package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import java.util.List;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * Vehicle filter component with color and edge filters
 */
public class VehicleFilterPane extends VBox {
    
    private final ComboBox<String> colorFilter;
    private final ComboBox<String> edgeFilter;
    private final Button filterButton;
    private final Button resetButton;
    private final SimulationEngine simulationEngine;
    
    public VehicleFilterPane(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
        this.setSpacing(10);
        
        // Color filter
        colorFilter = new ComboBox<>();
        List<String> colorOptions = new java.util.ArrayList<>(Color.ListofAllColor);
        colorOptions.add(0, ""); // Empty option for no filter
        colorFilter.getItems().addAll(colorOptions);
        colorFilter.setPromptText("Color");
        colorFilter.setPrefWidth(105);
        colorFilter.setMaxWidth(105);
        CustomElement.addTooltip(colorFilter, "Select color to filter vehicles");
        
        // Edge filter
        edgeFilter = new ComboBox<>();
        edgeFilter.setPromptText("Edge");
        edgeFilter.setPrefWidth(105);
        edgeFilter.setMaxWidth(105);
        try {
            List<String> edgeOptions = new java.util.ArrayList<>(simulationEngine.getAllEdgeIDs());
            edgeOptions.add(0, ""); // Empty option for no filter
            edgeFilter.getItems().addAll(edgeOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CustomElement.addTooltip(edgeFilter, "Select your filtered edge");
        
        HBox filterRow = new HBox(10, colorFilter, edgeFilter);
        
        // Filter button
        filterButton = CustomElement.createButton("Filter", 105, "Press to filter vehicles by color and edge.", "#6A6733");
        filterButton.setOnAction(e -> handleFilter());
        
        // Reset button
        resetButton = CustomElement.createButton("Reset Filter", 105, "Press to reset vehicle filter.", "#6A6733");
        resetButton.setOnAction(e -> handleReset());
        
        HBox buttonRow = new HBox(10, filterButton, resetButton);
        
        this.getChildren().addAll(filterRow, buttonRow);
    }
    
    /**
     * Handle filter action
    */
    private void handleFilter() {
        try {
            String color = colorFilter.getValue();
            String edge = edgeFilter.getValue();
            simulationEngine.setVehicleFilter(color, edge);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handle reset filter action
    */
    private void handleReset() {
        try {
            simulationEngine.setVehicleFilter("", "");
            colorFilter.setValue("");
            edgeFilter.setValue("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}