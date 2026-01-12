package real_time_traffic_simulation_with_java.gui.dashboardSection;

import javafx.util.Duration;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * The text section of the dashboard
 * Display basic info and congestion hotspot info
 */
public class TextSection extends VBox {
    private Timeline update;
    /**
     * Constructor: Create a TextSection VBox with labels that update with simulation data
     * @param simulationEngine The simulation engine to get data
     */
    public TextSection(SimulationEngine simulationEngine){
        // Time steps label
        Label timeSteps = new Label();
        timeSteps.setWrapText(true);
        timeSteps.setPrefWidth(Metrics.DASHBOARD_WIDTH - 20);
        timeSteps.setText(simulationEngine.getCurrentTimeStep());
        timeSteps.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        // Basic info label
        Label basicInfo = new Label();
        basicInfo.setWrapText(true);
        basicInfo.setPrefWidth(Metrics.DASHBOARD_WIDTH - 20);
        basicInfo.setText(simulationEngine.getBasicInfo());

        // Congestion hotspot labels
        Label congestedEdgeIDs = new Label();
        congestedEdgeIDs.setWrapText(true);
        congestedEdgeIDs.setPrefWidth(Metrics.DASHBOARD_WIDTH - 20);
        congestedEdgeIDs.setText(simulationEngine.getCongestionHotspots());

        // Create TextSection VBox
        super(5, timeSteps, basicInfo, congestedEdgeIDs);

        // Add Timeline to update content with simulation speed
        this.update = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            try{
                timeSteps.setText(simulationEngine.getCurrentTimeStep());
                basicInfo.setText(simulationEngine.getBasicInfo());
                congestedEdgeIDs.setText(simulationEngine.getCongestionHotspots());
            } catch (IllegalStateException ex) {
                this.update.stop();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }), new KeyFrame(Duration.millis(Metrics.CONNECT_SPEED_MS))); // Time line stop after this duration (or loop if setCycleCount)

        // Start the update timeline
        this.update.setCycleCount(Animation.INDEFINITE);
        this.update.play();
    }
}
