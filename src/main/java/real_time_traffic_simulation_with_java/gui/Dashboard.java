package real_time_traffic_simulation_with_java.gui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * The statistic panel on the right side of the main window
 * Use to display statistics about the simulation
*/
public class Dashboard extends Pane {
    private SimulationEngine simulationEngine;
    private VBox textSection;
    

    public Dashboard(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        setupTextSection();
        this.getChildren().add(textSection);
    }
    



    /**
     * Private helper method: show live data about congestion hotspots
     * @throws Exception
     */
    private void setupTextSection() throws Exception {
        // Basic info labels
        Label basicInfo = new Label();
        basicInfo.setWrapText(true);
        basicInfo.setPrefWidth(Metrics.DASHBOARD_WIDTH - 20);
        basicInfo.setText(simulationEngine.getBasicInfo());
        // Congestion hotspot labels
        Label congestedEdgeIDs = new Label();
        congestedEdgeIDs.setWrapText(true);
        congestedEdgeIDs.setPrefWidth(Metrics.DASHBOARD_WIDTH - 20);
        congestedEdgeIDs.setText(simulationEngine.getCongestionHotspots());
        // Add Timeline to update content with simulation speed
        Timeline update = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            try{
                basicInfo.setText(simulationEngine.getBasicInfo());
                congestedEdgeIDs.setText(simulationEngine.getCongestionHotspots());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }), new KeyFrame(Duration.millis(Metrics.CONNECT_SPEED_MS))); // Time line stop after this duration (or loop if setCycleCount)
        // Ensure the timeline runs indefinitely
        update.setCycleCount(Animation.INDEFINITE);
        update.play();
        this.getChildren().addAll(basicInfo, congestedEdgeIDs);
        // Add to text section
        textSection = new VBox(10, basicInfo, congestedEdgeIDs);
    }
}
