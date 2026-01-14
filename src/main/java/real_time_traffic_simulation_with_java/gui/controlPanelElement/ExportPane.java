package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.tools.ExportingFiles;

/**
 * Export component with CSV export button
 */
public class ExportPane extends VBox {
    
    private final Button exportButton;
    private final SimulationEngine simulationEngine;
    private final ExportingFiles exportingFiles;
    
    public ExportPane(SimulationEngine simulationEngine, ExportingFiles exportingFiles) {
        this.simulationEngine = simulationEngine;
        this.exportingFiles = exportingFiles;
        
        exportButton = new Button("Export CSV");
        exportButton.setPrefWidth(220);
        exportButton.setMaxWidth(220);
        exportButton.setStyle("-fx-background-color: #6A6733; -fx-text-fill: white;");
        Tooltip tooltip = new Tooltip("Press to export current CSV log file.");
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(exportButton, tooltip);
        
        exportButton.setOnAction(e -> handleExport());
        
        this.getChildren().add(exportButton);
    }
    
    private void handleExport() {
        try {
            exportingFiles.queueCSV(simulationEngine.dataForCSV());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}