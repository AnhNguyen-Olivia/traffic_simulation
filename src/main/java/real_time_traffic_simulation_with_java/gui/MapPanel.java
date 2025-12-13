package real_time_traffic_simulation_with_java.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoGeometry;
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
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import real_time_traffic_simulation_with_java.wrapper.LaneManager;
import real_time_traffic_simulation_with_java.wrapper.VehicleManager;

/**
 * MapPanel - Panel hiển thị bản đồ ở giữa với zoom controls
 * Sử dụng Affine Transform cho pan/zoom mượt mà và chính xác
 */
public class MapPanel extends StackPane {
    
    // Zoom settings - cho phép zoom rất sâu để xem từng con đường
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 15.0;  // Tăng lên 15x để zoom sâu
    private static final double ZOOM_STEP = 1.15;  // Tăng step để zoom nhanh hơn
    
    private double scale = 1.0;
    private double anchorX, anchorY;
    private double anchorTx, anchorTy;
    
    // Components
    private final Pane viewport;
    private final Group world;
    private final Group laneLayer;      // Layer chứa các lane (đường)
    private final Group trafficLightLayer;   // Layer chứa traffic lights
    private final Group vehicleLayer;   // Layer chứa các xe
    private final Affine viewTransform;
    
    // SUMO Managers - sẽ được set từ bên ngoài
    private LaneManager laneManager;
    private VehicleManager vehicleManager;
    private real_time_traffic_simulation_with_java.wrapper.TrafficLightManager trafficLightManager;
    
    // Cache để lưu trữ shapes
    private final Map<String, Group> laneShapes = new HashMap<>();
    private final Map<String, Polygon> vehicleShapes = new HashMap<>();
    private final Map<String, javafx.scene.shape.Circle> trafficLightShapes = new HashMap<>();
    
    /**
     * Constructor - Khởi tạo MapPanel với Affine Transform cho pan/zoom tối ưu
     */
    public MapPanel() {
        // Thiết lập style cho MapPanel
        setStyle("-fx-background-color: #F0F0F0; " +
                 "-fx-border-color: #bdbdbd; " +
                 "-fx-border-width: 0 2 0 2;");
        
        // Tạo viewport (Pane chứa world) - sẽ clip content
        viewport = new Pane();
        viewport.setStyle("-fx-background-color: #F0F0F0;");
        
        // Tạo world (Group chứa map layers) - sẽ được transform
        world = new Group();
        
        // Tạo các layer
        laneLayer = new Group();
        trafficLightLayer = new Group();
        vehicleLayer = new Group();
        
        // Thêm layers vào world (thứ tự: lanes -> traffic lights -> vehicles)
        world.getChildren().addAll(laneLayer, trafficLightLayer, vehicleLayer);
        
        // Tạo Affine transform cho world
        viewTransform = new Affine();
        world.getTransforms().setAll(viewTransform);
        
        // Thêm world vào viewport
        viewport.getChildren().add(world);
        
        // Bind clipping cho viewport để không tràn ra ngoài
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);
        
        // Thêm viewport vào MapPanel
        getChildren().add(viewport);
        
        // Setup pan/zoom handlers
        setupPanZoom();
        
        // Tạo nút zoom + - macOS style
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
        
        // Tạo nút zoom - - macOS style
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
        
        // Tạo VBox chứa 2 nút zoom
        VBox zoomControls = new VBox(2); // Khoảng cách 2px giữa các nút
        zoomControls.getChildren().addAll(zoomInBtn, zoomOutBtn);
        zoomControls.setStyle("-fx-background-color: transparent;");
        zoomControls.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE); // Giữ kích thước nhỏ gọn
        
        // Thêm zoom controls vào panel
        getChildren().add(zoomControls);
        
        // Đặt zoom controls ở góc dưới bên phải - Responsive position
        StackPane.setAlignment(zoomControls, Pos.BOTTOM_RIGHT); // Chỉ set cho zoomControls thôi
        
        // Responsive margin - adjust based on viewport size
        widthProperty().addListener((obs, old, newVal) -> {
            double margin = newVal.doubleValue() > 800 ? 16 : 10;
            StackPane.setMargin(zoomControls, new Insets(0, margin, margin, 0));
        });
    }
    
    /**
     * Setup pan and zoom handlers với Affine Transform
     */
    private void setupPanZoom() {
        // Pan với chuột
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

        // Zoom tại vị trí chuột với mouse wheel/touchpad
        viewport.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() == 0) return;

            double factor = (e.getDeltaY() > 0) ? ZOOM_STEP : 1.0 / ZOOM_STEP;
            double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
            factor = newScale / scale;

            // Pivot trong tọa độ LOCAL của world
            Point2D pivot = world.sceneToLocal(e.getSceneX(), e.getSceneY());

            // Affine.appendScale với pivot local sẽ giữ pivot đứng yên trên màn hình
            viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());

            scale = newScale;
            e.consume();
        });
    }
    
    /**
     * Set SUMO managers để lấy dữ liệu
     */
    public void setManagers(LaneManager laneManager, VehicleManager vehicleManager, 
                           real_time_traffic_simulation_with_java.wrapper.TrafficLightManager trafficLightManager) {
        this.laneManager = laneManager;
        this.vehicleManager = vehicleManager;
        this.trafficLightManager = trafficLightManager;
    }
    
    /**
     * Render toàn bộ map (lanes) - chỉ gọi 1 lần khi khởi tạo
     */
    public void renderMap() {
        if (laneManager == null) {
            System.err.println("❌ LaneManager is NULL! Cannot render map.");
            return;
        }
        
        try {
            List<String> laneIDs = laneManager.getIDList();
            System.out.println("✅ Found " + laneIDs.size() + " lanes to render");
            
            if (laneIDs.isEmpty()) {
                System.err.println("❌ No lanes found in SUMO network!");
                return;
            }
            
            int successCount = 0;
            for (String laneID : laneIDs) {
                renderLane(laneID);
                successCount++;
            }
            
            System.out.println("✅ Successfully rendered " + successCount + " lanes");
            
            // Center view sau khi render xong map
            centerView();
        } catch (Exception e) {
            System.err.println("❌ Error rendering map: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Render một lane (đường)
     */
    private void renderLane(String laneID) {
        try {
            // Lấy dữ liệu lane
            SumoGeometry geometry = laneManager.getCoordinateList(laneID);
            double width = laneManager.getLength(laneID);
            
            System.out.println("🔧 Rendering lane: " + laneID + " (width: " + width + ")");
            
            if (geometry == null) {
                System.err.println("⚠️  Lane " + laneID + " has no geometry!");
                return;
            }
            
            Group laneGroup = new Group();
            
            // Vẽ từng đoạn của lane từ geometry - đơn giản và liền mạch
            // SumoGeometry là List<SumoPosition2D>
            List<de.tudresden.sumo.objects.SumoPosition2D> points = (List<de.tudresden.sumo.objects.SumoPosition2D>) geometry;
            if (points != null && points.size() > 1) {
                for (int i = 0; i < points.size() - 1; i++) {
                    de.tudresden.sumo.objects.SumoPosition2D p1 = points.get(i);
                    de.tudresden.sumo.objects.SumoPosition2D p2 = points.get(i + 1);
                    double[] point1 = {p1.x, p1.y};
                    double[] point2 = {p2.x, p2.y};
                
                // Debug: In ra coordinates
                if (i == 0) {
                    System.out.println("   First point: [" + point1[0] + ", " + point1[1] + "]");
                }
                
                // Vẽ mặt đường chính với màu xám và viền mượt
                Line laneLine = new Line(point1[0], -point1[1], point2[0], -point2[1]);
                laneLine.setStroke(Color.rgb(70, 70, 70)); // Màu xám đậm cho asphalt
                laneLine.setStrokeWidth(width); // Sử dụng đúng width của lane
                laneLine.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND); // Bo tròn đầu mút để liền mạch
                laneLine.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND); // Bo tròn góc nối
                laneLine.setSmooth(true); // Làm mượt đường
                
                laneGroup.getChildren().add(laneLine);
                }
            }
            
            System.out.println("   ✅ Added " + (points != null ? (points.size() - 1) : 0) + " line segments");
            
            // Lưu vào cache và thêm vào layer
            laneShapes.put(laneID, laneGroup);
            laneLayer.getChildren().add(laneGroup);
            
        } catch (Exception e) {
            System.err.println("❌ Error rendering lane " + laneID + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Render traffic lights - gọi 1 lần khi khởi tạo
     */
    public void renderTrafficLights() {
        if (trafficLightManager == null || laneManager == null) return;
        
        try {
            List<String> tlIDs = trafficLightManager.getIDList();
            System.out.println("\n========================================");
            System.out.println("🚦 TRAFFIC LIGHT RENDERING");
            System.out.println("========================================");
            System.out.println("Total traffic lights found: " + tlIDs.size());
            System.out.println("Traffic light IDs: " + tlIDs);
            System.out.println("========================================\n");
            
            int successCount = 0;
            int skippedCount = 0;
            
            for (String tlID : tlIDs) {
                int beforeSize = trafficLightLayer.getChildren().size();
                renderTrafficLight(tlID);
                int afterSize = trafficLightLayer.getChildren().size();
                
                if (afterSize > beforeSize) {
                    successCount++;
                } else {
                    skippedCount++;
                }
            }
            
            System.out.println("\n========================================");
            System.out.println("📊 RENDERING SUMMARY:");
            System.out.println("   ✅ Successfully rendered: " + successCount);
            System.out.println("   ⚠️  Skipped: " + skippedCount);
            System.out.println("   📍 Total on map: " + trafficLightShapes.size());
            System.out.println("========================================\n");
            
        } catch (Exception e) {
            System.err.println("❌ Error rendering traffic lights: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Render một traffic light với cột đèn và trạng thái
     */
    private void renderTrafficLight(String tlID) {
        try {
            // Lấy traffic lanes để tìm vị trí
            List<String> trafficLanes = trafficLightManager.getLaneTraffic(tlID);
            if (trafficLanes == null || trafficLanes.isEmpty()) {
                System.out.println("⚠️  Traffic light " + tlID + " has no traffic lanes - SKIPPED");
                return;
            }
            
            System.out.println("   Processing TL " + tlID + " with " + trafficLanes.size() + " traffic lanes: " + trafficLanes);
            
            // Lấy lane đầu tiên để xác định vị trí
            String firstLane = trafficLanes.get(0);
            SumoGeometry geometry = laneManager.getCoordinateList(firstLane);
            
            if (geometry == null) {
                System.out.println("⚠️  Lane " + firstLane + " has no geometry - SKIPPED");
                return;
            }
            
            // Vị trí traffic light = điểm cuối của lane (trước junction)
            List<de.tudresden.sumo.objects.SumoPosition2D> tlPoints = (List<de.tudresden.sumo.objects.SumoPosition2D>) geometry;
            de.tudresden.sumo.objects.SumoPosition2D lastPos = tlPoints != null && tlPoints.size() > 0 ? tlPoints.get(tlPoints.size() - 1) : new de.tudresden.sumo.objects.SumoPosition2D(0, 0);
            double x = lastPos.x;
            double y = -lastPos.y; // Đảo Y
            
            // Tạo Group chứa cột đèn và đèn tín hiệu đẹp hơn
            Group tlGroup = new Group();
            
            // 1. Vẽ đế cột đèn (base)
            Rectangle base = new Rectangle(1.2, 0.4);
            base.setX(x - 0.6);
            base.setY(y - 0.2);
            base.setFill(Color.rgb(60, 60, 60));
            base.setStroke(Color.rgb(30, 30, 30));
            base.setStrokeWidth(0.1);
            base.setArcWidth(0.2);
            base.setArcHeight(0.2);
            
            // 2. Vẽ cột đèn (pole) - với gradient và shadow
            Rectangle pole = new Rectangle(0.5, 8);
            pole.setX(x - 0.25);
            pole.setY(y - 8);
            javafx.scene.paint.LinearGradient poleGradient = new javafx.scene.paint.LinearGradient(
                0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.rgb(80, 80, 80)),
                new javafx.scene.paint.Stop(0.5, Color.rgb(100, 100, 100)),
                new javafx.scene.paint.Stop(1, Color.rgb(70, 70, 70))
            );
            pole.setFill(poleGradient);
            pole.setStroke(Color.rgb(40, 40, 40));
            pole.setStrokeWidth(0.1);
            pole.setEffect(new javafx.scene.effect.DropShadow(3, 1, 1, Color.rgb(0, 0, 0, 0.5)));
            
            // 3. Vẽ hộp đèn (traffic light housing) - hình chữ nhật bo góc
            Rectangle housing = new Rectangle(1.8, 2.4);
            housing.setX(x - 0.9);
            housing.setY(y - 10.9);
            housing.setFill(Color.rgb(40, 40, 40));
            housing.setStroke(Color.rgb(20, 20, 20));
            housing.setStrokeWidth(0.15);
            housing.setArcWidth(0.4);
            housing.setArcHeight(0.4);
            housing.setEffect(new javafx.scene.effect.DropShadow(4, 1, 2, Color.rgb(0, 0, 0, 0.6)));
            
            // 4. Vẽ đèn tín hiệu (traffic light) - hình tròn với gradient và glow
            javafx.scene.shape.Circle lightCircle = new javafx.scene.shape.Circle(x, y - 9.7, 0.7);
            lightCircle.setStroke(Color.rgb(30, 30, 30));
            lightCircle.setStrokeWidth(0.15);
            lightCircle.setFill(Color.rgb(60, 60, 60)); // Màu tắt mặc định
            
            // Thêm glow effect
            javafx.scene.effect.Bloom bloom = new javafx.scene.effect.Bloom(0.3);
            javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
            glow.setColor(Color.rgb(100, 100, 100, 0.8));
            glow.setRadius(2);
            glow.setSpread(0.5);
            bloom.setInput(glow);
            lightCircle.setEffect(bloom);
            
            // Thêm vào group theo thứ tự: base -> pole -> housing -> light
            tlGroup.getChildren().addAll(base, pole, housing, lightCircle);
            
            // Lưu reference để update màu sau
            trafficLightShapes.put(tlID, lightCircle);
            
            // Add vào layer
            trafficLightLayer.getChildren().add(tlGroup);
            
            System.out.println("   ✅ Successfully rendered traffic light " + tlID + " at (" + String.format("%.2f", x) + ", " + String.format("%.2f", y) + ")");
            
        } catch (Exception e) {
            System.err.println("❌ Error rendering traffic light " + tlID + ": " + e.getMessage());
        }
    }
    
    /**
     * Update vehicles và traffic lights - gọi liên tục mỗi simulation step
     */
    public void updateVehicles() {
        if (vehicleManager == null) return;
        
        try {
            List<String> vehicleIDs = vehicleManager.getIDList();
            
            // Xóa các xe không còn tồn tại
            vehicleLayer.getChildren().clear();
            
            // Update hoặc tạo mới vehicle shapes
            for (String vehicleID : vehicleIDs) {
                updateVehicle(vehicleID);
            }
            
            // Update traffic lights colors
            updateTrafficLights();
            
        } catch (Exception e) {
            System.err.println("Error updating vehicles: " + e.getMessage());
        }
    }
    
    /**
     * Update traffic lights state (màu)
     */
    private void updateTrafficLights() {
        if (trafficLightManager == null) return;
        
        try {
            for (String tlID : trafficLightShapes.keySet()) {
                String state = trafficLightManager.getState(tlID);
                // State string từ SUMO:
                // Mỗi ký tự tương ứng với 1 controlled link/lane:
                // 'r'/'R' = red (đỏ)
                // 'y'/'Y' = yellow (vàng) 
                // 'g' = green (xanh - yield)
                // 'G' = green (xanh - priority, không cần nhường đường)
                
                if (state != null && !state.isEmpty()) {
                    javafx.scene.shape.Circle tlShape = trafficLightShapes.get(tlID);
                    
                    if (tlShape != null) {
                        // Hiển thị màu dominant (ưu tiên đỏ > vàng > xanh)
                        // Vì mỗi junction có nhiều lanes với states khác nhau,
                        // ta hiển thị màu có priority cao nhất để người dùng biết
                        // có ít nhất 1 hướng đang đỏ/vàng
                        char dominantState = getDominantState(state);
                        
                        Color color;
                        Color glowColor;
                        
                        switch (dominantState) {
                            case 'r', 'R' -> {
                                color = Color.rgb(220, 20, 20);
                                glowColor = Color.rgb(255, 0, 0, 0.9);
                            }
                            case 'y', 'Y' -> {
                                color = Color.rgb(255, 200, 0);
                                glowColor = Color.rgb(255, 220, 0, 0.9);
                            }
                            case 'g', 'G' -> {
                                color = Color.rgb(0, 200, 50);
                                glowColor = Color.rgb(0, 255, 100, 0.9);
                            }
                            default -> {
                                // Fallback - không nên xảy ra
                                color = Color.rgb(60, 60, 60);
                                glowColor = Color.rgb(100, 100, 100, 0.5);
                            }
                        }
                        
                        // Set màu với gradient
                        javafx.scene.paint.RadialGradient lightGradient = new javafx.scene.paint.RadialGradient(
                            0, 0, 0.5, 0.5, 0.5, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                            new javafx.scene.paint.Stop(0, color.brighter()),
                            new javafx.scene.paint.Stop(0.7, color),
                            new javafx.scene.paint.Stop(1, color.darker())
                        );
                        tlShape.setFill(lightGradient);
                        
                        // Update glow effect
                        javafx.scene.effect.Bloom bloom = new javafx.scene.effect.Bloom(0.6);
                        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
                        glow.setColor(glowColor);
                        glow.setRadius(4);
                        glow.setSpread(0.7);
                        bloom.setInput(glow);
                        tlShape.setEffect(bloom);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating traffic lights: " + e.getMessage());
        }
    }
    
    /**
     * Xác định state dominant từ state string
     * Ưu tiên: red > yellow > green
     * 
     * Lý do: Mỗi traffic light junction có nhiều controlled lanes,
     * mỗi lane có state riêng (ví dụ: "grrrggggrrrr" = 12 lanes).
     * Vì chỉ hiển thị 1 đèn cho cả junction, ta ưu tiên màu đỏ/vàng
     * để người dùng biết có ít nhất 1 hướng đang stop.
     */
    private char getDominantState(String state) {
        // Đếm số lượng mỗi loại state
        int redCount = 0;
        int yellowCount = 0;
        int greenCount = 0;
        
        for (char c : state.toCharArray()) {
            switch (c) {
                case 'r', 'R' -> redCount++;
                case 'y', 'Y' -> yellowCount++;
                case 'g', 'G' -> greenCount++;
            }
        }
        
        // Ưu tiên: nếu có ít nhất 1 đỏ/vàng thì hiển thị đỏ/vàng
        if (redCount > 0) {
            return 'r';
        }
        if (yellowCount > 0) {
            return 'y';
        }
        if (greenCount > 0) {
            return 'g';
        }
        
        // Fallback: lấy ký tự đầu tiên
        return state.charAt(0);
    }
    
    /**
     * Update một vehicle
     */
    private void updateVehicle(String vehicleID) {
        try {
            // Lấy dữ liệu vehicle
            SumoPosition2D position = vehicleManager.getPosition(vehicleID);
            double angle = vehicleManager.getAngle(vehicleID);
            SumoColor sumoColor = vehicleManager.getColor(vehicleID);
            
            // Kích thước xe mặc định (5m x 1.8m)
            double length = 5.0;
            double width = 1.8;
            
            // Tạo hoặc lấy polygon từ cache với hình dáng xe 3D đẹp hơn
            Polygon vehicleShape = vehicleShapes.get(vehicleID);
            if (vehicleShape == null) {
                vehicleShape = new Polygon();
                vehicleShapes.put(vehicleID, vehicleShape);
                // Tạo hình xe với đầu nhọn (aerodynamic) - tọa độ local, center tại origin
                vehicleShape.getPoints().addAll(
                    -length/2, -width/2,          // Rear-left
                    -length/2, width/2,           // Rear-right
                    length/2 - 0.8, width/2,      // Front-right
                    length/2, 0.0,                // Front tip (nose)
                    length/2 - 0.8, -width/2      // Front-left
                );
            }
            
            // Set màu xe với gradient để tạo hiệu ứng 3D
            Color baseColor = convertSumoColor(sumoColor);
            javafx.scene.paint.LinearGradient carGradient = new javafx.scene.paint.LinearGradient(
                0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, baseColor.brighter()),
                new javafx.scene.paint.Stop(0.5, baseColor),
                new javafx.scene.paint.Stop(1, baseColor.darker())
            );
            vehicleShape.setFill(carGradient);
            vehicleShape.setStroke(baseColor.darker().darker());
            vehicleShape.setStrokeWidth(0.15);
            
            // Thêm shadow để xe nổi bật
            javafx.scene.effect.DropShadow carShadow = new javafx.scene.effect.DropShadow();
            carShadow.setRadius(1.5);
            carShadow.setOffsetX(0.3);
            carShadow.setOffsetY(0.3);
            carShadow.setColor(Color.rgb(0, 0, 0, 0.5));
            vehicleShape.setEffect(carShadow);
            
            // Transform: Translate trước (di chuyển tới vị trí), Rotate sau (quay tại chỗ)
            // SUMO angle: 0° = North (hướng lên), 90° = East (hướng phải), clockwise
            // JavaFX rotate: 0° = East (phải), 90° = South (xuống), clockwise
            // Cần convert: JavaFX angle = SUMO angle - 90°
            vehicleShape.getTransforms().clear();
            vehicleShape.getTransforms().addAll(
                new javafx.scene.transform.Translate(position.x, -position.y),  // Translate tới vị trí (đảo Y)
                new Rotate(angle - 90, 0, 0)           // Rotate quanh center (convert SUMO -> JavaFX angle)
            );
            
            // Thêm vào layer
            vehicleLayer.getChildren().add(vehicleShape);
            
        } catch (Exception e) {
            System.err.println("Error updating vehicle " + vehicleID + ": " + e.getMessage());
        }
    }
    
    /**
     * Convert SumoColor sang JavaFX Color
     */
    private Color convertSumoColor(SumoColor sumoColor) {
        if (sumoColor == null) return Color.WHITE;
        
        return Color.rgb(
            sumoColor.r & 0xFF,
            sumoColor.g & 0xFF,
            sumoColor.b & 0xFF
        );
    }
    
    /**
     * Center view để hiển thị toàn bộ map
     */
    private void centerView() {
        if (laneLayer.getChildren().isEmpty()) {
            System.err.println("⚠️  Cannot center view: no lanes rendered!");
            return;
        }
        
        System.out.println("📐 Centering view...");
        System.out.println("   Viewport size: " + viewport.getWidth() + " x " + viewport.getHeight());
        
        // Nếu viewport chưa có size, đợi đến khi có size
        if (viewport.getWidth() == 0 || viewport.getHeight() == 0) {
            System.out.println("⏳ Viewport not ready, waiting for layout...");
            viewport.widthProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.doubleValue() > 0 && viewport.getHeight() > 0) {
                    centerView();
                }
            });
            return;
        }
        
        // Reset transform
        viewTransform.setToIdentity();
        scale = 1.0;
        
        // Tính bounds của map
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        
        for (javafx.scene.Node node : laneLayer.getChildren()) {
            javafx.geometry.Bounds bounds = node.getBoundsInParent();
            minX = Math.min(minX, bounds.getMinX());
            maxX = Math.max(maxX, bounds.getMaxX());
            minY = Math.min(minY, bounds.getMinY());
            maxY = Math.max(maxY, bounds.getMaxY());
        }
        
        double mapWidth = maxX - minX;
        double mapHeight = maxY - minY;
        double mapCenterX = (minX + maxX) / 2;
        double mapCenterY = (minY + maxY) / 2;
        
        System.out.println("   Map bounds: [" + minX + ", " + minY + "] to [" + maxX + ", " + maxY + "]");
        System.out.println("   Map size: " + mapWidth + " x " + mapHeight);
        System.out.println("   Map center: [" + mapCenterX + ", " + mapCenterY + "]");
        
        // Tính scale để fit map vào viewport với zoom to hơn
        double scaleX = viewport.getWidth() / mapWidth;
        double scaleY = viewport.getHeight() / mapHeight;
        double fitScale = Math.min(scaleX, scaleY) * 2.5; // 2.5 để map to hơn (thay vì 0.9)
        
        // Giới hạn scale trong khoảng MIN_SCALE -> MAX_SCALE
        fitScale = clamp(fitScale, MIN_SCALE, MAX_SCALE);
        
        System.out.println("   Fit scale: " + fitScale + " (scaleX: " + scaleX + ", scaleY: " + scaleY + ")");
        
        // Apply transform: scale và center
        viewTransform.appendScale(fitScale, fitScale);
        viewTransform.appendTranslation(
            viewport.getWidth() / 2 / fitScale - mapCenterX,
            viewport.getHeight() / 2 / fitScale - mapCenterY
        );
        
        scale = fitScale;
        System.out.println("✅ View centered successfully!");
    }
    
    /**
     * Public method để recenter view - gọi từ bên ngoài sau khi window hiển thị
     */
    public void recenterView() {
        // Đợi một chút để viewport có size
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(100); // Đợi 100ms
                javafx.application.Platform.runLater(this::centerView);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    /**
     * Clamp giá trị trong khoảng min-max
     */
    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
    
    /**
     * Phóng to hình ảnh (zoom in) - zoom tại center của viewport
     */
    private void zoomIn() {
        double factor = ZOOM_STEP;
        double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
        factor = newScale / scale;
        
        // Zoom tại center của viewport
        double centerX = viewport.getWidth() / 2;
        double centerY = viewport.getHeight() / 2;
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }
    
    /**
     * Thu nhỏ hình ảnh (zoom out) - zoom tại center của viewport
     */
    private void zoomOut() {
        double factor = 1.0 / ZOOM_STEP;
        double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
        factor = newScale / scale;
        
        // Zoom tại center của viewport
        double centerX = viewport.getWidth() / 2;
        double centerY = viewport.getHeight() / 2;
        Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
        viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
        scale = newScale;
    }
}
