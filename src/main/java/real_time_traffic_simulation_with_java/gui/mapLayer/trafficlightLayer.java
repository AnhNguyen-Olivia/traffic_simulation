package real_time_traffic_simulation_with_java.gui.mapLayer;

import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.cores.TrafficLightData;
import real_time_traffic_simulation_with_java.alias.Metrics;

import java.util.List;

import javafx.scene.control.Tooltip;
import javafx.scene.control.Label;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.scene.Group;
import javafx.util.Duration;

/**
 * trafficlightLayer class: create traffic light layer including traffic lights
 * Add tooltip for better interactivity
 * No mouse events needed
 * @extends Group
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */
public class trafficlightLayer extends Group {
    private SimulationEngine simulationEngine;


    /**
     * Constructor for trafficlightLayer
     * @throws Exception
     */
    public trafficlightLayer(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        createTrafficLightLayer();
    }


    /**
     * Private helper method: Grouping traffic lights into 1 group for traffic light layer
     * @throws Exception
     */
    private void createTrafficLightLayer() throws Exception {
        List<TrafficLightData> Tls = this.simulationEngine.getMapTls();

        // Add tooltip and mouse events
        addToolTip(Tls);
        setToggleEvent(Tls);

        // Add traffic light groups to the traffic light layer
        this.getChildren().addAll(Tls);
    }


    /**
     * Private helper method: Add tooltip and mouse events to traffic lights
     * @throws Exception
     */
    private void addToolTip(List<TrafficLightData> Tls) throws Exception {
        for (TrafficLightData Tl : Tls){
            Label tooltipLabel = new Label();
            Tooltip tooltip = new Tooltip();
            // Tooltip does not repaint when visible if setText, Label is a live node that can be updated dynamically
            // Tooltip text is treated as static String, tooltip graphic is treated as Node that can be updated dynamically
            tooltip.setGraphic(tooltipLabel);
            // Add Timeline to update tooltip content with simulation speed
            Timeline updateTooltip = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                try{
                    tooltipLabel.setText(simulationEngine.getTlTooltip(Tl.getId()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }), new KeyFrame(Duration.millis(Metrics.CONNECT_SPEED_MS))); // Time line stop after this duration (or loop if setCycleCount)
            // Ensure the timeline runs indefinitely
            updateTooltip.setCycleCount(Animation.INDEFINITE);
            // To prevent tooltip delay and automatical hide
            tooltip.setShowDelay(Duration.ZERO);
            tooltip.setShowDuration(Duration.INDEFINITE);
            tooltip.setHideDelay(Duration.millis(Metrics.HIDE_DELAY));
            // Update tooltip content when shown
            tooltip.setOnShown(e->updateTooltip.play());
            tooltip.setOnHidden(e->updateTooltip.stop());
            Tooltip.install(Tl, tooltip);
        }
    }

    
    /**
     * Private helper method: Set double-click event to toggle traffic lights to change state
     */
    private void setToggleEvent(List<TrafficLightData> Tls) {
        for (TrafficLightData Tl : Tls){
            Tl.setOnMouseClicked(event -> {
                try {
                    if (event.getClickCount() == 2) {
                        simulationEngine.toggleSingleTl(Tl.getId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }


    /**
     * Public method: Refresh traffic light layer to update traffic light states
     * @throws Exception
     */
    public void refreshTrafficLightLayer() throws Exception {
        this.simulationEngine.updateMapTls();
    }


}
