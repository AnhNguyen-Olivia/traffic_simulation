package real_time_traffic_simulation_with_java.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudresden.sumo.objects.SumoPosition2D;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import real_time_traffic_simulation_with_java.cores.Edge;
import real_time_traffic_simulation_with_java.cores.Lane;
import real_time_traffic_simulation_with_java.cores.VehicleData;
import real_time_traffic_simulation_with_java.wrapper.SumoNetworkLoader;

/**
* MapPanel is a JavaFX view component responsible for rendering a SUMO road network
* and vehicles using a Cartesian coordinate system.
* <p>
* Responsibilities:
* <ul>
* <li>Render SUMO edges and lanes</li>
* <li>Render vehicles aligned to lanes</li>
* <li>Handle pan, zoom, rotation, and reset interactions</li>
* </ul>
* The map elements (edges and vehicles) reside in a single {@code world} {@link Group}
* which is transformed by {@code viewTransform} to handle all viewing manipulations.
*
* @author Pham Tran Minh Anh
*/
public class MapPanel extends StackPane {
    
	private static final double MIN_SCALE = 0.5;
	private static final double MAX_SCALE = 5.0;
	private static final double ZOOM_STEP = 1.10;

	/** The current visual scale factor applied to the world group. */
	private double scale = 1.0;
	/** Mouse anchor X and Y position for panning */
	private double anchorX, anchorY;
	/** Transform X and Y offset at pan start */
	private double anchorTx, anchorTy;

	// default lane width in meters (world units)
	private final double defaultLaneWidthMeters = 3.2;

	/** Viewport acting as the visible camera window */
	private final Pane viewport = new Pane();
	/** Root world node that receives all transformations */
	private final Group world = new Group();
	/** Layer containing road edges and lanes */
	private final Group edgeLayer = new Group();
	/** Layer containing vehicle shapes */
	private final Group vehicleLayer = new Group();
	/** Affine transform used for pan, zoom, and rotation */
	private final Affine viewTransform = new Affine();

	private SumoNetworkLoader.SumoNetwork network;
	/** List of vehicles currently rendered */
	private List<VehicleData> vehicles;

	/** Map of edge IDs to their rendered groups */
	private final Map<String, Group> edgeGroups = new HashMap<>();
	
	/** Current map rotation in degrees relative to their initial state */
	private double rotationDeg = 0.0;
   
	/**
	* Constructs the MapPanel and initializes:
	* <ul>
	* <li>Scene graph layers</li>
	* <li>Viewport clipping</li>
	* <li>Pan, zoom, and rotation controls</li>
	* <li>Reset view button</li>
	* </ul>
	*/
    public MapPanel() {
        setStyle("-fx-background-color: #F0F0F0; " + "-fx-border-color: #bdbdbd; " + "-fx-border-width: 0 2 0 2;");
       
        viewport.setStyle("-fx-background-color: #F0F0F0;");
        
        world.getChildren().addAll(edgeLayer, vehicleLayer);
        
        // Apply the Affine transform to the entire world group
        world.getTransforms().setAll(viewTransform);
        
        viewport.getChildren().add(world);
        
        // Create a clipping rectangle to ensure map elements don't draw outside the viewport bounds
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);
        
        getChildren().add(viewport);
        
        setupPanZoom();
        
        Button zoomInBtn = new Button("+");
        zoomInBtn.setPrefSize(36, 36);
        zoomInBtn.setStyle("-fx-background-color: #FFFFFF; " +
                          "-fx-border-color: #D1D1D6; " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 6; " +
                          "-fx-background-radius: 6; " +
                          "-fx-font-size: 18px; " +
                          "-fx-font-weight: 600; " +
                          "-fx-text-fill: #007AFF; " +
                          "-fx-cursor: hand; " +
                          "-fx-padding: 0; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");
        zoomInBtn.setOnMouseEntered(e -> 
            zoomInBtn.setStyle("-fx-background-color: #F5F5F7; " +
                          "-fx-border-color: #007AFF; " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 6; " +
                          "-fx-background-radius: 6; " +
                          "-fx-font-size: 18px; " +
                          "-fx-font-weight: 600; " +
                          "-fx-text-fill: #007AFF; " +
                          "-fx-cursor: hand; " +
                          "-fx-padding: 0; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,122,255,0.3), 6, 0, 0, 2);")
        );
        zoomInBtn.setOnMouseExited(e -> 
            zoomInBtn.setStyle("-fx-background-color: #FFFFFF; " +
                          "-fx-border-color: #D1D1D6; " +
                          "-fx-border-width: 1; " +
                          "-fx-border-radius: 6; " +
                          "-fx-background-radius: 6; " +
                          "-fx-font-size: 18px; " +
                          "-fx-font-weight: 600; " +
                          "-fx-text-fill: #007AFF; " +
                          "-fx-cursor: hand; " +
                          "-fx-padding: 0; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);")
        );
        zoomInBtn.setOnAction(e -> zoomIn());
        
        Button zoomOutBtn = new Button("−");
        zoomOutBtn.setPrefSize(36, 36);
        zoomOutBtn.setStyle("-fx-background-color: #FFFFFF; " +
                           "-fx-border-color: #D1D1D6; " +
                           "-fx-border-width: 1; " +
                           "-fx-border-radius: 6; " +
                           "-fx-background-radius: 6; " +
                           "-fx-font-size: 20px; " +
                           "-fx-font-weight: 600; " +
                           "-fx-text-fill: #007AFF; " +
                           "-fx-cursor: hand; " +
                           "-fx-padding: 0; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");
        zoomOutBtn.setOnMouseEntered(e -> 
            zoomOutBtn.setStyle("-fx-background-color: #F5F5F7; " +
                           "-fx-border-color: #007AFF; " +
                           "-fx-border-width: 1; " +
                           "-fx-border-radius: 6; " +
                           "-fx-background-radius: 6; " +
                           "-fx-font-size: 20px; " +
                           "-fx-font-weight: 600; " +
                           "-fx-text-fill: #007AFF; " +
                           "-fx-cursor: hand; " +
                           "-fx-padding: 0; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,122,255,0.3), 6, 0, 0, 2);")
        );
        zoomOutBtn.setOnMouseExited(e -> 
            zoomOutBtn.setStyle("-fx-background-color: #FFFFFF; " +
                           "-fx-border-color: #D1D1D6; " +
                           "-fx-border-width: 1; " +
                           "-fx-border-radius: 6; " +
                           "-fx-background-radius: 6; " +
                           "-fx-font-size: 20px; " +
                           "-fx-font-weight: 600; " +
                           "-fx-text-fill: #007AFF; " +
                           "-fx-cursor: hand; " +
                           "-fx-padding: 0; " +
                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);")
        );
        zoomOutBtn.setOnAction(e -> zoomOut());
        
        Button rotateLeftBtn = new Button("⟲");
        Button rotateRightBtn = new Button("⟳");

        rotateLeftBtn.setOnAction(e -> rotateMap(-10));
        rotateRightBtn.setOnAction(e -> rotateMap(10));

        rotateLeftBtn.setPrefSize(36, 36);
        rotateRightBtn.setPrefSize(36, 36);
        
        VBox zoomControls = new VBox(4);
        zoomControls.getChildren().addAll(zoomInBtn, zoomOutBtn, rotateLeftBtn, rotateRightBtn);
        zoomControls.setStyle("-fx-background-color: transparent;");
        zoomControls.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE); 
        getChildren().add(zoomControls);
        StackPane.setAlignment(zoomControls, Pos.BOTTOM_RIGHT);
        
        Button resetBtn = new Button("⟳ Reset");
        resetBtn.setPrefSize(100, 36);
        resetBtn.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: #D1D1D6;" +
                "-fx-border-radius: 6;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: #1D1D1F;" +
                "-fx-cursor: hand;"
        );

        resetBtn.setOnAction(e -> resetView());
        
        VBox resetBox = new VBox();
        resetBox.getChildren().add(resetBtn);
        resetBox.setPickOnBounds(false); // click-through except button
        getChildren().add(resetBox);
        StackPane.setAlignment(resetBox, Pos.TOP_LEFT);
    }
    
    /**
     * Sets up the interactive Pan (drag) and Zoom (scroll) handlers on the map viewport.
     * <p>
     * **Panning:** Stores the current mouse and translation coordinates on press,
     * then updates the {@code viewTransform} translation during drag.
     * <p>
     * **Zooming:** On scroll, it calculates a new scale factor and uses
     * {@code viewTransform.appendScale(factor, factor, pivotX, pivotY)} to scale
     * around the current mouse cursor location (the pivot point).
     */
    private void setupPanZoom() {
        viewport.setOnMousePressed(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            anchorX = e.getX();
            anchorY = e.getY();
            anchorTx = viewTransform.getTx();
            anchorTy = viewTransform.getTy();
            viewport.setCursor(Cursor.CLOSED_HAND);
        });

        viewport.setOnMouseDragged(e -> {
            if (!e.isPrimaryButtonDown()) return;
            double dx = e.getX() - anchorX;
            double dy = e.getY() - anchorY;
            viewTransform.setTx(anchorTx + dx);
            viewTransform.setTy(anchorTy + dy);
        });

        viewport.setOnMouseReleased(e -> viewport.setCursor(Cursor.DEFAULT));

        viewport.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() == 0) return;

            double factor = (e.getDeltaY() > 0) ? ZOOM_STEP : 1.0 / ZOOM_STEP;
            double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
            factor = newScale / scale;

            Point2D pivot = world.sceneToLocal(e.getSceneX(), e.getSceneY());

            viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());

            scale = newScale;
            e.consume();
        });
    }
    
    /**
    * Assigns the SUMO network model to this view.
    *
    * @param network loaded SUMO network data
    */
    public void setNetwork(SumoNetworkLoader.SumoNetwork network) {
    	this.network = network;
    }

    /**
    * Sets the list of vehicles to be rendered and attaches their shapes
    * to the vehicle rendering layer.
    *
    * @param vehicles list of vehicle data objects
    */
    public void setVehicles(List<VehicleData> vehicles) {
    	this.vehicles = vehicles;
    	vehicleLayer.getChildren().clear();
    	if (vehicles == null) return;
    	for (VehicleData v : vehicles) {
    		vehicleLayer.getChildren().add(v.getShape());
    	}
    }
   
    /**
    * Renders the road network by iterating through all edges and lanes.
    * Each lane shape (a polyline of {@link SumoPosition2D} points) is drawn
    * as a sequence of thick {@link Line} segments.
    * <p>
    * **Coordinate System Note:** SUMO uses a standard cartesian system (Y-up),
    * but JavaFX uses a screen system (Y-down). Positions are rendered using
    * `(a.x, -a.y)` to flip the Y-axis for correct visual orientation.
    */
    public void renderMap() {
        if (network == null || network.edges == null) return;

        edgeLayer.getChildren().clear();
        edgeGroups.clear();

        // create edge groups and lane polylines
        for (Edge edge : network.edges) {
            if (edge == null || edge.lanes == null || edge.lanes.isEmpty()) continue;

            Group edgeGroup = new Group();

            for (Lane lane : edge.lanes) {
                if (lane == null || lane.shape == null || lane.shape.size() < 2) continue;

                // draw polyline as sequence of lines
                for (int i = 0; i < lane.shape.size() - 1; i++) {
                    SumoPosition2D a = lane.shape.get(i);
                    SumoPosition2D b = lane.shape.get(i + 1);

                    Line line = new Line(a.x, -a.y, b.x, -b.y); 
                    line.setStrokeWidth(defaultLaneWidthMeters);
                    line.setStroke(Color.GRAY);
                    line.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                    edgeGroup.getChildren().add(line);
                }
            }

            edgeLayer.getChildren().add(edgeGroup);
            edgeGroups.put(edge.id, edgeGroup);
        }

        javafx.application.Platform.runLater(this::centerAndFit);
    }
    
    /**
    * Resets the {@code viewTransform} and calculates the necessary scale and translation
    * to fit the entire rendered network ({@code edgeLayer}) within the current viewport,
    * maintaining aspect ratio and adding a small margin.
    * This method is called upon map load and when the view is manually reset.
    */
    private void centerAndFit() {
        // reset transform, then compute bounds
        viewTransform.setToIdentity();
        scale = 1.0;
        rotationDeg = 0.0;

        // compute bounds in world coords by collecting laneLayer bounds
        javafx.geometry.Bounds bounds = edgeLayer.getBoundsInParent();
        if (bounds == null || bounds.isEmpty()) return;

        double minX = bounds.getMinX();
        double minY = bounds.getMinY();
        double maxX = bounds.getMaxX();
        double maxY = bounds.getMaxY();

        double mapWidth = Math.max(1.0, maxX - minX);
        double mapHeight = Math.max(1.0, maxY - minY);

        double pw = viewport.getWidth();
        double ph = viewport.getHeight();
        if (pw <= 0 || ph <= 0) return;

        double fitScaleX = pw / mapWidth;
        double fitScaleY = ph / mapHeight;
        double fitScale = Math.min(fitScaleX, fitScaleY) * 0.9; // keep margin

        // apply scale then translate so map center aligns with viewport center
        viewTransform.appendScale(fitScale, fitScale);
        double mapCenterX = (minX + maxX) / 2.0;
        double mapCenterY = (minY + maxY) / 2.0;

        double tx = pw / 2.0 / fitScale - mapCenterX;
        double ty = ph / 2.0 / fitScale - mapCenterY;

        viewTransform.appendTranslation(tx, ty);
        scale = fitScale;
    }
    
    /**
    * Called every frame/tick to update the visual position and rotation of vehicles.
    * The position is set using {@code setLayoutX/Y} based on the raw SUMO coordinates,
    * and the rotation is applied locally to the vehicle's shape. The vehicle shapes
    * are implicitly moved, scaled, and rotated by the parent {@code world} group's
    * {@code viewTransform}.
    */
    public void updateVehicles() {
        if (vehicles == null) return;

        for (VehicleData v : vehicles) {
            Rectangle r = v.getShape();
            SumoPosition2D p = v.getPosition();

            // Position in WORLD coordinates (same as lanes)
            r.setLayoutX(p.x);
            r.setLayoutY(-p.y); // flip Y once

            // Rotation ONLY (vehicle-local)
            r.setRotate(v.getAngle());
        }
    }
    
    /**
    * Clamps a value between a minimum and maximum range.
    *
    * @param v input value
    * @param min minimum allowed value
    * @param max maximum allowed value
    * @return clamped value
    */
    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
    
    /**
    * Zooms the map in around the center of the viewport.
    */
    private void zoomIn() {
        double factor = ZOOM_STEP;
        double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
        factor = newScale / scale;
        
        double centerX = viewport.getWidth() / 2;
        double centerY = viewport.getHeight() / 2;
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }
    
    /**
    * Zooms the map out around the center of the viewport.
    */
    private void zoomOut() {
        double factor = 1.0 / ZOOM_STEP;
        double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
        factor = newScale / scale;
        
        double centerX = viewport.getWidth() / 2;
        double centerY = viewport.getHeight() / 2;
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }
    
    /**
    * Rotates the map around the center of the viewport by a specified angle.
    * The rotation is appended to the existing {@code viewTransform}.
    *
    * @param deltaDeg rotation change in degrees (positive = clockwise)
    */
    public void rotateMap(double deltaDeg) {
        rotationDeg += deltaDeg;

        double centerX = viewport.getWidth() / 2;
        double centerY = viewport.getHeight() / 2;

        // Convert screen center to world coordinates to find the pivot point
        Point2D pivot = world.sceneToLocal(
                viewport.localToScene(centerX, centerY)
        );

        viewTransform.appendRotation(rotationDeg, pivot.getX(), pivot.getY());
    }
    
    /**
    * Resets the map view to its default state:
    * <ul>
    * <li>Zero rotation</li>
    * <li>Default zoom</li>
    * <li>Centered and fitted network</li>
    * </ul>
    */
    public void resetView() {
        rotationDeg = 0.0;
        javafx.application.Platform.runLater(this::centerAndFit);
    }
}
