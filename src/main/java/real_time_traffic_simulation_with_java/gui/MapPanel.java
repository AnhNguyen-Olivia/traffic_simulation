package real_time_traffic_simulation_with_java.gui;

import java.util.logging.Logger;

import javafx.scene.layout.StackPane;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.input.MouseEvent;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.mapLayer.*;


/**
 * Create map panel including 3 layers: road layer (bottom-most), vehicle layer, traffic light layer (top-most). <br>
 * MapPanel supports zooming, panning, rotating functionalities. 
 *      Zoom by scrolling mouse wheel, pan by hold and dragging mouse, rotate by right-dragging mouse. <br>
 * <i><b>Note:</b> Drag to quadrant IV of Cartesian plane to rotate clockwise, 
 *      drag to quadrant II of Cartesian plane to rotate counter-clockwise. 
 *      Rotation might be unpredictable when dragging to other quadrants of Cartesian plane</i> <br>
 * MapPanel is clipped to prevent overflow drawing. 
 *      Transformation shouldn't be applied to the clip, or the clip will be distorted, only apply transformation to the children nodes.
 */
public class MapPanel extends StackPane {
    private SimulationEngine simulationEngine;
    private static final Logger LOGGER = Logger.getLogger(MapPanel.class.getName());
    /** Current zoom level of the map panel */
    private double currentZoomLevel = 1.0;

    /**
     * Create map panel including 3 layers: road layer (bottom-most), vehicle layer, traffic light layer (top-most). <br>
     *      MapPanel supports zooming, panning, rotating functionalities. 
     *      Zoom by scrolling mouse wheel, pan by hold and dragging mouse, rotate by right-dragging mouse <br>
     * MapPanel is clipped to prevent overflow drawing. 
     *      Transformation shouldn't be applied to the clip, or the clip will be distorted, only apply transformation to the children nodes.
     * @param engine SimulationEngine instance
     */
    public MapPanel(SimulationEngine engine) {
        try{
            this.simulationEngine = engine;
            createMapPanel(engine);
            setupZooming();
            setupPanning();
            setupRotating();
            // Set clip to prevent overflow drawing
            Rectangle clip = new Rectangle(Metrics.WINDOW_WIDTH - Metrics.CONTROL_PANEL_WIDTH - Metrics.DASHBOARD_WIDTH, Metrics.WINDOW_HEIGHT);
            this.setStyle("-fx-background-color: rgba(248, 217, 185, 0.9);");
            this.setClip(clip);
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize MapPanel.");
        }
        
        LOGGER.info("MapPanel initialized.");
    }


    /**
     * Public method: Refresh map panel by redraw vehicle layer and set color of traffic light layer
     */
    public void refresh() throws IllegalStateException{
        try{
            // Redraw vehicle layer
            Group temp = (Group) this.getChildren().get(0);
            temp.getChildren().set(1, new vehicleLayer(this.simulationEngine));
        } catch (IllegalStateException e) {
            LOGGER.severe("Simulation has ended or connection lost while refreshing MapPanel.");
            throw e;
        } catch (Exception e) {
            LOGGER.warning("Failed to refresh vehicle layer in MapPanel.");
        }
    }




    /**
     * Private helper method: Create map panel by grouping 3 layers: road layer, vehicle layer, traffic light layer than add the Group to MapPanel StackPane
     */
    private void createMapPanel(SimulationEngine engine) {
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
            // Limit the zoom level within the range [MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL]
            zoomFactor = Math.max(Math.min(zoomFactor, Metrics.MAX_ZOOM_LEVEL/currentZoomLevel), Metrics.MIN_ZOOM_LEVEL/currentZoomLevel);
            currentZoomLevel *= zoomFactor;
            // Apply zooming at mouse cursor position
            Scale scale = new Scale(zoomFactor, zoomFactor, event.getX(), event.getY());
            mapGroup.getTransforms().add(scale);
            event.consume();
        });
    }


    /**
     * Private helper method: Panning functionality for MapPanel by dragging left mouse button
     */
    private void setupPanning() {
        // Since scene and group have different coordinate system
        // We need to translate the group based on scene coordinates to avoid huge jump
        double[] xy = new double[2];
        Group mapGroup = (Group) this.getChildren().get(0);
        // Translate mean moving the node to x=... y=... not increasing by ... (x=0,y=0 is the top-left corner of the scene)
        // Variable in lambda must be local, final or effectively final
        // Both panning and rotating use MOUSE_PRESSED and MOUSE_DRAGGED events -> over-written without event handler
        mapGroup.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if(!event.isPrimaryButtonDown()) return; // Only pan when left mouse button is pressed
            xy[0] = event.getSceneX() - mapGroup.getTranslateX();
            xy[1] = event.getSceneY() - mapGroup.getTranslateY();
        });
        // New position of mouse - original position of mouse when pressed + old translated of node -> translate vector
        mapGroup.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if(!event.isPrimaryButtonDown()) return; // Only pan when left mouse button is pressed
            mapGroup.setTranslateX(event.getSceneX() - xy[0]);
            mapGroup.setTranslateY(event.getSceneY() - xy[1]);
        });
    }


    /**
     * Private helper method: Rotating functionality for MapPanel by dragging right mouse button
     * Drag to quadrant IV of Cartesian plane to rotate clockwise, drag to quadrant II of Cartesian plane to rotate counter-clockwise
     * Rotation might be unpredictable when dragging to other quadrants of Cartesian plane
     */
    private void setupRotating() {
        double[] delta = new double[3];
        Group mapGroup = (Group) this.getChildren().get(0);
        // variable in lambda must be local, final or effectively final
        // Both panning and rotating use MOUSE_PRESSED and MOUSE_DRAGGED events -> over-written without event handler
        mapGroup.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if(event.isPrimaryButtonDown()) return; // Only rotate when right mouse button is pressed
            delta[0] = event.getSceneX();
            delta[1] = event.getSceneY();
            delta[2] = mapGroup.getRotate(); // Store the rotation angle (when start to press)
        });
        // Rotate based on the change of mouse position
        mapGroup.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if(event.isPrimaryButtonDown()) return; // Only rotate when right mouse button is pressed
            double angleX = event.getSceneX() - delta[0];
            double angleY = event.getSceneY() - delta[1];
            mapGroup.setRotate(delta[2] + (angleX + angleY) / 2);
        });
    }


}