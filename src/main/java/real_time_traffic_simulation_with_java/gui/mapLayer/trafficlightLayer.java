package real_time_traffic_simulation_with_java.gui.mapLayer;

import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.cores.TrafficLightData;
import real_time_traffic_simulation_with_java.alias.Metrics;

import java.util.List;
import java.util.Optional;

import javafx.scene.control.Tooltip;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.scene.Group;
import javafx.util.Duration;


/**
 * Create traffic lights layer including traffic lights, 
 *      with tooltip for better interactivity. <br>
 * Double-clicked left mouse events is set to toggle traffic light state. <br>
 * Double-clicked right mouse events is set to adjust traffic light phase durations.
 */
public class trafficlightLayer extends Group {
    private SimulationEngine simulationEngine;

    /**
     * Create traffic lights layer including traffic lights, 
     *      with tooltip for better interactivity. <br>
     * Double-clicked left mouse events is set to toggle traffic light state. <br>
     * Double-clicked right mouse events is set to adjust traffic light phase durations.
     * @param engine SimulationEngine instance
     */
    public trafficlightLayer(SimulationEngine engine) {
        this.simulationEngine = engine;
        List<TrafficLightData> Tls = this.simulationEngine.getMapTls();

        // Add tooltip and mouse events
        addToolTip(Tls);
        setToggleEvent(Tls);
        setAdjustPhaseEvent(Tls);

        // Add traffic light groups to the traffic light layer
        this.getChildren().addAll(Tls);
    }


    /**
     * Private helper method: Add tooltip to traffic lights
     */
    private void addToolTip(List<TrafficLightData> Tls) {
        for (TrafficLightData Tl : Tls){
            Label tooltipLabel = new Label();
            Tooltip tooltip = new Tooltip();
            // Tooltip does not repaint when visible if setText, Label is a live node that can be updated dynamically
            // Tooltip text is treated as static String, tooltip graphic is treated as Node that can be updated dynamically
            tooltip.setGraphic(tooltipLabel);
            // Add Timeline to update tooltip content with simulation speed
            Timeline updateTooltip = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                tooltipLabel.setText(simulationEngine.getTlTooltip(Tl.getId()));
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
     * Private helper method: Set double-click left mouse event to toggle traffic lights to change state
     */
    private void setToggleEvent(List<TrafficLightData> Tls) {
        for (TrafficLightData Tl : Tls){
            // MOUSE_CLICKED fired when mouse button is released
            Tl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                // event.isPrimaryButtonDown() not working because mouse button has been released at this time
                if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                    simulationEngine.toggleSingleTl(Tl.getId());
                }
            });
        }
    }


    /**
     * Private helper method: Set double-click right mouse event to adjust traffic lights phase durations
     */
    private void setAdjustPhaseEvent(List<TrafficLightData> Tls) {
        for (TrafficLightData Tl : Tls){
            // MOUSE_CLICKED fired when mouse button is released
            Tl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                // event.isSecondaryButtonDown() not working because mouse button has been released at this time
                if (event.getClickCount() == 2 && event.getButton() == MouseButton.SECONDARY) {
                    // Open popup window to adjust phase durations and get the result
                    trafficlightPopupWindow popup = new trafficlightPopupWindow(Tl.getId(), Tl.getPhasesDuration());
                    Optional<List<Integer>> result = popup.showAndWait();
                    // If result is not null, set new phase durations to the simulation engine
                    result.ifPresent(phaseDurations -> {
                        try {
                            simulationEngine.setTlPhaseDurations(Tl.getId(), phaseDurations);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            });
        }
    }

}
