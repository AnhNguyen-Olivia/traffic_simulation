package real_time_traffic_simulation_with_java.gui;

import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * The statistic panel on the right side of the main window
 * Use to display statistic infomation
 * It is currently placeing an image as a placeholder, we will implement it later
*/
public class Dashboard extends Pane {
    SimulationEngine simulationEngine;

    public Dashboard(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        showCongestionHotspots();
    }
    

    private void showCongestionHotspots() throws Exception {
        Label congestedEdgeIDs = new Label();
        congestedEdgeIDs.setWrapText(true);
        congestedEdgeIDs.setPrefWidth(Metrics.DASHBOARD_WIDTH - 20);

        // Add Timeline to update tooltip content with simulation speed
        Timeline update = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            try{
                congestedEdgeIDs.setText(simulationEngine.getCongestionHotspots());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }), new KeyFrame(Duration.millis(Metrics.CONNECT_SPEED_MS))); // Time line stop after this duration (or loop if setCycleCount)
        // Ensure the timeline runs indefinitely
        update.setCycleCount(Animation.INDEFINITE);
        update.play();


        this.getChildren().add(congestedEdgeIDs);
    }
}
