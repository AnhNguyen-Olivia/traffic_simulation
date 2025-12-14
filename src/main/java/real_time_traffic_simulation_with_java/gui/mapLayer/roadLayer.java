package real_time_traffic_simulation_with_java.gui.mapLayer;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.alias.Metrics;

import java.util.List;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


/**
 * roadLayer class: create road layer including junctions and edges
 * Add tooltip and mouse events for better interactivity
 * Junction have no tooltips or mouse events, since they are just for visual purpose
 * @extends Group
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */
public class roadLayer extends Group {
    private SimulationEngine simulationEngine;

    /**
     * Constructor for roadLayer
     * @throws Exception
     */
    public roadLayer(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        createRoadLayer();
    }


    /**
     * Private helper method: Grouping junctions and edges into 1 group for road layer
     * @throws Exception
     */
    private void createRoadLayer() throws Exception {
        List<Polygon> junctions = this.simulationEngine.getMapJunctions();
        List<Group> edges = this.simulationEngine.getMapEdges();

        // Add tooltip and mouse events
        addToolTip(edges);

        // Add junction shapes to the road layer
        this.getChildren().addAll(junctions);
        // Add edge shapes to the road layer
        this.getChildren().addAll(edges);
    }


    /**
     * Private helper method: Add tooltip and mouse events to edges
     * @throws Exception
     */
    private void addToolTip(List<Group> edges) throws Exception {
        for (Group edge : edges){
            // Install tooltip
            Label tooltipLabel = new Label();
            // Install tooltip
            Tooltip tooltip = new Tooltip();
            tooltip.setGraphic(tooltipLabel);
            tooltip.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            Timeline updateTooltip = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                try{
                    tooltipLabel.setText(simulationEngine.getEdgeTooltip(edge.getId()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }), new KeyFrame(Duration.millis(Metrics.CONNECT_SPEED_MS)));
            updateTooltip.setCycleCount(Animation.INDEFINITE);
            tooltip.setShowDelay(Duration.ZERO);
            tooltip.setShowDuration(Duration.INDEFINITE);
            tooltip.setHideDelay(Duration.millis(Metrics.HIDE_DELAY));
            tooltip.setOnShown(e->updateTooltip.play());
            tooltip.setOnHidden(e->updateTooltip.stop());
            Tooltip.install(edge, tooltip);
            // Add mouse entered/exited to know hovering state
            // Since edge is a Group, we need to get the Rectangle inside it (1st child, see in cores/EdgeData.java)
            edge.setOnMouseEntered(e -> {
                if (edge.getChildren().get(0) instanceof Rectangle rectangle) {
                    rectangle.setStroke(javafx.scene.paint.Color.AQUA);
                    rectangle.setStrokeWidth(0.5);
                }
            });
            edge.setOnMouseExited(e -> {
                if (edge.getChildren().get(0) instanceof Rectangle rectangle) {
                    rectangle.setStroke(null);
                }
            });
        }
    }


}
