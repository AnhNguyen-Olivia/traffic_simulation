# 🗺️ HƯỚNG DẪN: CÁCH RENDER MAP TỪ SUMO LÊN MAPPANEL

## 📋 TỔNG QUAN

Document này giải thích chi tiết cách hệ thống render bản đồ từ SUMO simulation lên giao diện JavaFX MapPanel.

### Flow tổng thể:
```
SUMO (Simulation) → Java TraCI → Managers → MapPanel → Màn hình
```

---

## 🚀 BƯỚC 1: KHỞI ĐỘNG SUMO

**File:** `MainWindow.java` (dòng 35-37)

```java
SumoTraasConnection sumoConn = new SumoTraasConnection();
sumoConn.startConnection();
```

### Chuyện gì xảy ra:

1. **Tìm SUMO binary:**
   - Đọc path từ `Path.java`: `src/main/java/.../lib/sumo.exe`
   - Tìm file `sumo.exe` (Windows) hoặc `sumo` (Mac/Linux)

2. **Chạy SUMO:**
   ```bash
   sumo.exe map.net.xml map.rou.xml --remote-port 8813
   ```
   - `map.net.xml` → Network file (đường, giao lộ, traffic lights)
   - `map.rou.xml` → Route file (lộ trình xe)
   - `--remote-port 8813` → Mở port để Java kết nối

3. **SUMO load map:**
   - Parse XML files
   - Tạo network graph (nodes, edges, lanes)
   - Khởi tạo traffic lights
   - Sẵn sàng nhận lệnh từ Java

4. **Java kết nối qua TraCI:**
   - TraCI = Traffic Control Interface
   - Protocol để giao tiếp với SUMO
   - Giống REST API nhưng dùng socket TCP

### Kết quả:
- ✅ SUMO đang chạy ở background
- ✅ Java có connection để giao tiếp
- ✅ Map data đã load trong SUMO memory

---

## 🔧 BƯỚC 2: TẠO MANAGERS

**File:** `MainWindow.java` (dòng 40-44)

```java
LaneManager laneManager = new LaneManager(sumoConn.getConnection());
VehicleManager vehicleManager = new VehicleManager(sumoConn.getConnection(), sumoConn);
TrafficLightManager trafficLightManager = new TrafficLightManager(sumoConn.getConnection());
```

### Managers là gì?

**Manager** = Wrapper class để giao tiếp với SUMO

| Manager | Chức năng | API SUMO |
|---------|-----------|----------|
| `LaneManager` | Lấy thông tin lanes (đường) | `Lane.*` commands |
| `VehicleManager` | Lấy thông tin vehicles (xe) | `Vehicle.*` commands |
| `TrafficLightManager` | Lấy thông tin traffic lights | `TrafficLight.*` commands |

### Analogy (So sánh):

```
SUMO          = Cơ sở dữ liệu (Database)
Manager       = Data Access Object (DAO)
Java App      = Frontend hiển thị
```

### Ví dụ chi tiết - LaneManager:

**File:** `LaneManager.java`

```java
public class LaneManager {
    private final SumoTraciConnection conn;  // Kết nối tới SUMO
    
    // Lấy danh sách IDs của tất cả lanes
    public List<String> getIDList() throws Exception {
        // Gửi command tới SUMO: "Lane.getIDList()"
        return (List<String>) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getIDList());
    }
    
    // Lấy tọa độ của 1 lane
    public List<double[]> getCoordinateList(String laneID) throws Exception {
        // Gửi command: "Lane.getShape(laneID)"
        SumoGeometry geometry = (SumoGeometry) conn.do_job_get(
            de.tudresden.sumo.cmd.Lane.getShape(laneID)
        );
        
        // Convert geometry thành list of [x, y]
        List<double[]> coordinates = new ArrayList<>();
        for (SumoPosition2D pos : geometry.coords) {
            coordinates.add(new double[]{pos.x, pos.y});
        }
        return coordinates;
    }
    
    // Lấy độ rộng của lane (mét)
    public double getWidth(String laneID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getWidth(laneID));
    }
}
```

### Flow giao tiếp:

```
Java                    TraCI                   SUMO
----                    -----                   ----
getIDList()       →     CMD: 0xa3 (Lane)   →    Query lanes
                  ←     Response: List     ←    ["E1_0", "E2_0", ...]

getWidth("E1_0")  →     CMD: 0xa3 + laneID →    Query width
                  ←     Response: double   ←    3.2
```

---

## 🎯 BƯỚC 3: TRUYỀN MANAGERS CHO MAPPANEL

**File:** `MainWindow.java` (dòng 47-48)

```java
centerPanel.setManagers(laneManager, vehicleManager, trafficLightManager);
```

**File:** `MapPanel.java`

```java
// Biến lưu managers
private LaneManager laneManager;
private VehicleManager vehicleManager;
private TrafficLightManager trafficLightManager;

// Method nhận managers
public void setManagers(LaneManager laneManager, 
                       VehicleManager vehicleManager,
                       TrafficLightManager trafficLightManager) {
    this.laneManager = laneManager;
    this.vehicleManager = vehicleManager;
    this.trafficLightManager = trafficLightManager;
}
```

### Tại sao cần truyền managers?

- MapPanel **KHÔNG** kết nối trực tiếp với SUMO
- MapPanel dùng **managers** để lấy data
- Loose coupling → Dễ test, dễ thay đổi

---

## 🎨 BƯỚC 4: RENDER MAP (QUAN TRỌNG NHẤT!)

**File:** `MainWindow.java` (dòng 51-52)

```java
centerPanel.renderMap();
```

### 4.1. Lấy danh sách lanes

**File:** `MapPanel.java` - `renderMap()`

```java
public void renderMap() {
    // Lấy tất cả lane IDs từ SUMO
    List<String> laneIDs = laneManager.getIDList();
    // Kết quả: ["E1_0", "E1_1", "E2_0", "-E1_0", "-E2_0", ...]
    
    System.out.println("✅ Found " + laneIDs.size() + " lanes to render");
    // Output: ✅ Found 142 lanes to render
```

#### Giải thích lane IDs:

```
"E1_0"   → Edge 1, Lane 0 (làn đầu tiên)
"E1_1"   → Edge 1, Lane 1 (làn thứ 2)
"-E1_0"  → Edge 1 ngược chiều, Lane 0
```

**Ví dụ thực tế:**
```
Đường 2 chiều, mỗi chiều 2 làn:
- Chiều đi:   E1_0, E1_1
- Chiều về:  -E1_0, -E1_1
```

### 4.2. Loop qua từng lane

```java
for (String laneID : laneIDs) {
    renderLane(laneID);  // Vẽ 1 lane
}
```

### 4.3. Render 1 lane - Chi tiết từng dòng

**File:** `MapPanel.java` - `renderLane(String laneID)`

#### Bước 4.3.1: Lấy tọa độ lane

```java
List<double[]> coordinates = laneManager.getCoordinateList(laneID);
double width = laneManager.getWidth(laneID);
```

**Ví dụ data thực tế:**

```java
laneID = "E1_0"

coordinates = [
    [100.0, 200.0],   // Point 1: x=100, y=200
    [150.0, 210.0],   // Point 2: x=150, y=210
    [200.0, 220.0]    // Point 3: x=200, y=220
]

width = 3.2  // 3.2 mét
```

**Visualization:**
```
       Point 2 (150, 210)
       /
      /
Point 1 (100, 200) -------- Point 3 (200, 220)

Lane tạo thành đường cong nối 3 điểm
```

#### Bước 4.3.2: Vẽ các Line segments

```java
Group laneGroup = new Group();  // Nhóm chứa các lines

// Vẽ từng đoạn giữa 2 điểm liên tiếp
for (int i = 0; i < coordinates.size() - 1; i++) {
    double[] point1 = coordinates.get(i);      // [x1, y1]
    double[] point2 = coordinates.get(i + 1);  // [x2, y2]
    
    // Tạo Line từ point1 đến point2
    Line laneLine = new Line(
        point1[0],   // startX
        -point1[1],  // startY (ĐẢO DẤU!)
        point2[0],   // endX
        -point2[1]   // endY (ĐẢO DẤU!)
    );
    
    // Style cho line
    laneLine.setStroke(Color.rgb(70, 70, 70));  // Màu xám đậm (asphalt)
    laneLine.setStrokeWidth(width);              // Độ dày = 3.2 pixels
    laneLine.setStrokeLineCap(StrokeLineCap.ROUND);  // Đầu bo tròn
    
    laneGroup.getChildren().add(laneLine);  // Thêm line vào group
}
```

#### ⚠️ Tại sao đảo dấu Y (-y)?

**Hệ tọa độ khác nhau:**

```
SUMO Coordinate System:          JavaFX Coordinate System:
     ^ Y (North)                      (0,0) ─────→ X
     |                                  |
     |                                  |
     |                                  ↓ Y
(0,0)─────→ X                      

→ Y tăng = đi lên                 → Y tăng = đi xuống
```

**Công thức convert:**
```java
JavaFX_Y = -SUMO_Y
```

**Ví dụ:**
```
SUMO:   Point(100, 200)  → Y=200 (cao)
JavaFX: Point(100, -200) → Y=-200 hiển thị đúng vị trí
```

#### Bước 4.3.3: Thêm lane vào layer

```java
// Lưu vào cache (để update sau)
laneShapes.put(laneID, laneGroup);

// Thêm vào layer hiển thị
laneLayer.getChildren().add(laneGroup);
```

**Layer structure:**
```
MapPanel (StackPane)
  └─ viewport (Pane)
      └─ world (Group)
          ├─ laneLayer (Group)        ← Lanes ở đây!
          │   ├─ laneGroup "E1_0"
          │   │   ├─ Line 1
          │   │   └─ Line 2
          │   ├─ laneGroup "E1_1"
          │   └─ ...
          ├─ trafficLightLayer
          └─ vehicleLayer
```

### 4.4. Center view (canh giữa map)

```java
centerView();
```

**Chức năng:**
1. Tính bounds (giới hạn) của tất cả lanes
2. Tìm center point của map
3. Zoom để map vừa màn hình
4. Pan để center ở giữa viewport

**Code detail:**

```java
private void centerView() {
    // Tính bounds
    double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
    double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
    
    for (Node node : laneLayer.getChildren()) {
        Bounds bounds = node.getBoundsInParent();
        minX = Math.min(minX, bounds.getMinX());
        maxX = Math.max(maxX, bounds.getMaxX());
        minY = Math.min(minY, bounds.getMinY());
        maxY = Math.max(maxY, bounds.getMaxY());
    }
    
    // Tính center
    double mapCenterX = (minX + maxX) / 2;
    double mapCenterY = (minY + maxY) / 2;
    
    // Tính scale để fit
    double scaleX = viewport.getWidth() / (maxX - minX);
    double scaleY = viewport.getHeight() / (maxY - minY);
    double fitScale = Math.min(scaleX, scaleY) * 0.9;  // 90% để có margin
    
    // Apply transform
    viewTransform.setToIdentity();
    viewTransform.appendScale(fitScale, fitScale);
    viewTransform.appendTranslation(
        viewport.getWidth() / 2 / fitScale - mapCenterX,
        viewport.getHeight() / 2 / fitScale - mapCenterY
    );
}
```

---

## 🚦 BƯỚC 5: RENDER TRAFFIC LIGHTS

**File:** `MainWindow.java` (dòng 55-57)

```java
centerPanel.renderTrafficLights();
```

Tương tự render map, nhưng vẽ traffic lights thay vì lanes.

---

## 📊 FLOW CHART CHI TIẾT

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. MainWindow.start()                                           │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 2. new SumoTraasConnection()                                    │
│    → Đọc Path.java: sumo.exe, map.net.xml, map.rou.xml         │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 3. sumoConn.startConnection()                                   │
│    → Chạy: sumo.exe map.net.xml map.rou.xml --remote-port 8813 │
│    → SUMO load XML, tạo network graph                           │
│    → Java connect qua TraCI protocol                            │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 4. new LaneManager(connection)                                  │
│    → Lưu connection để gọi SUMO sau                             │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 5. mapPanel.setManagers(laneManager, ...)                       │
│    → MapPanel nhận managers                                     │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6. mapPanel.renderMap()                                         │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6.1. laneManager.getIDList()                                    │
│      Java → TraCI → SUMO: "GET /lanes"                          │
│      SUMO → TraCI → Java: ["E1_0", "E1_1", ...]                │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6.2. FOR EACH laneID in laneIDs:                                │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6.2.1. laneManager.getCoordinateList(laneID)                    │
│        Java → SUMO: "GET /lane/E1_0/shape"                      │
│        SUMO → Java: [[x1,y1], [x2,y2], [x3,y3]]                │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6.2.2. laneManager.getWidth(laneID)                             │
│        Java → SUMO: "GET /lane/E1_0/width"                      │
│        SUMO → Java: 3.2                                         │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6.2.3. VẼ LINES (JavaFX)                                        │
│        Group laneGroup = new Group()                            │
│        FOR i in 0..coordinates.size()-1:                        │
│           point1 = coordinates[i]                               │
│           point2 = coordinates[i+1]                             │
│           Line line = new Line(x1, -y1, x2, -y2)  // Đảo Y!    │
│           line.setStrokeWidth(width)                            │
│           laneGroup.add(line)                                   │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 6.2.4. laneLayer.add(laneGroup)                                 │
│        → Lines hiển thị lên màn hình!                           │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 7. centerView()                                                 │
│    → Tính bounds của map                                        │
│    → Zoom + Pan để vừa màn hình                                 │
└────────────────────┬────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────────────────────┐
│ 8. ✅ HOÀN TẤT                                                  │
│    User thấy map với tất cả lanes!                              │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🎯 TÓM TẮT

### Công thức đơn giản:

```
SUMO XML → TraCI → LaneManager → Coordinates → JavaFX Lines → Màn hình
```

### 5 bước chính:

1. **Khởi động SUMO** → Load map từ XML
2. **Tạo Managers** → Cầu nối Java ↔ SUMO
3. **Lấy lane IDs** → Danh sách tất cả đường
4. **Với mỗi lane:**
   - Lấy coordinates (list of points)
   - Lấy width
   - Vẽ Lines nối các points
   - Thêm vào layer
5. **Center view** → Map vừa màn hình

### Các khái niệm quan trọng:

| Khái niệm | Giải thích |
|-----------|------------|
| **Lane** | Một làn đường (1 direction, 1 làn xe) |
| **Edge** | Một con đường (có thể nhiều lanes) |
| **TraCI** | Protocol giao tiếp với SUMO |
| **Manager** | Wrapper class để call SUMO API |
| **Coordinate** | Tọa độ [x, y] trong hệ SUMO |
| **Layer** | Lớp chứa các elements (lanes, vehicles, lights) |

---

## 💡 TIPS & TRICKS

### Debug rendering:

```java
// In ra tọa độ để kiểm tra
System.out.println("Lane: " + laneID);
System.out.println("  Coordinates: " + coordinates);
System.out.println("  Width: " + width);
```

### Kiểm tra SUMO connection:

```bash
# Test SUMO standalone
sumo-gui map.net.xml map.rou.xml

# Kiểm tra port
netstat -an | findstr 8813
```

### Common issues:

1. **Map không hiện:**
   - Check SUMO có chạy không: Task Manager → sumo.exe
   - Check connection: Try-catch trong renderMap()

2. **Map bị ngược:**
   - Quên đảo Y: Phải dùng `-y` cho JavaFX

3. **Lanes bị mất:**
   - Check laneManager != null
   - Check getIDList() có trả về data không

---

## 📚 TÀI LIỆU THAM KHẢO

- [SUMO Documentation](https://sumo.dlr.de/docs/)
- [TraCI Protocol](https://sumo.dlr.de/docs/TraCI.html)
- [JavaFX Graphics](https://openjfx.io/javadoc/17/)

---

**Last updated:** December 12, 2025  
**Author:** Traffic Simulation Team
