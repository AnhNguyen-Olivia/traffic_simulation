package real_time_traffic_simulation_with_java.gui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * MapPanel - Optimized map display with Affine Transform
 * 
 * Features:
 * - Affine Transform for smooth pan/zoom
 * - Scroll wheel or +/- button zoom
 * - Mouse drag pan
 * - Dynamic clipping with viewport
 */
public class MapPanel extends StackPane {
    
    private SimulationEngine simulationEngine;
    private final Pane viewport;
    private final Group world;
    private final Group laneLayer;
    private final Group trafficLightLayer;
    private final Group vehicleLayer;
    private Affine viewTransform;
    private boolean isMapCentered = false;
    
    // Pan/Zoom state
    private double anchorX = 0;
    private double anchorY = 0;
    private double anchorTx = 0;
    private double anchorTy = 0;
    private double scale = 1.0;
    
    // Zoom constraints
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 5.0;
    private static final double ZOOM_STEP = 1.15;
    
    /**
     * Constructor - Initialize MapPanel with Affine Transform for pan/zoom
     */
    public MapPanel() {
        // Setup style
        setStyle("-fx-background-color: #F0F0F0; " +
                 "-fx-border-color: #bdbdbd; " +
                 "-fx-border-width: 0 2 0 2;");
        
        // Create viewport (Pane containing world) - will clip content
        viewport = new Pane();
        viewport.setStyle("-fx-background-color: #FFFFFF;");
        
        // Create world (Group containing map layers) - will be transformed
        world = new Group();
        
        // Create layers
        laneLayer = new Group();
        trafficLightLayer = new Group();
        vehicleLayer = new Group();
        
        // Add layers to world (order: lanes → tls → vehicles)
        world.getChildren().addAll(laneLayer, trafficLightLayer, vehicleLayer);
        
        // Create Affine transform for world
        viewTransform = new Affine();
        world.getTransforms().setAll(viewTransform);
        
        // Add world to viewport
        viewport.getChildren().add(world);
        
        // Bind clipping to viewport size
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);
        
        // Add viewport to panel
        getChildren().add(viewport);
        
        // Setup pan/zoom handlers
        setupPanZoom();
        
        // Create zoom + button (macOS style)
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
        
        // Create zoom - button (macOS style)
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
        
        // Create VBox containing zoom buttons
        VBox zoomControls = new VBox(2);
        zoomControls.getChildren().addAll(zoomInBtn, zoomOutBtn);
        zoomControls.setStyle("-fx-background-color: transparent;");
        zoomControls.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
        
        // Add zoom controls to panel
        getChildren().add(zoomControls);
        
        // Position zoom controls at bottom-right
        setAlignment(zoomControls, Pos.BOTTOM_RIGHT);
        
        // Responsive margin
        widthProperty().addListener((obs, old, newVal) -> {
            double margin = newVal.doubleValue() > 800 ? 16 : 10;
            setMargin(zoomControls, new Insets(0, margin, margin, 0));
        });
        
        System.out.println("✅ MapPanel initialized - Affine Transform optimized");
    }
    
    /**
     * Setup pan and zoom handlers with Affine Transform
     */
    private void setupPanZoom() {
        // Pan with mouse
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

        // Zoom at mouse position with scroll wheel
        viewport.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() == 0) return;

            double factor = (e.getDeltaY() > 0) ? ZOOM_STEP : 1.0 / ZOOM_STEP;
            double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
            factor = newScale / scale;

            // Pivot in LOCAL coordinates of world
            Point2D pivot = world.sceneToLocal(e.getSceneX(), e.getSceneY());

            // appendScale with local pivot keeps pivot fixed on screen
            viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());

            scale = newScale;
            e.consume();
        });
    }
    
    /**
     * Zoom in
     */
    private void zoomIn() {
        // Zoom at center of viewport
        Bounds viewportBounds = viewport.getBoundsInLocal();
        double centerX = viewportBounds.getCenterX();
        double centerY = viewportBounds.getCenterY();
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
        double newScale = clamp(scale * ZOOM_STEP, MIN_SCALE, MAX_SCALE);
        double factor = newScale / scale;
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }
    
    /**
     * Zoom out
     */
    private void zoomOut() {
        // Zoom at center of viewport
        Bounds viewportBounds = viewport.getBoundsInLocal();
        double centerX = viewportBounds.getCenterX();
        double centerY = viewportBounds.getCenterY();
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
        double newScale = clamp(scale / ZOOM_STEP, MIN_SCALE, MAX_SCALE);
        double factor = newScale / scale;
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }
    
    /**
     * Clamp value between min and max
     */
    private double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }
    
    /**
     * Set SimulationEngine and load initial data
     */
    public void setSimulationEngine(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        updateDisplay();
    }
    
    /**
     * Update display with latest map data from SimulationEngine
     */
    public void updateDisplay() throws Exception {
        if (simulationEngine == null) return;
        
        laneLayer.getChildren().clear();
        trafficLightLayer.getChildren().clear();
        vehicleLayer.getChildren().clear();
        
        // Get pre-rendered shapes from SimulationEngine (returns Lists now)
        var edges = simulationEngine.getMapEdges();
        var junctions = simulationEngine.getMapJunctions();
        var tls = simulationEngine.getMapTls();
        var vehicles = simulationEngine.getMapVehicles();
        
        // Add edges and junctions to lane layer
        if (edges != null) laneLayer.getChildren().addAll(edges);
        if (junctions != null) laneLayer.getChildren().addAll(junctions);
        
        // Add traffic lights to TLS layer
        if (tls != null) trafficLightLayer.getChildren().addAll(tls);
        
        // Add vehicles to vehicle layer
        if (vehicles != null) vehicleLayer.getChildren().addAll(vehicles);
        
        // Center view chỉ lần đầu tiên sau khi render xong map
        if (!isMapCentered) {
            centerMap();
        }
    }
    
    /**
     * Center map in viewport
     */
    private void centerMap() {
        // Nếu viewport chưa có kích thước, thử lại sau khi layout hoàn thành
        if (viewport.getWidth() == 0 || viewport.getHeight() == 0) {
            System.out.println("⏳ Viewport chưa có kích thước, đợi layout hoàn thành...");
            Platform.runLater(() -> {
                if (!isMapCentered) {
                    centerMap();
                }
            });
            return;
        }
        
        try {
            // Get bounds of all content in world
            Bounds worldBounds = world.getBoundsInLocal();
            if (worldBounds == null || worldBounds.isEmpty()) {
                System.err.println("⚠️  World bounds empty!");
                return;
            }
            
            Bounds viewportBounds = viewport.getBoundsInLocal();
            double mapWidth = worldBounds.getWidth();
            double mapHeight = worldBounds.getHeight();
            double viewWidth = viewportBounds.getWidth();
            double viewHeight = viewportBounds.getHeight();
            
            // Center with some padding
            double offsetX = (viewWidth / 2.0) - (worldBounds.getCenterX());
            double offsetY = (viewHeight / 2.0) - (worldBounds.getCenterY());
            
            viewTransform.setTx(offsetX);
            viewTransform.setTy(offsetY);
            
            System.out.println("✅ Map centered - Bounds: " + mapWidth + "x" + mapHeight + 
                             " in viewport: " + viewWidth + "x" + viewHeight);
            
            isMapCentered = true;
        } catch (Exception e) {
            System.err.println("⚠️  Error centering map: " + e.getMessage());
        }
    }
}