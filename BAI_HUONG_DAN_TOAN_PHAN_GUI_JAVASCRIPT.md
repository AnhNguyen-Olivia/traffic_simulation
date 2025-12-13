# 🚦 Hướng Dẫn Toàn Phần: Xây Dựng GUI Mô Phỏng Giao Thông

**Mục tiêu:** Hướng dẫn chi tiết từ A-Z cách xây dựng ứng dụng GUI mô phỏng giao thông sử dụng JavaFX + SUMO, dành cho người hoàn toàn mới bắt đầu.

---

## Phần 1: Kiến Thức Nền Tảng (Dành cho Người Mới)

### 1.1 GUI là gì?
- **GUI** = **Graphical User Interface** (Giao diện người dùng đồ họa)
- Là phần giao diện mà người dùng nhìn thấy và tương tác (nút, menu, bảng, đồ thị, v.v.)
- Ví dụ: 
  - Nút "+" để phóng to bản đồ
  - Bảng hiển thị thông tin xe
  - Đèn giao thông hiển thị trên bản đồ

### 1.2 JavaFX là gì?
- **JavaFX** = Thư viện lập trình giao diện của Java
- Cho phép tạo giao diện đẹp, chuyên nghiệp bằng Java
- Hỗ trợ: hình ảnh, hiệu ứng, animation, v.v.

### 1.3 SUMO là gì?
- **SUMO** = **Simulation of Urban Mobility** (Mô phỏng Giao thông Đô Thị)
- Phần mềm mô phỏng giao thông chuyên dụng
- Quản lý: đường, xe, đèn giao thông, luật giao thông
- Chúng ta sẽ **kết nối Java với SUMO** để lấy dữ liệu mô phỏng

### 1.4 Kiến trúc 3 lớp của ứng dụng
```
┌──────────────────────────────────────┐
│   GUI (JavaFX) - Lớp Trình Bày      │  ← Giao diện người dùng
│  MapPanel | Dashboard | ControlPanel │
└──────────────────────────────────────┘
           ↓ (kết nối qua interface)
┌──────────────────────────────────────┐
│  CORES (Dữ Liệu) - Lớp Dữ Liệu      │  ← Cache dữ liệu
│  LaneData | VehicleData | TrafficLight│
└──────────────────────────────────────┘
           ↓ (gọi phương thức)
┌──────────────────────────────────────┐
│  WRAPPER (API) - Lớp Giao Tiếp       │  ← Kết nối SUMO
│  LaneManager | VehicleManager        │
└──────────────────────────────────────┘
           ↓ (TraCI protocol)
┌──────────────────────────────────────┐
│  SUMO - Mô Phỏng Giao Thông          │  ← Phần mềm SUMO
└──────────────────────────────────────┘
```

**Tại sao chia 3 lớp?**
- **GUI** chỉ lo hiển thị (không cần biết SUMO)
- **CORES** lưu trữ dữ liệu (không cần biết hiển thị)
- **WRAPPER** kết nối SUMO (chuyên riêng cho giao tiếp)
- ✅ Dễ bảo trì, dễ sửa lỗi, dễ mở rộng

---

## Phần 2: Chuẩn Bị Môi Trường

### 2.1 Cài đặt cần thiết
```bash
1. Java Development Kit (JDK) 25+
   - Tải từ: https://www.oracle.com/java/technologies/
   - Kiểm tra: java -version

2. Apache Maven 3.9+
   - Tải từ: https://maven.apache.org/
   - Kiểm tra: mvn -version

3. SUMO 1.25.0+
   - Tải từ: https://sumo.dlr.de/
   - Kiểm tra: sumo --version

4. IDE: IntelliJ IDEA hoặc VS Code
```

### 2.2 Cấu trúc thư mục dự án
```
traffic_simulation/
├── pom.xml                          ← Cấu hình Maven
├── src/
│   ├── main/
│   │   └── java/
│   │       └── real_time_traffic_simulation_with_java/
│   │           ├── App.java         ← Điểm bắt đầu
│   │           ├── alias/           ← Các class nhỏ
│   │           │   ├── Color.java
│   │           │   └── Path.java
│   │           ├── cores/           ← Lớp dữ liệu (CORES)
│   │           │   ├── LaneData.java
│   │           │   ├── VehicleData.java
│   │           │   └── ...
│   │           ├── gui/             ← Giao diện (GUI)
│   │           │   ├── MainWindow.java
│   │           │   ├── MapPanel.java
│   │           │   ├── Dashboard.java
│   │           │   └── ControlPanel.java
│   │           └── wrapper/         ← API kết nối SUMO (WRAPPER)
│   │               ├── LaneManager.java
│   │               ├── VehicleManager.java
│   │               └── ...
│   └── test/
│       └── java/
│           └── ...test files
├── lib/
│   └── TraaS.jar                    ← Thư viện kết nối SUMO
└── target/                          ← Thư mục biên dịch (tự động)
```

---

## Phần 3: Hiểu Về Các Thành Phần GUI

### 3.1 MainWindow - Cửa sổ chính
```java
public class MainWindow {
    public static void start(Stage stage) {
        // 1. Tạo BorderPane - bố cục cơ bản
        BorderPane root = new BorderPane();
        
        // 2. Tạo 3 panel chính
        MapPanel mapPanel = new MapPanel();          // Bản đồ giữa
        ControlPanel controlPanel = new ControlPanel();  // Điều khiển trên
        Dashboard dashboard = new Dashboard();       // Thông tin dưới
        
        // 3. Sắp xếp các panel
        root.setTop(controlPanel);      // Đặt ở trên
        root.setCenter(mapPanel);       // Đặt giữa
        root.setBottom(dashboard);      // Đặt dưới
        
        // 4. Tạo scene và hiển thị
        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.show();
    }
}
```

**Giải thích:**
- `BorderPane`: Bố cục với 5 vị trí (Top, Center, Bottom, Left, Right)
- `Scene`: Canvas chứa các thành phần
- `Stage`: Cửa sổ ứng dụng

---

### 3.2 MapPanel - Bản Đồ Chính

#### A. Cấu trúc cơ bản
```java
public class MapPanel extends StackPane {
    // Các layer (lớp)
    private Group laneLayer;           // Lớp đường
    private Group trafficLightLayer;   // Lớp đèn
    private Group vehicleLayer;        // Lớp xe
    
    // Transform cho pan/zoom
    private Affine viewTransform;      // Biến đổi view
    private double scale = 1.0;        // Mức zoom hiện tại
    
    public MapPanel() {
        // 1. Tạo viewport (vùng hiển thị)
        Pane viewport = new Pane();
        
        // 2. Tạo world (thế giới ảo chứa tất cả)
        Group world = new Group();
        
        // 3. Thêm các layer vào world
        world.getChildren().addAll(laneLayer, trafficLightLayer, vehicleLayer);
        
        // 4. Thêm world vào viewport
        viewport.getChildren().add(world);
        
        // 5. Thêm viewport vào MapPanel
        getChildren().add(viewport);
        
        // 6. Setup pan/zoom
        setupPanZoom();
    }
}
```

**Tại sao chia thành các layer?**
- Dễ quản lý: mỗi layer có trách nhiệm riêng
- Dễ update: cập nhật từng layer mà không ảnh hưởng cái khác
- Dễ hiểu: layer đèn hiển thị trên layer đường

#### B. Vẽ Đường (Lane)
```java
private void renderLane(String laneID) {
    // 1. Lấy danh sách tọa độ từ SUMO
    List<double[]> coordinates = laneManager.getCoordinateList(laneID);
    
    // 2. Tạo Group chứa đường
    Group laneGroup = new Group();
    
    // 3. Vẽ từng đoạn đường
    for (int i = 0; i < coordinates.size() - 1; i++) {
        double[] p1 = coordinates.get(i);
        double[] p2 = coordinates.get(i + 1);
        
        Line line = new Line(p1[0], -p1[1], p2[0], -p2[1]);
        line.setStroke(Color.GRAY);
        line.setStrokeWidth(2);
        laneGroup.getChildren().add(line);
    }
    
    // 4. Thêm vào layer
    laneLayer.getChildren().add(laneGroup);
}
```

**Giải thích:**
- Tọa độ từ SUMO: `[x, y]`
- Chúng ta lấy hai điểm liên tiếp: `p1` → `p2`
- Vẽ đường thẳng nối 2 điểm
- Lặp lại cho tất cả các đoạn

#### C. Vẽ Xe (Vehicle)
```java
private void updateVehicleWithData(String vehicleID) {
    // 1. Lấy dữ liệu xe từ cache
    VehicleData vData = vehicleDataCache.get(vehicleID);
    
    if (vData == null) {
        // Lần đầu tiên gặp xe này
        double[] position = vehicleManager.getPosition(vehicleID);
        double angle = vehicleManager.getAngle(vehicleID);
        vData = new VehicleData(vehicleID, position[0], position[1], angle, color);
        vehicleDataCache.put(vehicleID, vData);
    }
    
    // 2. Tạo hình xe (polygon)
    Polygon vehicleShape = createVehicleShape();
    
    // 3. Tô màu xe
    Color baseColor = Color.web(vData.color);
    vehicleShape.setFill(baseColor);
    
    // 4. Đặt vị trí và hướng xe
    double x = vData.top_left_corner.getX();
    double y = vData.top_left_corner.getY();
    vehicleShape.getTransforms().addAll(
        new Translate(x, y),
        new Rotate(vData.angle, 0, 0)  // Quay theo hướng
    );
    
    // 5. Thêm vào layer
    vehicleLayer.getChildren().add(vehicleShape);
}
```

**Giải thích:**
- Cache: lưu dữ liệu xe để không cần lấy nhiều lần
- Transform: dịch chuyển (Translate) + quay (Rotate) xe
- Update: mỗi step mô phỏng lại cập nhật vị trí/hướng xe

#### D. Vẽ Đèn Giao Thông (Traffic Light)
```java
private void renderTrafficLight(String tlID) {
    // 1. Lấy vị trí từ lane đầu tiên
    List<String> controlledLanes = trafficLightManager.getControlledLanes(tlID);
    String firstLane = controlledLanes.get(0);
    List<double[]> coordinates = laneManager.getCoordinateList(firstLane);
    double[] endPoint = coordinates.get(coordinates.size() - 1);
    
    double x = endPoint[0];
    double y = -endPoint[1];  // Đảo Y để khớp với màn hình
    
    // 2. Tạo group chứa đèn
    Group tlGroup = new Group();
    
    // 3. Vẽ đế đèn (base)
    Rectangle base = new Rectangle(1.2, 0.4);
    base.setFill(Color.rgb(60, 60, 60));
    tlGroup.getChildren().add(base);
    
    // 4. Vẽ cột đèn (pole)
    Rectangle pole = new Rectangle(0.5, 8);
    pole.setFill(Color.rgb(100, 100, 100));
    tlGroup.getChildren().add(pole);
    
    // 5. Vẽ đèn (bulb) - hình tròn
    Circle light = new Circle(x, y - 9.7, 0.7);
    light.setFill(Color.rgb(60, 60, 60));  // Mặc định tắt
    tlGroup.getChildren().add(light);
    
    // 6. Lưu tham chiếu để cập nhật màu sau
    trafficLightShapes.put(tlID, light);
    
    // 7. Thêm vào layer
    trafficLightLayer.getChildren().add(tlGroup);
}
```

**Giải thích:**
- Đèn giao thông gồm 3 phần: đế, cột, đèn
- Đèn được vẽ ở cuối của lane (điểm dừng)
- Lưu tham chiếu để update màu sau (đỏ/vàng/xanh)

#### E. Pan và Zoom
```java
private void setupPanZoom() {
    // 1. PAN: Kéo bản đồ bằng chuột
    viewport.setOnMousePressed(e -> {
        if (e.getButton() == MouseButton.PRIMARY) {
            anchorX = e.getSceneX();
            anchorY = e.getSceneY();
            anchorTx = viewTransform.getTx();
            anchorTy = viewTransform.getTy();
        }
    });
    
    viewport.setOnMouseDragged(e -> {
        if (e.getButton() == MouseButton.PRIMARY) {
            double dx = e.getSceneX() - anchorX;
            double dy = e.getSceneY() - anchorY;
            viewTransform.setTx(anchorTx + dx);
            viewTransform.setTy(anchorTy + dy);
        }
    });
    
    // 2. ZOOM: Cuộn chuột
    viewport.setOnScroll(e -> {
        if (e.getDeltaY() > 0) {
            zoomIn();   // Cuộn lên: phóng to
        } else {
            zoomOut();  // Cuộn xuống: thu nhỏ
        }
    });
}

private void zoomIn() {
    double factor = 1.15;  // Phóng to 15%
    double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
    viewTransform.appendScale(newScale, newScale);
    scale = newScale;
}

private void zoomOut() {
    double factor = 0.87;  // Thu nhỏ 13%
    double newScale = clamp(scale * factor, MIN_SCALE, MAX_SCALE);
    viewTransform.appendScale(newScale, newScale);
    scale = newScale;
}
```

**Giải thích:**
- **Pan** (Kéo): Di chuyển viewport (Translate)
- **Zoom** (Phóng to/thu nhỏ): Thay đổi scale
- **Affine Transform**: Biến đổi hình ảnh mượt mà và chính xác

---

### 3.3 Dashboard - Bảng Thông Tin
```java
public class Dashboard extends VBox {
    private Label vehicleCountLabel;
    private Label laneCountLabel;
    private Label simulationTimeLabel;
    
    public Dashboard() {
        // Tạo các nhãn
        vehicleCountLabel = new Label("Số xe: 0");
        laneCountLabel = new Label("Số đường: 0");
        simulationTimeLabel = new Label("Thời gian: 0s");
        
        // Thêm vào VBox (sắp xếp dọc)
        getChildren().addAll(vehicleCountLabel, laneCountLabel, simulationTimeLabel);
        
        // Định dạng
        setStyle("-fx-padding: 10; -fx-spacing: 10;");
    }
    
    public void updateInfo(int vehicleCount, int laneCount, double time) {
        vehicleCountLabel.setText("Số xe: " + vehicleCount);
        laneCountLabel.setText("Số đường: " + laneCount);
        simulationTimeLabel.setText(String.format("Thời gian: %.1f s", time));
    }
}
```

**Giải thích:**
- `VBox`: Sắp xếp các thành phần theo chiều dọc
- `Label`: Hiển thị text
- Update: mỗi frame cập nhật thông tin mới

### 3.4 ControlPanel - Nút Điều Khiển
```java
public class ControlPanel extends HBox {
    private Button startBtn;
    private Button pauseBtn;
    private Button resetBtn;
    
    public ControlPanel() {
        // Tạo nút
        startBtn = new Button("▶ Chạy");
        pauseBtn = new Button("⏸ Dừng");
        resetBtn = new Button("🔄 Tái đặt");
        
        // Thêm vào HBox (sắp xếp ngang)
        getChildren().addAll(startBtn, pauseBtn, resetBtn);
        
        // Định dạng
        setStyle("-fx-padding: 10; -fx-spacing: 10;");
    }
    
    public Button getStartButton() { return startBtn; }
    public Button getPauseButton() { return pauseBtn; }
    public Button getResetButton() { return resetBtn; }
}
```

**Giải thích:**
- `HBox`: Sắp xếp các thành phần theo chiều ngang
- `Button`: Nút bấm
- Getter: để lấy nút từ bên ngoài và gắn event listener

---

## Phần 4: Hiểu Về CORES (Lớp Dữ Liệu)

### 4.1 LaneData - Dữ Liệu Đường
```java
public class LaneData {
    public String laneID;                    // ID đường
    public List<SumoPosition2D> polyline;   // Danh sách tọa độ
    public int laneIndex;                   // Vị trí trong road
    public double length;                   // Độ dài
    public double width;                    // Rộng
    
    public LaneData(String laneID, List<SumoPosition2D> polyline) {
        this.laneID = laneID;
        this.polyline = polyline;
    }
}
```

**Tại sao cần LaneData?**
- Cache dữ liệu: không cần gọi SUMO nhiều lần
- Định type: biết chính xác dữ liệu là gì
- Tiện quản lý: tất cả dữ liệu đường ở một chỗ

### 4.2 VehicleData - Dữ Liệu Xe
```java
public class VehicleData {
    public String vehicleID;           // ID xe
    public Point2D top_left_corner;    // Vị trí
    public double angle;               // Hướng (0-360 độ)
    public String color;               // Màu
    
    public static double length = 5.0; // Độ dài xe
    public static double width = 2.0;  // Rộng xe
    
    public VehicleData(String vehicleID, double x, double y, 
                       double angle, String color) {
        this.vehicleID = vehicleID;
        this.top_left_corner = new Point2D(x, y);
        this.angle = angle;
        this.color = color;
    }
}
```

**Giải thích:**
- Dữ liệu tĩnh (`static`): tất cả xe có cùng kích thước
- Dữ liệu động: vị trí, hướng, màu thay đổi mỗi frame

---

## Phần 5: Hiểu Về WRAPPER (API Kết Nối)

### 5.1 LaneManager - Quản Lý Đường
```java
public class LaneManager {
    private TrafficLightManager tlManager;  // Kết nối với đèn
    
    // Cache
    private Map<String, LaneData> laneDataCache;
    
    public List<String> getIDList() {
        // Gọi SUMO để lấy danh sách ID đường
        return sumoConnection.getLaneIDList();
    }
    
    public List<double[]> getCoordinateList(String laneID) {
        // Gọi SUMO để lấy tọa độ
        return sumoConnection.getCoordinates(laneID);
    }
}
```

### 5.2 VehicleManager - Quản Lý Xe
```java
public class VehicleManager {
    
    public List<String> getIDList() {
        // Lấy danh sách ID xe đang chạy
        return sumoConnection.getVehicleIDList();
    }
    
    public double[] getPosition(String vehicleID) {
        // Lấy vị trí xe: [x, y]
        return sumoConnection.getPosition(vehicleID);
    }
    
    public double getAngle(String vehicleID) {
        // Lấy hướng xe: 0-360 độ
        return sumoConnection.getAngle(vehicleID);
    }
    
    public String getColor(String vehicleID) {
        // Lấy màu xe
        return sumoConnection.getColor(vehicleID);
    }
}
```

### 5.3 TrafficLightManager - Quản Lý Đèn
```java
public class TrafficLightManager {
    
    public List<String> getIDList() {
        // Lấy danh sách ID đèn giao thông
        return sumoConnection.getTrafficLightIDList();
    }
    
    public List<String> getControlledLanes(String tlID) {
        // Lấy danh sách lane mà đèn này điều khiển
        return sumoConnection.getControlledLanes(tlID);
    }
    
    public String getState(String tlID) {
        // Lấy state hiện tại: "rrrggggyyy" (r=red, g=green, y=yellow)
        // Mỗi ký tự tương ứng 1 controlled lane
        return sumoConnection.getState(tlID);
    }
    
    public void setPhase(String tlID, int phase) {
        // Thay đổi phase của đèn (0, 1, 2, ...)
        sumoConnection.setPhase(tlID, phase);
    }
}
```

---

## Phần 6: Quy Trình Chạy Ứng Dụng

### 6.1 Khởi động (Initialization)
```
1. App.main() được gọi
   ↓
2. MainWindow.start() tạo giao diện
   ├─ Tạo MapPanel (bản đồ)
   ├─ Tạo Dashboard (thông tin)
   └─ Tạo ControlPanel (nút điều khiển)
   ↓
3. SumoTraasConnection kết nối với SUMO
   ↓
4. LaneManager.loadMapData()
   ├─ Lấy danh sách đường từ SUMO
   ├─ Lấy tọa độ mỗi đường
   └─ Lưu vào cache (CORES)
   ↓
5. MapPanel.renderMap()
   ├─ Lặp qua tất cả đường
   └─ Vẽ mỗi đường lên bản đồ
   ↓
6. MapPanel.renderTrafficLights()
   ├─ Lặp qua tất cả đèn
   └─ Vẽ mỗi đèn lên bản đồ
   ↓
7. Bản đồ hiển thị!
```

### 6.2 Lặp Mô Phỏng (Simulation Loop)
```
Mỗi frame (~30-60 FPS):

1. Bấm nút "Chạy"
   ↓
2. SimulationEngine.start() bắt đầu lặp
   ↓
3. SUMO chạy 1 bước mô phỏng
   ↓
4. MapPanel.updateVehicles()
   ├─ Lấy danh sách xe hiện tại
   ├─ Lặp qua mỗi xe:
   │  ├─ Lấy vị trí từ SUMO
   │  ├─ Lấy hướng từ SUMO
   │  └─ Cập nhật vị trí trên bản đồ
   └─ Vẽ lại tất cả xe
   ↓
5. updateTrafficLights()
   ├─ Lặp qua mỗi đèn:
   │  ├─ Lấy state từ SUMO (red/green/yellow)
   │  └─ Thay đổi màu đèn trên bản đồ
   ↓
6. Dashboard.updateInfo()
   ├─ Cập nhật số xe
   ├─ Cập nhật thời gian
   └─ Hiển thị thông tin mới
   ↓
7. Người dùng nhìn thấy bản đồ cập nhật
   ↓
8. Quay lại bước 3 (mỗi frame)
```

### 6.3 Dừng (Shutdown)
```
1. Bấm nút "Dừng" hoặc đóng cửa sổ
   ↓
2. SimulationEngine.stop() dừng lặp
   ↓
3. SumoTraasConnection.close() đóng kết nối SUMO
   ↓
4. Ứng dụng thoát
```

---

## Phần 7: Ví Dụ Code Thực Tế

### 7.1 App.java - Điểm bắt đầu
```java
public class App extends Application {
    public static void main(String[] args) {
        launch();  // Khởi động JavaFX
    }
    
    @Override
    public void start(Stage stage) {
        try {
            // 1. Tạo cửa sổ
            stage.setTitle("🚦 Mô Phỏng Giao Thông");
            stage.setWidth(1200);
            stage.setHeight(800);
            
            // 2. Tạo giao diện
            MainWindow.start(stage);
            
            // 3. Hiển thị
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 7.2 MainWindow.java - Giao diện chính
```java
public class MainWindow {
    public static void start(Stage stage) throws Exception {
        // 1. Tạo layout chính
        BorderPane root = new BorderPane();
        
        // 2. Tạo các panel
        ControlPanel controlPanel = new ControlPanel();
        MapPanel mapPanel = new MapPanel();
        Dashboard dashboard = new Dashboard();
        
        // 3. Sắp xếp
        root.setTop(controlPanel);
        root.setCenter(mapPanel);
        root.setBottom(dashboard);
        
        // 4. Kết nối SUMO
        var sumoConnection = new SumoTraasConnection();
        sumoConnection.start();
        
        // 5. Tạo managers
        var laneManager = new LaneManager(sumoConnection);
        var vehicleManager = new VehicleManager(sumoConnection);
        var tlManager = new TrafficLightManager(sumoConnection);
        
        // 6. Set managers cho MapPanel
        mapPanel.setManagers(laneManager, vehicleManager, tlManager);
        
        // 7. Render map
        laneManager.loadMapData();  // Load dữ liệu vào CORES cache
        mapPanel.renderMap();        // Vẽ đường
        mapPanel.renderTrafficLights();  // Vẽ đèn
        mapPanel.recenterView();     // Căn giữa bản đồ
        
        // 8. Tạo simulation engine
        var simEngine = new SimulationEngine(sumoConnection, mapPanel, dashboard);
        
        // 9. Gắn sự kiện nút
        controlPanel.getStartButton().setOnAction(e -> simEngine.start());
        controlPanel.getPauseButton().setOnAction(e -> simEngine.stop());
        
        // 10. Tạo scene và hiển thị
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
```

### 7.3 MapPanel.java - Các phương thức chính
```java
public class MapPanel extends StackPane {
    
    // 1. Load dữ liệu từ SUMO vào cache (CORES)
    public void loadMapData() {
        try {
            List<String> laneIDs = laneManager.getIDList();
            for (String laneID : laneIDs) {
                List<double[]> coords = laneManager.getCoordinateList(laneID);
                LaneData laneData = new LaneData(laneID, coords);
                laneDataCache.put(laneID, laneData);  // Lưu vào cache
            }
        } catch (Exception e) {
            System.err.println("Lỗi load dữ liệu: " + e.getMessage());
        }
    }
    
    // 2. Vẽ bản đồ từ cache
    public void renderMap() {
        try {
            for (LaneData laneData : laneDataCache.values()) {
                renderLaneFromData(laneData);
            }
        } catch (Exception e) {
            System.err.println("Lỗi vẽ bản đồ: " + e.getMessage());
        }
    }
    
    // 3. Vẽ một đường từ LaneData
    private void renderLaneFromData(LaneData laneData) {
        Group laneGroup = new Group();
        
        for (int i = 0; i < laneData.polyline.size() - 1; i++) {
            SumoPosition2D p1 = laneData.polyline.get(i);
            SumoPosition2D p2 = laneData.polyline.get(i + 1);
            
            Line line = new Line(p1.x, -p1.y, p2.x, -p2.y);
            line.setStroke(Color.GRAY);
            laneGroup.getChildren().add(line);
        }
        
        laneLayer.getChildren().add(laneGroup);
    }
    
    // 4. Update xe mỗi frame
    public void updateVehicles() {
        try {
            vehicleLayer.getChildren().clear();
            
            for (String vehicleID : vehicleManager.getIDList()) {
                updateVehicleWithData(vehicleID);
            }
            
            updateTrafficLights();
        } catch (Exception e) {
            System.err.println("Lỗi update xe: " + e.getMessage());
        }
    }
    
    // 5. Update màu đèn giao thông
    private void updateTrafficLights() {
        for (String tlID : trafficLightShapes.keySet()) {
            String state = trafficLightManager.getState(tlID);
            char dominant = getDominantState(state);
            
            Circle light = trafficLightShapes.get(tlID);
            Color color = switch(dominant) {
                case 'r' -> Color.RED;
                case 'y' -> Color.YELLOW;
                case 'g' -> Color.GREEN;
                default -> Color.GRAY;
            };
            
            light.setFill(color);
        }
    }
}
```

---

## Phần 8: Một Số Kiến Thức Quan Trọng

### 8.1 Transform (Biến Đổi Hình Ảnh)
```java
// Translate - Di chuyển
Translate translate = new Translate(x, y);

// Rotate - Quay
Rotate rotate = new Rotate(angle, pivotX, pivotY);

// Scale - Phóng to/thu nhỏ
Scale scale = new Scale(scaleX, scaleY);

// Áp dụng nhiều transform
node.getTransforms().addAll(translate, rotate, scale);
```

### 8.2 Layout (Bố Cục)
```java
// BorderPane - 5 vị trí (North, South, East, West, Center)
BorderPane border = new BorderPane();
border.setTop(topNode);
border.setCenter(centerNode);

// HBox - Sắp xếp ngang
HBox hbox = new HBox(10);  // 10px spacing
hbox.getChildren().addAll(child1, child2);

// VBox - Sắp xếp dọc
VBox vbox = new VBox(10);
vbox.getChildren().addAll(child1, child2);

// StackPane - Xếp chồng
StackPane stack = new StackPane();
stack.getChildren().addAll(background, foreground);
```

### 8.3 Event Handling (Xử Lý Sự Kiện)
```java
// Mouse event
node.setOnMousePressed(event -> {
    double x = event.getSceneX();
    double y = event.getSceneY();
});

// Scroll event
node.setOnScroll(event -> {
    double deltaY = event.getDeltaY();
    if (deltaY > 0) zoomIn();
    else zoomOut();
});

// Button click
button.setOnAction(event -> {
    // Xử lý
});
```

### 8.4 Animation (Hiệu Ứng Động)
```java
// Timeline - Động hoá theo thời gian
Timeline timeline = new Timeline(
    new KeyFrame(Duration.seconds(1), 
        new KeyValue(node.layoutXProperty(), 100))
);
timeline.play();

// TranslateTransition - Di chuyển
TranslateTransition trans = new TranslateTransition(Duration.seconds(1), node);
trans.setToX(100);
trans.play();
```

---

## Phần 9: Lỗi Thường Gặp Và Cách Khắc Phục

### 9.1 SUMO không kết nối
**Lỗi:**
```
ConnectionRefusedException: Cannot connect to SUMO
```
**Cách khắc phục:**
```bash
1. Kiểm tra SUMO đã chạy chưa
   sumo -c <config_file>.sumocfg

2. Kiểm tra cổng 8813 đã mở chưa
   netstat -an | find "8813"

3. Kiểm tra TraaS.jar có trong lib/ không
   ls lib/TraaS.jar
```

### 9.2 Bản đồ không hiển thị
**Lỗi:**
```
MapPanel hiện toàn màu xám, không có đường
```
**Cách khắc phục:**
```java
1. Kiểm tra loadMapData() có được gọi không
   System.out.println("Lanes loaded: " + laneDataCache.size());

2. Kiểm tra renderMap() vẽ được không
   for (LaneData lane : laneDataCache.values()) {
       System.out.println("Rendering lane: " + lane.laneID);
   }

3. Kiểm tra layer có được thêm vào world không
   System.out.println("Lanes on map: " + laneLayer.getChildren().size());
```

### 9.3 Xe không chuyển động
**Lỗi:**
```
Xe xuất hiện nhưng không di chuyển
```
**Cách khắc phục:**
```java
1. Kiểm tra SimulationEngine có chạy không
   if (simEngine.isRunning()) { ... }

2. Kiểm tra updateVehicles() có được gọi không
   @Override
   public void handle(long now) {
       mapPanel.updateVehicles();  // Phải có dòng này
   }

3. Kiểm tra vehicle cache có update không
   System.out.println("Vehicle cache size: " + vehicleDataCache.size());
```

### 9.4 Zoom không hoạt động
**Lỗi:**
```
Cuộn chuột nhưng bản đồ không phóng to/thu nhỏ
```
**Cách khắc phục:**
```java
1. Kiểm tra Affine Transform có được set không
   if (viewTransform == null) {
       System.err.println("Transform chưa được tạo");
   }

2. Kiểm tra scale trong khoảng hợp lệ
   System.out.println("Current scale: " + scale);
   System.out.println("MIN: " + MIN_SCALE + ", MAX: " + MAX_SCALE);

3. Kiểm tra viewport có size không
   System.out.println("Viewport: " + viewport.getWidth() + "x" + viewport.getHeight());
```

---

## Phần 10: Tham Khảo Và Tài Nguyên

### 10.1 Tài Liệu Chính Thức
- **JavaFX**: https://openjfx.io/
- **SUMO**: https://sumo.dlr.de/
- **TraaS**: https://sumo.dlr.de/docs/TraCI/TraaS/

### 10.2 Các Lớp Quan Trọng
| Lớp | Mục Đích | Ví Dụ |
|-----|----------|-------|
| `StackPane` | Xếp chồng các node | Chứa MapPanel |
| `BorderPane` | Bố cục 5 vị trí | Layout chính |
| `Affine` | Transform (Pan/Zoom) | Điều khiển view |
| `Group` | Nhóm các node | Nhóm layers |
| `Line` | Vẽ đường thẳng | Vẽ lane |
| `Polygon` | Vẽ hình đa giác | Vẽ xe |
| `Circle` | Vẽ hình tròn | Vẽ đèn |

### 10.3 Methods Quan Trọng
```java
// Thêm node vào container
container.getChildren().add(node);
container.getChildren().addAll(node1, node2, node3);

// Xóa tất cả node
container.getChildren().clear();

// Áp dụng transform
node.getTransforms().addAll(transform1, transform2);

// Bind property
label.textProperty().bind(slider.valueProperty().asString());

// Listen to property
property.addListener((obs, oldVal, newVal) -> {
    System.out.println("Changed from " + oldVal + " to " + newVal);
});
```

---

## Kết Luận

Bây giờ bạn đã hiểu:
1. ✅ Kiến trúc 3 lớp (GUI - CORES - WRAPPER)
2. ✅ Các thành phần GUI chính (MainWindow, MapPanel, Dashboard, ControlPanel)
3. ✅ Cách vẽ và cập nhật bản đồ
4. ✅ Cách kết nối với SUMO
5. ✅ Quy trình chạy ứng dụng
6. ✅ Cách xử lý sự kiện (Pan, Zoom)
7. ✅ Cách debug lỗi

**Bước tiếp theo:**
1. Chạy ứng dụng và xem nó hoạt động
2. Thay đổi màu, kích thước các thành phần
3. Thêm các tính năng mới (zoom to region, search vehicle, v.v.)
4. Tìm hiểu thêm về JavaFX Animation và Effects

**Chúc bạn học tập vui vẻ! 🎉**
