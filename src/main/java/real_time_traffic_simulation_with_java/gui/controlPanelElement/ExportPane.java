package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.tools.ExportingFiles;

/**
 * Export component with CSV export button
 */
public class ExportPane extends VBox {
    
    private final ComboBox<String> colorFilter;
    private final CheckBox congestedOnlyCheckBox;
    private final Button exportButton;
    private final SimulationEngine simulationEngine;
    private final ExportingFiles exportingFiles;
    
    public ExportPane(SimulationEngine simulationEngine, ExportingFiles exportingFiles) {
        this.simulationEngine = simulationEngine;
        this.exportingFiles = exportingFiles;
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
        
        // Congested edge checkbox
        congestedOnlyCheckBox = new CheckBox("Only include congested edge");
        congestedOnlyCheckBox.setStyle("-fx-text-fill: white;");
        CustomElement.addTooltip(congestedOnlyCheckBox, "Check to export only congested edges");

        exportButton = CustomElement.createButton("Export PDF", 220, 
            "Press to export current PDF log file.", "#6A6733");
        exportButton.setOnAction(e -> handleExport());
        
        this.getChildren().addAll(colorFilter, congestedOnlyCheckBox, exportButton);
    }
    
    /**
     * Returns whether the "Only include congested edge" checkbox is selected
     * @return true if checked, false otherwise
     */
    public boolean isCongestedOnlySelected() {
        return congestedOnlyCheckBox.isSelected();
    }

    private void handleExport() {
    }
}