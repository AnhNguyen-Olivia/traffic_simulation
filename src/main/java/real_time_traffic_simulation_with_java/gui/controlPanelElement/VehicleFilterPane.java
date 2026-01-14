package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import java.util.List;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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
        addTooltip(colorFilter, "Select color to filter vehicles");
        
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
        addTooltip(edgeFilter, "Select your filtered edge");
        
        HBox filterRow = new HBox(10, colorFilter, edgeFilter);
        
        // Filter button
        filterButton = new Button("Filter");
        filterButton.setPrefWidth(105);
        filterButton.setMaxWidth(105);
        filterButton.setStyle("-fx-background-color: #6A6733; -fx-text-fill: white;");
        addTooltip(filterButton, "Press to filter vehicles by color and edge.");
        filterButton.setOnAction(e -> handleFilter());
        
        // Reset button
        resetButton = new Button("Reset Filter");
        resetButton.setPrefWidth(105);
        resetButton.setMaxWidth(105);
        resetButton.setStyle("-fx-background-color:  #6A6733; -fx-text-fill: white;");
        addTooltip(resetButton, "Press to reset vehicle filter.");
        resetButton.setOnAction(e -> handleReset());
        
        HBox buttonRow = new HBox(10, filterButton, resetButton);
        
        this.getChildren().addAll(filterRow, buttonRow);
    }
    
    /***
     * Add tooltip to a control
     * @param control
     * @param text
    */
    private void addTooltip(Control control, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(control, tooltip);
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