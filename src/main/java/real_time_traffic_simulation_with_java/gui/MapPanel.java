package real_time_traffic_simulation_with_java.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.mapLayer.*;

public class MapPanel extends StackPane {
    private SimulationEngine simulationEngine;

    /**
     * Constructor for MapPanel, MapPanel size: 900x830
     * MapPanel is clipped to prevent overflow drawing
     * Shouldn't apply transformation to the clip, or the clip will be distorted, only apply transformation to the children nodes
     * Zoom by scrolling mouse wheel, pan by hold and dragging mouse, rotate by right-dragging mouse
     * @throws Exception
     */
    public MapPanel(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        this.setStyle("-fx-background-color: transparent;");
        createMapPanel(engine);
        setupZooming();
        setupPanning();
        Rectangle clip = new Rectangle(900, 830);
        this.setClip(clip);
    }


    /**
     * Public method: Refresh map panel by redraw vehicle layer and set color of traffic light layer
     * @throws Exception
     */
    public void refresh() throws Exception {
        // Refresh vehicle layer
        Group temp = (Group) this.getChildren().get(0);
        temp.getChildren().set(1, new vehicleLayer(this.simulationEngine));
        // Refresh traffic light layer
        ((trafficlightLayer)temp.getChildren().get(2)).refreshTrafficLightLayer();
    }




    /**
     * Private helper method: Create map panel by grouping 3 layers: road layer, vehicle layer, traffic light layer than add the Group to MapPanel StackPane
     * @throws Exception
     */
    private void createMapPanel(SimulationEngine engine) throws Exception {
        // Generate road layer for map panel
        roadLayer RoadLayer = new roadLayer(engine);
        vehicleLayer VehicleLayer = new vehicleLayer(engine);
        trafficlightLayer TrafficLightLayer = new trafficlightLayer(engine);

        // Group 3 layers together: road layer, vehicle layer, traffic light layer (top-most)
        Group mapPanel = new Group();
        mapPanel.getChildren().addAll(RoadLayer, VehicleLayer, TrafficLightLayer);
        // Flip vertically because the coordinate system of SUMO is different from JavaFX
        mapPanel.setScaleY(-1); 

        // If focus is not set, all mouse event will not be captured by mapGroup because StackPane will capture it first
        mapPanel.setPickOnBounds(true);
        mapPanel.setFocusTraversable(true);

        this.getChildren().add(mapPanel);
    }


    /**
     * Private helper method: Zooming functionality for MapPanel
     * Zoom in and out by scrolling mouse wheel at cursor position
     */
    private void setupZooming() {
        Group mapGroup = (Group) this.getChildren().get(0);
        mapGroup.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() > 0 ? Metrics.ENLARGE_FACTOR : Metrics.SHRINK_FACTOR;
            Scale scale = new Scale(zoomFactor, zoomFactor, event.getX(), event.getY());
            mapGroup.getTransforms().add(scale);
            event.consume();
        });
    }


    /**
     * Private helper method: Panning & rotating functionality for MapPanel
     * Left mouse button for panning, right mouse button for rotating
     */
    private void setupPanning() {
        // // Since scene and group have different coordinate system
        // // We need to translate the group based on scene coordinates to avoid huge jump
        // double x_translate;
        // double y_translate;
        // Group mapGroup = (Group) this.getChildren().get(0);
        // mapGroup.setOnMouseClicked(event -> {
        //     x_translate = event.getSceneX();
        //     y_translate = event.getSceneY();
        //     event.consume();
        // });
        // mapGroup.setOnMouseDragged(event -> {
        //     if (event.isPrimaryButtonDown()) {
        //         // Panning with left mouse button
        //         mapGroup.setTranslateX(mapGroup.getTranslateX() + x_translate);
        //         mapGroup.setTranslateY(mapGroup.getTranslateY() + y_translate);
        //     } else if (event.isSecondaryButtonDown()) {
        //         // Rotating with right mouse button
        //         double angle = Math.atan2(deltaY, deltaX) * (180 / Math.PI);
        //         mapGroup.setRotate(mapGroup.getRotate() + angle);
        //     }
        //     x_translate = event.getSceneX();
        //     y_translate = event.getSceneY();
        //     event.consume();
        // });
    }


}