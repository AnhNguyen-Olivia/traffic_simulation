package real_time_traffic_simulation_with_java.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudresden.sumo.objects.SumoPosition2D;
import javafx.geometry.Insets;
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

public class MapPanel extends StackPane {
    
	private static final double MIN_SCALE = 0.5;
	private static final double MAX_SCALE = 5.0;
	private static final double ZOOM_STEP = 1.10;


	private double scale = 1.0;
	private double anchorX, anchorY;
	private double anchorTx, anchorTy;


	// default lane width in meters (world units)
	private final double defaultLaneWidthMeters = 3.2;


	private final Pane viewport = new Pane();
	private final Group world = new Group();
	private final Group edgeLayer = new Group();
	private final Group vehicleLayer = new Group();
	private final Affine viewTransform = new Affine();


	// current network data
	private SumoNetworkLoader.SumoNetwork network;
	private List<VehicleData> vehicles;


	private final Map<String, Group> edgeGroups = new HashMap<>();
   
    public MapPanel() {
        setStyle("-fx-background-color: #F0F0F0; " + "-fx-border-color: #bdbdbd; " + "-fx-border-width: 0 2 0 2;");
       
        viewport.setStyle("-fx-background-color: #F0F0F0;");
        
        world.getChildren().addAll(edgeLayer, vehicleLayer);
        
        world.getTransforms().setAll(viewTransform);
        
        viewport.getChildren().add(world);
        
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);
        
        getChildren().add(viewport);
        
        setupPanZoom();
        
        Button zoomInBtn = new Button("+");
        zoomInBtn.setMinSize(36, 36);
        zoomInBtn.setMaxSize(36, 36);
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
        zoomOutBtn.setMinSize(36, 36);
        zoomOutBtn.setMaxSize(36, 36);
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
        
        VBox zoomControls = new VBox(2); 
        zoomControls.getChildren().addAll(zoomInBtn, zoomOutBtn);
        zoomControls.setStyle("-fx-background-color: transparent;");
        zoomControls.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE); 
        
        getChildren().add(zoomControls);
        
        StackPane.setAlignment(zoomControls, Pos.BOTTOM_RIGHT); 
        
        widthProperty().addListener((obs, old, newVal) -> {
            double margin = newVal.doubleValue() > 800 ? 16 : 10;
            StackPane.setMargin(zoomControls, new Insets(0, margin, margin, 0));
        });
    }
    
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
    
    public void setNetwork(SumoNetworkLoader.SumoNetwork network) {
    	this.network = network;
    }

    public void setVehicles(List<VehicleData> vehicles) {
    	this.vehicles = vehicles;
    	vehicleLayer.getChildren().clear();
    	if (vehicles == null) return;
    	for (VehicleData v : vehicles) {
    		vehicleLayer.getChildren().add(v.getShape());
    	}
    }
   
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

                    Line line = new Line(a.x, -a.y, b.x, -b.y); // flip Y for display
                    // lane.length may not be suitable for width; use defaultLaneWidthMeters always
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
    
    private void centerAndFit() {
        // reset transform, then compute bounds
        viewTransform.setToIdentity();
        scale = 1.0;

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
    * Called every frame after network.updateVehicles().
    * Applies correct transforms so vehicles follow lanes.
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
    
    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
    
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
}
