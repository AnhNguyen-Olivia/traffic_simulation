package real_time_traffic_simulation_with_java.gui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.util.Duration;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.mapLayer.roadLayer;
import real_time_traffic_simulation_with_java.gui.mapLayer.trafficlightLayer;
import real_time_traffic_simulation_with_java.gui.mapLayer.vehicleLayer;

/**
 * MapPanel (Merged + Optimized)
 *
 * Giữ UI/UX của bản Affine (smooth pan/zoom, viewport clip, zoom buttons),
 * đồng thời dùng mapLayer classes (roadLayer/vehicleLayer/trafficlightLayer),
 * refresh tối ưu (chỉ refresh vehicle + traffic light), và tooltip.
 */
public class MapPanel extends StackPane {

    private SimulationEngine simulationEngine;

    // Viewport + world (Affine transforms)
    private final Pane viewport;
    private final Group world;

    // Layer container (giữ thứ tự: road -> tls -> vehicles hoặc road -> vehicles -> tls)
    // Mình giữ TL trên cùng như đoạn 2: road, vehicle, trafficlight (top-most)
    private final Group roadContainer = new Group();
    private final Group vehicleContainer = new Group();
    private final Group trafficLightContainer = new Group();

    // Actual layer instances (từ package gui.mapLayer.*)
    private roadLayer roadLayerNode;
    private vehicleLayer vehicleLayerNode;
    private trafficlightLayer trafficlightLayerNode;

    private final Affine viewTransform = new Affine();
    private boolean isMapCentered = false;

    // Pan state
    private double anchorX = 0;
    private double anchorY = 0;
    private double anchorTx = 0;
    private double anchorTy = 0;

    // Zoom state
    private double scale = 1.0;
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 5.0;
    private static final double ZOOM_STEP = 1.15;

    public MapPanel() {
        setStyle("-fx-background-color: #F0F0F0; " +
                 "-fx-border-color: #bdbdbd; " +
                 "-fx-border-width: 0 2 0 2;");

        viewport = new Pane();
        viewport.setStyle("-fx-background-color: #FFFFFF;");

        world = new Group();

        // Thứ tự layer: road -> vehicles -> traffic light (top-most)
        world.getChildren().addAll(roadContainer, vehicleContainer, trafficLightContainer);

        // (Optional) nếu bạn vẫn cần “flip Y” như SUMO -> JavaFX:
        // Cách “đúng” nhất là flip ở WORLD để mọi layer đồng bộ, và pivot tại 0.
        // Nếu roadLayer/vehicleLayer/trafficlightLayer đã tự flip thì comment dòng này.
        world.setScaleY(-1);

        world.getTransforms().setAll(viewTransform);

        viewport.getChildren().add(world);

        // Clip theo viewport
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);

        getChildren().add(viewport);

        setupPanZoom();       // Affine pan/zoom (đoạn 1)
        setupRotateOptional(); // (đoạn 2 có rotate) — mình để “optional” để không phá UX

        addZoomButtons();     // GUI zoom +/- (đoạn 1)

        System.out.println("✅ MapPanel merged initialized (Affine + mapLayer + tooltips + optimized refresh)");
    }

    public MapPanel(SimulationEngine engine) {
        this();
        try {
            setSimulationEngine(engine);
        } catch (Exception e) {
            System.err.println("Error initializing MapPanel with engine: " + e.getMessage());
        }
    }

    public void setSimulationEngine(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        buildInitialLayers();     // build road + vehicle + tl
        installTooltips();        // tooltip cho nodes
        centerMapOnceReady();     // center lần đầu
    }

    /**
     * Build map layers (road + vehicle + traffic light).
     * Road layer thường "tĩnh" -> build 1 lần.
     */
    private void buildInitialLayers() throws Exception {
        if (simulationEngine == null) return;

        // Clear containers (không đụng viewTransform)
        roadContainer.getChildren().clear();
        vehicleContainer.getChildren().clear();
        trafficLightContainer.getChildren().clear();

        // Create layers từ mapLayer package (đoạn 2)
        roadLayerNode = new roadLayer(simulationEngine);
        vehicleLayerNode = new vehicleLayer(simulationEngine);
        trafficlightLayerNode = new trafficlightLayer(simulationEngine);

        // Add vào containers (giữ top-most TL)
        roadContainer.getChildren().add(roadLayerNode);
        vehicleContainer.getChildren().add(vehicleLayerNode);
        trafficLightContainer.getChildren().add(trafficlightLayerNode);

        isMapCentered = false;
    }

    /**
     * Optimized refresh:
     * - Road tĩnh -> không rebuild mỗi frame
     * - Vehicle: rebuild node (như đoạn 2)
     * - Traffic light: gọi refreshTrafficLightLayer()
     */
    public void refresh() throws Exception {
        if (simulationEngine == null) return;

        // Refresh vehicle layer (rebuild)
        vehicleLayer newVehicles = new vehicleLayer(simulationEngine);
        vehicleContainer.getChildren().setAll(newVehicles);
        vehicleLayerNode = newVehicles;

        // Refresh traffic light state (không rebuild toàn bộ nếu class hỗ trợ)
        if (trafficlightLayerNode != null) {
            trafficlightLayerNode.refreshTrafficLightLayer();
        }

        // Tooltips: vehicles rebuild => cài lại tooltip cho vehicle nodes
        installTooltipsVehiclesOnly();
    }

    // -------------------------
    // Pan / Zoom (Affine)
    // -------------------------
    private void setupPanZoom() {
        // Double-click toggle traffic light (giữ từ đoạn 1)
        viewport.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                handleDoubleClick(e.getSceneX(), e.getSceneY());
            }
        });

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
            viewTransform.setTy(anchorTy - dy);
        });

        viewport.setOnMouseReleased(e -> viewport.setCursor(Cursor.DEFAULT));

        // Zoom at mouse position
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

    // -------------------------
    // Rotate (optional, từ đoạn 2)
    // Mặc định tắt để giữ UX đoạn 1 “đúng chất map”.
    // Nếu muốn bật: uncomment handler ở dưới.
    // -------------------------
    private void setupRotateOptional() {
        // Nếu bạn muốn rotate giống đoạn 2, bật block này.
        // Mình cài vào WORLD để đồng bộ tất cả layer, vẫn giữ clip & affine.
        final double[] delta = new double[3];

        world.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (!e.isSecondaryButtonDown()) return;
            delta[0] = e.getSceneX();
            delta[1] = e.getSceneY();
            delta[2] = world.getRotate();
        });

        world.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (!e.isSecondaryButtonDown()) return;
            double angleX = e.getSceneX() - delta[0];
            double angleY = e.getSceneY() - delta[1];
            world.setRotate(delta[2] + (angleX + angleY) / 2.0);
        });
    }

    private void zoomIn() {
        Bounds viewportBounds = viewport.getBoundsInLocal();
        double centerX = viewportBounds.getCenterX();
        double centerY = viewportBounds.getCenterY();
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));

        double newScale = clamp(scale * ZOOM_STEP, MIN_SCALE, MAX_SCALE);
        double factor = newScale / scale;
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }

    private void zoomOut() {
        Bounds viewportBounds = viewport.getBoundsInLocal();
        double centerX = viewportBounds.getCenterX();
        double centerY = viewportBounds.getCenterY();
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));

        double newScale = clamp(scale / ZOOM_STEP, MIN_SCALE, MAX_SCALE);
        double factor = newScale / scale;
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }

    private static double clamp(double value, double min, double max) {
        return value < min ? min : (value > max ? max : value);
    }

    // -------------------------
    // Centering
    // -------------------------
    private void centerMapOnceReady() {
        if (viewport.getWidth() == 0 || viewport.getHeight() == 0) {
            Platform.runLater(this::centerMapOnceReady);
            return;
        }
        if (!isMapCentered) centerMap();
    }

    private void centerMap() {
        try {
            Bounds worldBounds = world.getBoundsInLocal();
            if (worldBounds == null || worldBounds.isEmpty()) {
                System.err.println("⚠️ World bounds empty!");
                return;
            }

            Bounds viewportBounds = viewport.getBoundsInLocal();
            double offsetX = (viewportBounds.getWidth() / 2.0) - worldBounds.getCenterX();
            double offsetY = (viewportBounds.getHeight() / 2.0) - worldBounds.getCenterY();

            viewTransform.setTx(offsetX);
            viewTransform.setTy(offsetY);

            isMapCentered = true;
            System.out.println("✅ Map centered");
        } catch (Exception e) {
            System.err.println("⚠️ Error centering map: " + e.getMessage());
        }
    }

    // -------------------------
    // Double-click traffic light toggle
    // -------------------------
    private void handleDoubleClick(double sceneX, double sceneY) {
        try {
            // Click vào trafficLightContainer (top-most)
            for (Node node : trafficLightContainer.getChildren()) {
                if (node instanceof Group group) {
                    // tìm sâu nếu tl nodes nằm bên trong group
                    Node hit = pickDeep(group, sceneX, sceneY);
                    if (hit != null) {
                        String tlID = hit.getId();
                        if (tlID == null) tlID = group.getId(); // fallback

                        if (tlID != null && simulationEngine != null) {
                            simulationEngine.toggleSingleTl(tlID);
                            if (trafficlightLayerNode != null) trafficlightLayerNode.refreshTrafficLightLayer();
                            System.out.println("🚦 Double-clicked traffic light: " + tlID);
                            return;
                        }
                    }
                } else {
                    if (node.contains(node.sceneToLocal(sceneX, sceneY))) {
                        String tlID = node.getId();
                        if (tlID != null && simulationEngine != null) {
                            simulationEngine.toggleSingleTl(tlID);
                            if (trafficlightLayerNode != null) trafficlightLayerNode.refreshTrafficLightLayer();
                            System.out.println("🚦 Double-clicked traffic light: " + tlID);
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error handling double-click: " + e.getMessage());
        }
    }

    private Node pickDeep(Node root, double sceneX, double sceneY) {
        if (root == null) return null;
        if (!root.isVisible()) return null;

        // Nếu root là Group, duyệt con từ trên xuống (top-most trước)
        if (root instanceof Group g) {
            for (int i = g.getChildren().size() - 1; i >= 0; i--) {
                Node child = g.getChildren().get(i);
                Node hit = pickDeep(child, sceneX, sceneY);
                if (hit != null) return hit;
            }
        }

        // Check self
        if (root.contains(root.sceneToLocal(sceneX, sceneY))) return root;
        return null;
    }

    // -------------------------
    // Tooltips
    // -------------------------
    private void installTooltips() {
        installTooltipsTrafficLights();
        installTooltipsVehiclesOnly();
        // Road tooltip thường không cần (nặng + rối UI). Nếu muốn, bạn cài thêm tương tự.
    }

    private void installTooltipsTrafficLights() {
        if (trafficlightLayerNode == null) return;

        // Tìm các node có id (tl id) để tooltip
        attachTooltipRecursively(trafficlightLayerNode, node -> {
            String id = node.getId();
            if (id == null || id.isBlank()) return null;
            return "Traffic Light\nID: " + id + "\n(Double-click để toggle)";
        });
    }

    private void installTooltipsVehiclesOnly() {
        if (vehicleLayerNode == null) return;

        attachTooltipRecursively(vehicleLayerNode, node -> {
            String id = node.getId();
            if (id == null || id.isBlank()) return null;
            return "Vehicle\nID: " + id;
        });
    }

    @FunctionalInterface
    private interface TooltipTextProvider {
        String get(Node node);
    }

    private void attachTooltipRecursively(Node root, TooltipTextProvider provider) {
        if (root == null) return;

        String text = provider.get(root);
        if (text != null) {
            Tooltip tip = new Tooltip(text);
            tip.setShowDelay(Duration.millis(120));
            tip.setHideDelay(Duration.millis(50));
            Tooltip.install(root, tip);
        }

        if (root instanceof Group g) {
            for (Node child : g.getChildren()) {
                attachTooltipRecursively(child, provider);
            }
        }
    }

    // -------------------------
    // Zoom buttons UI (giữ y nguyên style đoạn 1)
    // -------------------------
    private void addZoomButtons() {
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

        VBox zoomControls = new VBox(2, zoomInBtn, zoomOutBtn);
        zoomControls.setStyle("-fx-background-color: transparent;");
        zoomControls.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);

        getChildren().add(zoomControls);
        setAlignment(zoomControls, Pos.BOTTOM_RIGHT);

        widthProperty().addListener((obs, old, newVal) -> {
            double margin = newVal.doubleValue() > 800 ? 16 : 10;
            setMargin(zoomControls, new Insets(0, margin, margin, 0));
        });
    }
}
