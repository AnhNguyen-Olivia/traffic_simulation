// package real_time_traffic_simulation_with_java.gui;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import de.tudresden.sumo.objects.SumoColor;
// import javafx.geometry.Insets;
// import javafx.geometry.Point2D;
// import javafx.geometry.Pos;
// import javafx.scene.Cursor;
// import javafx.scene.Group;
// import javafx.scene.control.Button;
// import javafx.scene.input.MouseButton;
// import javafx.scene.input.ScrollEvent;
// import javafx.scene.layout.Pane;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Line;
// import javafx.scene.shape.Polygon;
// import javafx.scene.shape.Rectangle;
// import javafx.scene.transform.Affine;
// import javafx.scene.transform.Rotate;
// import real_time_traffic_simulation_with_java.wrapper.LaneManager;
// import real_time_traffic_simulation_with_java.wrapper.VehicleManager;

// /**
//  * MapPanel - Panel hiển thị bản đồ ở giữa với zoom controls
//  * Sử dụng Affine Transform cho pan/zoom mượt mà và chính xác
//  */
// public class MapPanel extends StackPane {
    
//     // Zoom settings
//     private static final double MIN_SCALE = 0.5;
//     private static final double MAX_SCALE = 3.0;
//     private static final double ZOOM_STEP = 1.10;
    
//     private double scale = 1.0;
//     private double anchorX, anchorY;
//     private double anchorTx, anchorTy;
    
//     // Components
//     private final Pane viewport;
//     private final Group world;
//     private final Group laneLayer;      // Layer chứa các lane (đường)
//     private final Group vehicleLayer;   // Layer chứa các xe
//     private final Affine viewTransform;
    
//     // SUMO Managers - sẽ được set từ bên ngoài
//     private LaneManager laneManager;
//     private VehicleManager vehicleManager;
    
//     // Cache để lưu trữ shapes
//     private final Map<String, Group> laneShapes = new HashMap<>();
//     private final Map<String, Polygon> vehicleShapes = new HashMap<>();
    
//     /**
//      * Constructor - Khởi tạo MapPanel với Affine Transform cho pan/zoom tối ưu
//      */
//     public MapPanel() {
//         // Thiết lập style cho MapPanel
//         setStyle("-fx-background-color: #F0F0F0; " +
//                  "-fx-border-color: #bdbdbd; " +
//                  "-fx-border-width: 0 2 0 2;");
        
//         // Tạo viewport (Pane chứa world) - sẽ clip content
//         viewport = new Pane();
//         viewport.setStyle("-fx-background-color: #F0F0F0;");
        
//         // Tạo world (Group chứa map layers) - sẽ được transform
//         world = new Group();
        
//         // Tạo các layer
//         laneLayer = new Group();
//         vehicleLayer = new Group();
        
//         // Thêm layers vào world (thứ tự quan trọng: lanes trước, vehicles sau để xe nằm trên đường)
//         world.getChildren().addAll(laneLayer, vehicleLayer);
        
//         // Tạo Affine transform cho world
//         viewTransform = new Affine();
//         world.getTransforms().setAll(viewTransform);
        
//         // Thêm world vào viewport
//         viewport.getChildren().add(world);
        
//         // Bind clipping cho viewport để không tràn ra ngoài
//         Rectangle clip = new Rectangle();
//         clip.widthProperty().bind(viewport.widthProperty());
//         clip.heightProperty().bind(viewport.heightProperty());
//         viewport.setClip(clip);
        
//         // Thêm viewport vào MapPanel
//         getChildren().add(viewport);
        
//         // Setup pan/zoom handlers
//         setupPanZoom();
        
//         // Tạo nút zoom + - macOS style
//         Button zoomInBtn = new Button("+");
//         zoomInBtn.setMinSize(36, 36);
//         zoomInBtn.setMaxSize(36, 36);
//         zoomInBtn.setPrefSize(36, 36);
//         zoomInBtn.setStyle("-fx-background-color: #FFFFFF; " +
//                           "-fx-border-color: #D1D1D6; " +
//                           "-fx-border-width: 1; " +
//                           "-fx-border-radius: 6; " +
//                           "-fx-background-radius: 6; " +
//                           "-fx-font-size: 18px; " +
//                           "-fx-font-weight: 600; " +
//                           "-fx-text-fill: #007AFF; " +
//                           "-fx-cursor: hand; " +
//                           "-fx-padding: 0; " +
//                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");
//         zoomInBtn.setOnMouseEntered(e -> 
//             zoomInBtn.setStyle("-fx-background-color: #F5F5F7; " +
//                           "-fx-border-color: #007AFF; " +
//                           "-fx-border-width: 1; " +
//                           "-fx-border-radius: 6; " +
//                           "-fx-background-radius: 6; " +
//                           "-fx-font-size: 18px; " +
//                           "-fx-font-weight: 600; " +
//                           "-fx-text-fill: #007AFF; " +
//                           "-fx-cursor: hand; " +
//                           "-fx-padding: 0; " +
//                           "-fx-effect: dropshadow(gaussian, rgba(0,122,255,0.3), 6, 0, 0, 2);")
//         );
//         zoomInBtn.setOnMouseExited(e -> 
//             zoomInBtn.setStyle("-fx-background-color: #FFFFFF; " +
//                           "-fx-border-color: #D1D1D6; " +
//                           "-fx-border-width: 1; " +
//                           "-fx-border-radius: 6; " +
//                           "-fx-background-radius: 6; " +
//                           "-fx-font-size: 18px; " +
//                           "-fx-font-weight: 600; " +
//                           "-fx-text-fill: #007AFF; " +
//                           "-fx-cursor: hand; " +
//                           "-fx-padding: 0; " +
//                           "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);")
//         );
//         zoomInBtn.setOnAction(e -> zoomIn());
        
//         // Tạo nút zoom - - macOS style
//         Button zoomOutBtn = new Button("−");
//         zoomOutBtn.setMinSize(36, 36);
//         zoomOutBtn.setMaxSize(36, 36);
//         zoomOutBtn.setPrefSize(36, 36);
//         zoomOutBtn.setStyle("-fx-background-color: #FFFFFF; " +
//                            "-fx-border-color: #D1D1D6; " +
//                            "-fx-border-width: 1; " +
//                            "-fx-border-radius: 6; " +
//                            "-fx-background-radius: 6; " +
//                            "-fx-font-size: 20px; " +
//                            "-fx-font-weight: 600; " +
//                            "-fx-text-fill: #007AFF; " +
//                            "-fx-cursor: hand; " +
//                            "-fx-padding: 0; " +
//                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");
//         zoomOutBtn.setOnMouseEntered(e -> 
//             zoomOutBtn.setStyle("-fx-background-color: #F5F5F7; " +
//                            "-fx-border-color: #007AFF; " +
//                            "-fx-border-width: 1; " +
//                            "-fx-border-radius: 6; " +
//                            "-fx-background-radius: 6; " +
//                            "-fx-font-size: 20px; " +
//                            "-fx-font-weight: 600; " +
//                            "-fx-text-fill: #007AFF; " +
//                            "-fx-cursor: hand; " +
//                            "-fx-padding: 0; " +
//                            "-fx-effect: dropshadow(gaussian, rgba(0,122,255,0.3), 6, 0, 0, 2);")
//         );
//         zoomOutBtn.setOnMouseExited(e -> 
//             zoomOutBtn.setStyle("-fx-background-color: #FFFFFF; " +
//                            "-fx-border-color: #D1D1D6; " +
//                            "-fx-border-width: 1; " +
//                            "-fx-border-radius: 6; " +
//                            "-fx-background-radius: 6; " +
//                            "-fx-font-size: 20px; " +
//                            "-fx-font-weight: 600; " +
//                            "-fx-text-fill: #007AFF; " +
//                            "-fx-cursor: hand; " +
//                            "-fx-padding: 0; " +
//                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);")
//         );
//         zoomOutBtn.setOnAction(e -> zoomOut());
        
//         // Tạo VBox chứa 2 nút zoom
//         VBox zoomControls = new VBox(2); // Khoảng cách 2px giữa các nút
//         zoomControls.getChildren().addAll(zoomInBtn, zoomOutBtn);
//         zoomControls.setStyle("-fx-background-color: transparent;");
//         zoomControls.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE); // Giữ kích thước nhỏ gọn
        
//         // Thêm zoom controls vào panel
//         getChildren().add(zoomControls);
        
//         // Đặt zoom controls ở góc dưới bên phải - Responsive position
//         StackPane.setAlignment(zoomControls, Pos.BOTTOM_RIGHT); // Chỉ set cho zoomControls thôi
        
//         // Responsive margin - adjust based on viewport size
//         widthProperty().addListener((obs, old, newVal) -> {
//             double margin = newVal.doubleValue() > 800 ? 16 : 10;
//             StackPane.setMargin(zoomControls, new Insets(0, margin, margin, 0));
//         });
//     }
    
//     /**
//      * Setup pan and zoom handlers với Affine Transform
//      */
//     private void setupPanZoom() {
//         // Pan với chuột
//         viewport.setOnMousePressed(e -> {
//             if (e.getButton() != MouseButton.PRIMARY) return;
//             anchorX = e.getX();
//             anchorY = e.getY();
//             anchorTx = viewTransform.getTx();
//             anchorTy = viewTransform.getTy();
//             viewport.setCursor(Cursor.CLOSED_HAND);
//         });

//         viewport.setOnMouseDragged(e -> {
//             if (!e.isPrimaryButtonDown()) return;
//             double dx = e.getX() - anchorX;
//             double dy = e.getY() - anchorY;
//             viewTransform.setTx(anchorTx + dx);
//             viewTransform.setTy(anchorTy + dy);
//         });

//         viewport.setOnMouseReleased(e -> viewport.setCursor(Cursor.DEFAULT));

//         // Zoom tại vị trí chuột với mouse wheel/touchpad
//         viewport.addEventFilter(ScrollEvent.SCROLL, e -> {
//             if (e.getDeltaY() == 0) return;

//             double factor = (e.getDeltaY() > 0) ? ZOOM_STEP : 1.0 / ZOOM_STEP;
//             double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
//             factor = newScale / scale;

//             // Pivot trong tọa độ LOCAL của world
//             Point2D pivot = world.sceneToLocal(e.getSceneX(), e.getSceneY());

//             // Affine.appendScale với pivot local sẽ giữ pivot đứng yên trên màn hình
//             viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());

//             scale = newScale;
//             e.consume();
//         });
//     }
    
//     /**
//      * Set SUMO managers để lấy dữ liệu
//      */
//     public void setManagers(LaneManager laneManager, VehicleManager vehicleManager) {
//         this.laneManager = laneManager;
//         this.vehicleManager = vehicleManager;
//     }
    
//     /**
//      * Render toàn bộ map (lanes) - chỉ gọi 1 lần khi khởi tạo
//      */
//     public void renderMap() {
//         if (laneManager == null) return;
        
//         try {
//             List<String> laneIDs = laneManager.getIDList();
            
//             for (String laneID : laneIDs) {
//                 renderLane(laneID);
//             }
            
//             // Center view sau khi render xong map
//             centerView();
//         } catch (Exception e) {
//             System.err.println("Error rendering map: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }
    
//     /**
//      * Render một lane (đường)
//      */
//     private void renderLane(String laneID) {
//         try {
//             // Lấy dữ liệu lane
//             List<double[]> coordinates = laneManager.getCoordinateList(laneID);
//             double width = laneManager.getWidth(laneID);
            
//             if (coordinates == null || coordinates.isEmpty()) return;
            
//             Group laneGroup = new Group();
            
//             // Vẽ từng đoạn của lane
//             for (int i = 0; i < coordinates.size() - 1; i++) {
//                 double[] point1 = coordinates.get(i);
//                 double[] point2 = coordinates.get(i + 1);
                
//                 // Tạo line cho lane
//                 Line laneLine = new Line(point1[0], -point1[1], point2[0], -point2[1]); // Đảo ngược Y vì SUMO dùng coordinate khác
//                 laneLine.setStroke(Color.GRAY);
//                 laneLine.setStrokeWidth(width);
                
//                 laneGroup.getChildren().add(laneLine);
//             }
            
//             // Lưu vào cache và thêm vào layer
//             laneShapes.put(laneID, laneGroup);
//             laneLayer.getChildren().add(laneGroup);
            
//         } catch (Exception e) {
//             System.err.println("Error rendering lane " + laneID + ": " + e.getMessage());
//         }
//     }
    
//     /**
//      * Update vehicles - gọi liên tục mỗi simulation step
//      */
//     public void updateVehicles() {
//         if (vehicleManager == null) return;
        
//         try {
//             List<String> vehicleIDs = vehicleManager.getIDList();
            
//             // Xóa các xe không còn tồn tại

//             vehicleLayer.getChildren().clear();
            
//             // Update hoặc tạo mới vehicle shapes
//             for (String vehicleID : vehicleIDs) {
//                 updateVehicle(vehicleID);
//             }
            
//         } catch (Exception e) {
//             System.err.println("Error updating vehicles: " + e.getMessage());
//         }
//     }
    
//     /**
//      * Update một vehicle
//      */
//     private void updateVehicle(String vehicleID) {
//         try {
//             // Lấy dữ liệu vehicle
//             double[] position = vehicleManager.getPosition(vehicleID);
//             double angle = vehicleManager.getAngle(vehicleID);
//             SumoColor sumoColor = vehicleManager.getColor(vehicleID);
            
//             // Kích thước xe mặc định (5m x 1.8m)
//             double length = 5.0;
//             double width = 1.8;
            
//             // Tạo hoặc lấy polygon từ cache
//             Polygon vehicleShape = vehicleShapes.get(vehicleID);
//             if (vehicleShape == null) {
//                 vehicleShape = new Polygon();
//                 vehicleShapes.put(vehicleID, vehicleShape);
//                 // Tạo hình chữ nhật cho xe (tọa độ local, center tại origin)
//                 vehicleShape.getPoints().addAll(
//                     -length/2, -width/2,  // Top-left
//                      length/2, -width/2,  // Top-right
//                      length/2,  width/2,  // Bottom-right
//                     -length/2,  width/2   // Bottom-left
//                 );
//             }
            
//             // Set màu xe
//             Color color = convertSumoColor(sumoColor);
//             vehicleShape.setFill(color);
//             vehicleShape.setStroke(Color.BLACK);
//             vehicleShape.setStrokeWidth(0.2);
            
//             // Transform: Rotate và Translate
//             vehicleShape.getTransforms().clear();
//             vehicleShape.getTransforms().addAll(
//                 new Rotate(angle, 0, 0),           // Rotate quanh center
//                 new javafx.scene.transform.Translate(position[0], -position[1])  // Translate tới vị trí (đảo Y)
//             );
            
//             // Thêm vào layer
//             vehicleLayer.getChildren().add(vehicleShape);
            
//         } catch (Exception e) {
//             System.err.println("Error updating vehicle " + vehicleID + ": " + e.getMessage());
//         }
//     }
    
//     /**
//      * Convert SumoColor sang JavaFX Color
//      */
//     private Color convertSumoColor(SumoColor sumoColor) {
//         if (sumoColor == null) return Color.WHITE;
        
//         return Color.rgb(
//             sumoColor.r & 0xFF,
//             sumoColor.g & 0xFF,
//             sumoColor.b & 0xFF
//         );
//     }
    
//     /**
//      * Center view để hiển thị toàn bộ map
//      */
//     private void centerView() {
//         if (laneLayer.getChildren().isEmpty()) return;
        
//         // Reset transform
//         viewTransform.setToIdentity();
//         scale = 1.0;
        
//         // Tính bounds của map
//         double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
//         double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        
//         for (javafx.scene.Node node : laneLayer.getChildren()) {
//             javafx.geometry.Bounds bounds = node.getBoundsInParent();
//             minX = Math.min(minX, bounds.getMinX());
//             maxX = Math.max(maxX, bounds.getMaxX());
//             minY = Math.min(minY, bounds.getMinY());
//             maxY = Math.max(maxY, bounds.getMaxY());
//         }
        
//         double mapWidth = maxX - minX;
//         double mapHeight = maxY - minY;
//         double mapCenterX = (minX + maxX) / 2;
//         double mapCenterY = (minY + maxY) / 2;
        
//         // Tính scale để fit map vào viewport
//         double scaleX = viewport.getWidth() / mapWidth;
//         double scaleY = viewport.getHeight() / mapHeight;
//         double fitScale = Math.min(scaleX, scaleY) * 0.9; // 0.9 để có margin
        
//         // Apply transform: scale và center
//         viewTransform.appendScale(fitScale, fitScale);
//         viewTransform.appendTranslation(
//             viewport.getWidth() / 2 / fitScale - mapCenterX,
//             viewport.getHeight() / 2 / fitScale - mapCenterY
//         );
        
//         scale = fitScale;
//     }
    
//     /**
//      * Clamp giá trị trong khoảng min-max
//      */
//     private static double clamp(double v, double min, double max) {
//         return Math.max(min, Math.min(max, v));
//     }
    
//     /**
//      * Phóng to hình ảnh (zoom in) - zoom tại center của viewport
//      */
//     private void zoomIn() {
//         double factor = ZOOM_STEP;
//         double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
//         factor = newScale / scale;
        
//         // Zoom tại center của viewport
//         double centerX = viewport.getWidth() / 2;
//         double centerY = viewport.getHeight() / 2;
//         Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
//         viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
//         scale = newScale;
//     }
    
//     /**
//      * Thu nhỏ hình ảnh (zoom out) - zoom tại center của viewport
//      */
//     private void zoomOut() {
//         double factor = 1.0 / ZOOM_STEP;
//         double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
//         factor = newScale / scale;
        
//         // Zoom tại center của viewport
//         double centerX = viewport.getWidth() / 2;
//         double centerY = viewport.getHeight() / 2;
//         Point2D pivot = world.sceneToLocal(viewport.localToScene(centerX, centerY));
        
//         viewTransform.appendScale(factor, factor, pivot.getX(), pivot.getY());
//         scale = newScale;
//     }
// }
