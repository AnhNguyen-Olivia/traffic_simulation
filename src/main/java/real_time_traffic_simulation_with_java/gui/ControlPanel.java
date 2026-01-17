package real_time_traffic_simulation_with_java.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.controlPanelElement.ExportPane;
import real_time_traffic_simulation_with_java.gui.controlPanelElement.StressTestPane;
import real_time_traffic_simulation_with_java.gui.controlPanelElement.TrafficLightPane;
import real_time_traffic_simulation_with_java.gui.controlPanelElement.VehicleFilterPane;
import real_time_traffic_simulation_with_java.gui.controlPanelElement.VehicleInjectionPane;
import real_time_traffic_simulation_with_java.tools.ExportingFiles;

/**
 * Control panel pane that wires together all control components.
 * Simple and modular design with separated concerns.
 * (Happy Christmas!) ᓚ₍⑅^- .-^₎ -ᶻ 𝗓 𐰁
 */
public class ControlPanel extends VBox {

    public ControlPanel(SimulationEngine simulationEngine, ExportingFiles exportingFiles) {
        
        // Main layout container with no spacing between header and content
        VBox mainLayout = new VBox(0);
        mainLayout.prefWidthProperty().bind(this.widthProperty());
        mainLayout.maxWidthProperty().bind(this.widthProperty());
        
        // Header with title
        Label title = new Label("Control Panel");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: white;");

        VBox headerPane = new VBox(title);
        headerPane.setAlignment(Pos.CENTER);
        headerPane.setPadding(new Insets(10));
        headerPane.setStyle("-fx-background-color: #6A6733;");

        // Content area with spacing
        VBox contentArea = new VBox();
        contentArea.setSpacing(15);
        contentArea.setPadding(new Insets(15));
        
        // Add section headers and components
        contentArea.getChildren().addAll(
            createSectionHeader("Inject vehicles"),
            new VehicleInjectionPane(simulationEngine),
            new Separator(),
            
            createSectionHeader("Stress test"),
            new StressTestPane(simulationEngine),
            new Separator(),
            
            createSectionHeader("Toggle all light"),
            new TrafficLightPane(simulationEngine),
            new Separator(),
            
            createSectionHeader("Filter"),
            new VehicleFilterPane(simulationEngine),
            new Separator(),
            
            createSectionHeader("Export"),
            new ExportPane(simulationEngine, exportingFiles)
        );
        
        // Assemble layout
        mainLayout.getChildren().addAll(headerPane, contentArea);
        this.getChildren().add(mainLayout);
        
        // Set background color /ᐠ - ˕ -マ ᶻ 𝗓 𐰁
        this.setStyle("-fx-background-color: #6D4514;");
        
        this.setVisible(true);
    }
    
    /**
     * Creates a styled section header label
     */
    private Label createSectionHeader(String text) {
        Label header = new Label(text);
        header.setFont(Font.font("System", FontWeight.NORMAL, 15));
        header.setStyle("-fx-text-fill: white;");

        // Center align and set max width to fill parent
        header.setMaxWidth(Double.MAX_VALUE);
        header.setAlignment(Pos.CENTER);

        return header;
    }
}