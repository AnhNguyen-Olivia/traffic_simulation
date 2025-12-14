package real_time_traffic_simulation_with_java.gui;

import javafx.scene.layout.StackPane;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.input.MouseEvent;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.mapLayer.*;


/**
 * MapPanel class: create map panel including 3 layers: road layer, vehicle layer, traffic light layer (top-most)
 * MapPanel supports zooming, panning, rotating functionalities
 * @extends StackPane
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */
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
        createMapPanel(engine);
        setupZooming();
        setupPanning();
        setupRotating();
        // Set clip to prevent overflow drawing
        Rectangle clip = new Rectangle(Metrics.WINDOW_WIDTH - Metrics.DASHBOARD_WIDTH - Metrics.STATISTIC_WIDTH, Metrics.WINDOW_HEIGHT);
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
     * Drag to bottom-right to rotate clockwise, drag to top-left to rotate counter-clockwise
     * Rotation might be unpredictable when dragging to other directions
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