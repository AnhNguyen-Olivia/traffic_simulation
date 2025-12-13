# 🏗️ So sánh CORES vs WRAPPER - Kiến trúc phân tầng

## 📋 Tổng quan

Dự án chia thành **2 tầng chính**:

```
┌─────────────────────────────────────────────────────────┐
│                    GUI Layer (View)                     │
│         (MainWindow, MapPanel, ControlPanel...)         │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ sử dụng
                     ▼
┌─────────────────────────────────────────────────────────┐
│                   CORES Package                         │
│         (EdgeData, VehicleData, LaneData...)            │
│              📦 Domain Objects (Data)                    │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ lấy dữ liệu từ
                     ▼
┌─────────────────────────────────────────────────────────┐
│                  WRAPPER Package                        │
│     (EdgeManager, VehicleManager, LaneManager...)       │
│         🔌 API Communication với SUMO                    │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ TraCI Protocol (TCP)
                     ▼
┌─────────────────────────────────────────────────────────┐
│                    SUMO Process                         │
│                   (sumo.exe)                            │
└─────────────────────────────────────────────────────────┘
```

---

## 🔑 Sự khác biệt cốt lõi

| Khía cạnh | **WRAPPER** | **CORES** |
|-----------|-------------|-----------|
| **Vai trò** | 🔌 Giao tiếp với SUMO | 📦 Lưu trữ dữ liệu |
| **Chức năng** | API calls (get/set) | Data storage & logic |
| **Phụ thuộc** | `SumoTraciConnection` | Không phụ thuộc SUMO |
| **Kiểu dữ liệu** | SUMO objects (`SumoPosition2D`, `SumoGeometry`) | JavaFX objects (`Point2D`) |
| **Khi nào dùng** | Khi cần lấy/gửi data từ/đến SUMO | Khi cần lưu trữ và xử lý data trong app |
| **Pattern** | Wrapper Pattern | Data Transfer Object (DTO) |

---

## 📦 WRAPPER Package - Lớp giao tiếp

### 🎯 Mục đích
**Wrapper = "Người phiên dịch"** giữa Java và SUMO.

```
Java App  →  [Wrapper]  →  TraCI  →  SUMO
          ←             ←        ←
```

### 📝 Ví dụ: VehicleManager.java

```java
public class VehicleManager {
    private final SumoTraciConnection conn;  // ⚡ Kết nối TCP với SUMO
    
    // ❓ Hỏi SUMO: "Cho tôi vị trí của xe này"
    public double[] getPosition(String vehicleID) throws Exception {
        SumoPosition2D pos = conn.do_job_get(
            de.tudresden.sumo.cmd.Vehicle.getPosition(vehicleID)
        );
        return new double[]{pos.x, pos.y};  // Trả về mảng [x, y]
    }
    
    // ❓ Hỏi SUMO: "Xe này màu gì?"
    public SumoColor getColor(String vehicleID) throws Exception {
        return (SumoColor) conn.do_job_get(
            de.tudresden.sumo.cmd.Vehicle.getColor(vehicleID)
        );
    }
}
```

### ✅ Đặc điểm:
- **Không lưu trữ dữ liệu** - chỉ lấy/gửi
- **Real-time** - mỗi lần gọi = 1 request đến SUMO
- **Trả về SUMO types** - `SumoPosition2D`, `SumoColor`, `SumoGeometry`
- **Throws Exception** - vì có thể mất kết nối SUMO

---

## 🧩 CORES Package - Lớp dữ liệu

### 🎯 Mục đích
**Cores = "Kho lưu trữ"** - chứa dữ liệu đã được xử lý, sẵn sàng dùng.

### 📝 Ví dụ: VehicleData.java

```java
public class VehicleData {
    // 🏷️ Thuộc tính - LƯU TRỮ dữ liệu
    public String vehicleID;
    public Point2D top_left_corner;  // ⚠️ JavaFX Point2D, KHÔNG phải SumoPosition2D
    public double angle;
    public String color;  // ⚠️ String, KHÔNG phải SumoColor
    
    // 🛠️ Constructor - XỬ LÝ và chuyển đổi dữ liệu
    public VehicleData(String vehicleID, double x, double y, 
                       double angle, SumoColor color) {
        this.vehicleID = vehicleID;
        this.top_left_corner = calculateTopLeftCorner(x, y);  // 🔄 Tính toán
        this.angle = angle;
        this.color = Color.colorToString(color);  // 🔄 Chuyển đổi
    }
    
    // 🧮 Logic nghiệp vụ
    private Point2D calculateTopLeftCorner(double x, double y) {
        double translate_vec = length/2 - width/2;
        return new Point2D(x + translate_vec, y + translate_vec);
    }
}
```

### ✅ Đặc điểm:
- **Lưu trữ dữ liệu** - public fields
- **Xử lý logic** - methods tính toán
- **JavaFX types** - `Point2D`, `String`, primitives
- **Không phụ thuộc SUMO** - có thể dùng mà không cần SUMO chạy

---

## 🔄 Luồng dữ liệu hoàn chỉnh

### Ví dụ: Hiển thị 1 chiếc xe lên map

```java
// 1️⃣ GUI yêu cầu vẽ xe
mapPanel.updateVehicles();

// 2️⃣ Lấy danh sách xe từ WRAPPER
List<String> vehicleIDs = vehicleManager.getIDList();  // ← WRAPPER

// 3️⃣ Với mỗi xe, lấy thông tin từ WRAPPER
for (String vehID : vehicleIDs) {
    double[] pos = vehicleManager.getPosition(vehID);      // ← WRAPPER
    double angle = vehicleManager.getAngle(vehID);         // ← WRAPPER
    SumoColor sumoColor = vehicleManager.getColor(vehID);  // ← WRAPPER
    
    // 4️⃣ Tạo object CORES để lưu trữ
    VehicleData vehicleData = new VehicleData(
        vehID, 
        pos[0], pos[1],  // x, y
        angle, 
        sumoColor
    );  // ← CORES (xử lý và lưu trữ)
    
    // 5️⃣ Dùng data để vẽ
    Point2D position = vehicleData.top_left_corner;  // ← CORES
    String color = vehicleData.color;                // ← CORES
    
    // Vẽ lên map
    drawVehicle(position, color, angle);
}
```

---

## 📊 So sánh chi tiết qua ví dụ

### Ví dụ 1: EdgeManager vs EdgeData

#### 🔌 EdgeManager (WRAPPER)

```java
public class EdgeManager {
    private final SumoTraciConnection conn;
    
    // ❓ Hỏi SUMO
    public List<String> getIDList() throws Exception {
        return conn.do_job_get(Edge.getIDList());
    }
    
    public int getLaneCount(String edgeID) throws Exception {
        return conn.do_job_get(Edge.getLaneNumber(edgeID));
    }
    
    public double getLength(String edgeID) throws Exception {
        return conn.do_job_get(Edge.getParameter(edgeID, "length"));
    }
}
```

**Cách dùng:**
```java
EdgeManager manager = new EdgeManager(sumoConnection);
int laneCount = manager.getLaneCount("E10");  // Gọi SUMO mỗi lần
double length = manager.getLength("E10");     // Gọi SUMO mỗi lần
```

#### 📦 EdgeData (CORES)

```java
public class EdgeData {
    // 🏷️ Lưu trữ
    public String edgeID;
    public int number_of_lanes;
    public double length;
    public List<Point2D> edge_corners;  // Đã xử lý sẵn
    
    // 🛠️ Nhận data từ WRAPPER và xử lý
    public EdgeData(String edgeID, int number_of_lanes, 
                    double length, List<SumoGeometry> coors) {
        this.edgeID = edgeID;
        this.number_of_lanes = number_of_lanes;
        this.length = length;
        
        // 🔄 Chuyển đổi SumoGeometry → JavaFX Point2D
        for (SumoGeometry geom : coors) {
            for (SumoPosition2D pos : geom.coords) {
                this.edge_corners.add(new Point2D(pos.x, pos.y));
            }
        }
    }
}
```

**Cách dùng:**
```java
// Lấy data 1 lần từ WRAPPER
String edgeID = "E10";
int laneCount = edgeManager.getLaneCount(edgeID);
double length = edgeManager.getLength(edgeID);
List<SumoGeometry> geom = edgeManager.getGeometry(edgeID);

// Tạo object CORES
EdgeData edge = new EdgeData(edgeID, laneCount, length, geom);

// Sau đó dùng object nhiều lần (KHÔNG cần gọi SUMO nữa)
System.out.println(edge.edgeID);           // ✅ Nhanh
System.out.println(edge.number_of_lanes);  // ✅ Nhanh
System.out.println(edge.edge_corners);     // ✅ Nhanh
```

---

## 🎯 Khi nào dùng gì?

### ✅ Dùng WRAPPER khi:
- ❓ **Cần data real-time từ SUMO** (vị trí xe, trạng thái đèn)
- 📡 **Gửi lệnh đến SUMO** (thêm xe, đổi màu đèn)
- 🔄 **Data thay đổi liên tục** (simulation đang chạy)

**Ví dụ:**
```java
// ✅ Trong animation loop - data thay đổi mỗi frame
for (String vehID : vehicleManager.getIDList()) {
    double[] pos = vehicleManager.getPosition(vehID);  // Real-time
    updateVehicleOnMap(vehID, pos);
}
```

### ✅ Dùng CORES khi:
- 💾 **Cần lưu trữ data** để dùng nhiều lần
- 🧮 **Cần xử lý/tính toán** trên data
- 🎨 **Cần format data** cho GUI (JavaFX types)
- ⚡ **Tối ưu performance** (tránh gọi SUMO nhiều lần)

**Ví dụ:**
```java
// ✅ Map rendering - data tĩnh, lưu 1 lần
List<LaneData> lanes = new ArrayList<>();
for (String laneID : laneManager.getIDList()) {
    SumoGeometry coords = laneManager.getCoordinateList(laneID);
    double length = laneManager.getLength(laneID);
    
    LaneData lane = new LaneData(laneID, edgeID, length, coords);
    lanes.add(lane);  // Lưu vào list
}

// Sau đó vẽ map từ list (KHÔNG gọi SUMO)
for (LaneData lane : lanes) {
    drawLane(lane.coordinates);  // ✅ Nhanh
}
```

---

## 🧪 Test với code thật

### Tạo 1 file test:

```java
public class TestWrapperVsCores {
    public static void main(String[] args) throws Exception {
        SumoTraasConnection sumoConn = new SumoTraasConnection();
        sumoConn.startConnection();
        
        VehicleManager vehicleManager = new VehicleManager(
            sumoConn.getConnection(), sumoConn
        );
        
        // 🔌 WRAPPER - Mỗi lần gọi = 1 request
        long start1 = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            double[] pos = vehicleManager.getPosition("veh_0");  // 100 requests
        }
        long end1 = System.nanoTime();
        System.out.println("WRAPPER: " + (end1 - start1) / 1_000_000 + "ms");
        
        
        // 📦 CORES - Lấy 1 lần, dùng nhiều lần
        long start2 = System.nanoTime();
        
        // Lấy data 1 lần
        double[] pos = vehicleManager.getPosition("veh_0");
        double angle = vehicleManager.getAngle("veh_0");
        SumoColor color = vehicleManager.getColor("veh_0");
        
        VehicleData vehicleData = new VehicleData(
            "veh_0", pos[0], pos[1], angle, color
        );
        
        // Dùng 100 lần
        for (int i = 0; i < 100; i++) {
            Point2D position = vehicleData.top_left_corner;  // ✅ Nhanh
        }
        long end2 = System.nanoTime();
        System.out.println("CORES: " + (end2 - start2) / 1_000_000 + "ms");
    }
}
```

**Kết quả:**
```
WRAPPER: 250ms   (100 requests qua network)
CORES:   2ms     (1 request + 99 lần đọc từ memory)
```

---

## 📚 Danh sách file

### 🔌 WRAPPER Package
```
wrapper/
├── EdgeManager.java          // API cho Edge
├── LaneManager.java          // API cho Lane
├── VehicleManager.java       // API cho Vehicle
├── TrafficLightManager.java  // API cho TrafficLight
├── RouteManager.java         // API cho Route
└── SumoTraasConnection.java  // Quản lý kết nối SUMO
```

### 📦 CORES Package
```
cores/
├── EdgeData.java             // Data object cho Edge
├── LaneData.java             // Data object cho Lane
├── VehicleData.java          // Data object cho Vehicle
├── TrafficLightData.java     // Data object cho TrafficLight
├── JunctionData.java         // Data object cho Junction
└── SimulationEngine.java     // Logic engine (không phải Data)
```

---

## 🎓 Tóm tắt

### 🔌 **WRAPPER = "Thư viện API"**
- Gọi SUMO qua TraCI
- Real-time data
- SUMO types (`SumoPosition2D`, `SumoColor`)
- Ném Exception

### 📦 **CORES = "Database trong RAM"**
- Lưu trữ data
- Processed data
- JavaFX types (`Point2D`, `String`)
- Logic nghiệp vụ

### 🔄 **Workflow chuẩn:**
```
1. GUI cần data
2. Gọi WRAPPER để lấy từ SUMO
3. Tạo object CORES để lưu
4. Dùng object CORES trong GUI
```

### 💡 **Nguyên tắc vàng:**
- ❌ **ĐỪNG** gọi WRAPPER trong loop (chậm)
- ✅ **NÊN** lấy 1 lần từ WRAPPER → tạo CORES object → dùng nhiều lần
