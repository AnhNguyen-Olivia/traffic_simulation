# 🎨 Cây UI (Component Hierarchy) - Ứng Dụng Mô Phỏng Giao Thông

## c
---

## Chi Tiết Từng Thành Phần

### 1️⃣ STAGE (Cửa Sổ Ứng Dụng)
```
Stage
├─ Title: "Real Time SUMO Traffic Simulation"
├─ Size: 1200 x 700 (khởi tạo)
├─ MinWidth: 1000
├─ MinHeight: 600
├─ Resizable: true (có thể resize)
└─ Maximized: false
```

### 2️⃣ SCENE (Canvas Chính)
```
Scene (1200 x 700)
├─ Background Color: #F5F5F7 (macOS light gray)
└─ Root: BorderPane
```

### 3️⃣ BorderPane (Bố Cục Chính)
```
BorderPane (root)
├─ Style: -fx-background-color: #F5F5F7
├─ Left: ScrollPane (ControlPanel)
│   ├─ FitToWidth: true
│   ├─ HbarPolicy: NEVER (ẩn thanh cuộn ngang)
│   ├─ VbarPolicy: AS_NEEDED (thanh cuộn dọc khi cần)
│   ├─ Pannable: true (kéo để scroll)
│   └─ Content: ControlPanel
│
├─ Center: MapPanel (StackPane)
│   └─ Chứa bản đồ (không scroll)
│
└─ Right: ScrollPane (Dashboard)
    ├─ FitToWidth: true
    ├─ HbarPolicy: NEVER
    ├─ VbarPolicy: AS_NEEDED
    ├─ Pannable: true
    └─ Content: Dashboard
```

---

## MapPanel - Chi Tiết Cây Layers

### Cấu Trúc Nested
```
MapPanel (StackPane)
├─ Clip: Rectangle (giới hạn hiển thị)
│
├─ Viewport (Pane)
│  └─ Background: #F0F0F0
│  │
│  └─ World (Group) [Transform: Affine]
│     │
│     ├─ LaneLayer (Group)
│     │  └─ laneShapes (Map<String, Group>)
│     │     └─ Mỗi lane:
│     │        └─ Line x N (các đoạn đường)
│     │
│     ├─ TrafficLightLayer (Group)
│     │  └─ trafficLightShapes (Map<String, Circle>)
│     │     └─ Mỗi traffic light:
│     │        └─ Group
│     │           ├─ Rectangle (base)
│     │           ├─ Rectangle (pole)
│     │           ├─ Rectangle (housing)
│     │           └─ Circle (bulb) ← Update màu mỗi frame
│     │
│     └─ VehicleLayer (Group)
│        └─ vehicleShapes (Map<String, Polygon>)
│           └─ Mỗi vehicle:
│              └─ Polygon [Transform: Translate + Rotate]
│                 ├─ Fill: LinearGradient (màu xe)
│                 ├─ Stroke: Color (viền)
│                 └─ Effect: DropShadow (bóng)
│
└─ ZoomButtons (VBox)
   ├─ Position: TOP_RIGHT
   ├─ Spacing: 5px
   ├─ Button "+"
   │  ├─ Size: 36x36
   │  ├─ Style: macOS blue (#007AFF)
   │  ├─ onAction: zoomIn()
   │  ├─ onMouseEntered: highlight
   │  └─ onMouseExited: normal
   │
   └─ Button "-"
      ├─ Size: 36x36
      ├─ Style: macOS blue (#007AFF)
      ├─ onAction: zoomOut()
      ├─ onMouseEntered: highlight
      └─ onMouseExited: normal
```

---

## ControlPanel - Chi Tiết Thành Phần

```
ControlPanel (VBox)
├─ Style: -fx-background-color: #FFFFFF
├─ Padding: 20px
├─ Spacing: 15px
│
├─ Label "Controls" (Title)
│  └─ Font: 18px, Bold
│
├─ Button "▶ Play"
│  ├─ Size: PrefWidth 180px, Height 40px
│  ├─ Style: macOS style (blue)
│  └─ onAction: simulationEngine.start()
│
├─ Button "⏸ Pause"
│  ├─ Size: PrefWidth 180px, Height 40px
│  ├─ Style: macOS style (gray)
│  └─ onAction: simulationEngine.stop()
│
├─ Button "🔄 Reset"
│  ├─ Size: Prefwidth 180px, Height 40px
│  ├─ Style: macOS style (orange)
│  └─ onAction: simulationEngine.reset()
│
├─ Separator (divider)
│
├─ Label "Simulation Speed:"
│  └─ Font: 14px
│
├─ Slider speedSlider
│  ├─ Min: 0.5
│  ├─ Max: 3.0
│  ├─ Value: 1.0
│  ├─ Major Tick Unit: 0.5
│  ├─ ShowTickLabels: true
│  ├─ ShowTickMarks: true
│  └─ onValueChange: simulationEngine.setSpeed()
│
├─ Label "Simulation Time: 0.0s"
│  ├─ Dynamic update
│  └─ Updates mỗi frame
│
├─ Separator
│
├─ Label "Active Vehicles: 0"
│  └─ Dynamic update
│
└─ Separator
```

---

## Dashboard - Chi Tiết Thành Phần

```
Dashboard (VBox)
├─ Style: -fx-background-color: #FFFFFF
├─ Padding: 20px
├─ Spacing: 15px
│
├─ Label "Statistics" (Title)
│  └─ Font: 18px, Bold
│
├─ HBox (Vehicle Info)
│  ├─ Label "🚗 Vehicles:"
│  └─ Label "0" (Dynamic)
│
├─ HBox (Lane Info)
│  ├─ Label "🛣️ Lanes:"
│  └─ Label "0" (Dynamic)
│
├─ HBox (Time Info)
│  ├─ Label "⏱️ Time:"
│  └─ Label "0.00s" (Dynamic)
│
├─ Separator
│
├─ Label "Traffic Lights Status"
│  └─ Font: 14px, Bold
│
├─ ScrollPane
│  └─ VBox (List of traffic lights)
│     └─ HBox (mỗi đèn)
│        ├─ Circle (màu hiện tại)
│        ├─ Label (ID)
│        └─ Label (State: RED/GREEN/YELLOW)
│
├─ Separator
│
├─ Label "Top Vehicles by Speed"
│  └─ Font: 14px, Bold
│
└─ VBox (Vehicle list)
   └─ HBox (mỗi xe) 
      ├─ Label (ID)
      ├─ Label (Speed: X km/h)
      └─ ProgressBar (tỷ lệ tốc độ)
```

---

## Luồng Dữ Liệu Qua UI Tree

```
┌─ MainWindow.start()
│
├─ BorderPane root được tạo
│  │
│  ├─ ControlPanel (leftPanel) được tạo
│  │  └─ Chờ SimulationEngine để bắt đầu
│  │
│  ├─ MapPanel (centerPanel) được tạo
│  │  ├─ renderMap() vẽ tất cả lanes
│  │  ├─ renderTrafficLights() vẽ tất cả đèn
│  │  └─ recenterView() căn giữa bản đồ
│  │
│  └─ Dashboard (rightPanel) được tạo
│     └─ Chờ update từ SimulationEngine
│
├─ Scene được tạo với BorderPane
│
└─ Stage hiển thị Scene
   │
   └─ Người dùng bấm nút Play
      │
      ├─ SimulationEngine.start()
      │  └─ Mỗi frame (30-60 FPS):
      │     │
      │     ├─ mapPanel.updateVehicles()
      │     │  ├─ Xóa vehicleLayer (clear old vehicles)
      │     │  ├─ Vẽ xe mới từ SUMO data
      │     │  └─ Update màu traffic lights
      │     │
      │     └─ Dashboard.updateInfo()
      │        ├─ Update số xe
      │        ├─ Update thời gian
      │        └─ Update trạng thái đèn
      │
      └─ Người dùng nhìn thấy bản đồ animated
```

---

## Chiều Cao / Chiều Rộng Mặc Định

| Thành Phần | Chiều Rộng | Chiều Cao | Ghi Chú |
|-----------|-----------|----------|---------|
| Window | 1200px | 700px | Khởi tạo, responsive |
| BorderPane | 100% | 100% | Full screen |
| ControlPanel | ~250px | 100% | Scrollable |
| MapPanel | ~700px | 100% | Pan/Zoom enabled |
| Dashboard | ~250px | 100% | Scrollable |
| Zoom Button | 36px | 36px | Fixed size |
| Lane | 2px width | Flexible | Stroke width |
| Vehicle | 5 units | 2 units | SUMO scale |
| Traffic Light | Bulb: Ø1.4 | Pole: 8 units | SUMO scale |

---

## CSS Styling Tree

```
Stage
├─ Scene
│  └─ background-color: #F5F5F7
│
└─ BorderPane
   ├─ background-color: #F5F5F7
   │
   ├─ Left (ScrollPane)
   │  └─ background: transparent
   │     └─ ControlPanel
   │        └─ background-color: #FFFFFF
   │           ├─ Buttons
   │           │  └─ background-color: #007AFF (blue)
   │           │  └─ hover: #0051D5 (darker blue)
   │           │
   │           └─ Labels
   │              └─ text-fill: #333333 (dark)
   │
   ├─ Center (MapPanel)
   │  ├─ background-color: #F0F0F0
   │  └─ border: #bdbdbd
   │
   └─ Right (ScrollPane)
      └─ background: transparent
         └─ Dashboard
            └─ background-color: #FFFFFF
               ├─ Labels
               │  └─ text-fill: #333333
               │
               └─ Traffic Light Status
                  ├─ Red circle: Color.rgb(220, 20, 20)
                  ├─ Green circle: Color.rgb(0, 200, 50)
                  └─ Yellow circle: Color.rgb(255, 200, 0)
```

---

## Binding & Properties Tree

```
MapPanel
├─ viewport.widthProperty()
│  └─ Bound to: Scene.width / viewport.clip.widthProperty()
│
├─ viewport.heightProperty()
│  └─ Bound to: Scene.height / viewport.clip.heightProperty()
│
└─ viewTransform (Affine)
   ├─ Tx (Translation X)
   ├─ Ty (Translation Y)
   └─ Scale (Mxx, Myy)

Dashboard
├─ vehicleCountLabel.textProperty()
│  └─ Bound to: simulationEngine.vehicleCountProperty()
│
├─ simulationTimeLabel.textProperty()
│  └─ Bound to: simulationEngine.timeProperty().asString()
│
└─ trafficLightStateLabel.textProperty()
   └─ Bound to: simulationEngine.tlStateProperty()

ControlPanel
└─ speedSlider.valueProperty()
   └─ Listener: simulationEngine.setSpeed()
```

---

## Event Listeners Tree

```
MapPanel
├─ viewport.onMousePressed
│  └─ Save anchor position
│
├─ viewport.onMouseDragged
│  └─ Update viewTransform (pan)
│
├─ viewport.onScroll
│  ├─ DeltaY > 0: zoomIn()
│  └─ DeltaY < 0: zoomOut()
│
├─ zoomInButton.onAction
│  └─ Call zoomIn()
│
└─ zoomOutButton.onAction
   └─ Call zoomOut()

ControlPanel
├─ playButton.onAction
│  └─ simulationEngine.start()
│
├─ pauseButton.onAction
│  └─ simulationEngine.stop()
│
├─ resetButton.onAction
│  └─ simulationEngine.reset()
│
└─ speedSlider.valueProperty
   └─ simulationEngine.setSpeed(value)

Dashboard
└─ Various labels listen to simulationEngine properties
   └─ Auto-update when property changes
```

---

## Transform Pipeline (Cho MapPanel)

```
World Group
    │
    └─ viewTransform (Affine)
       │
       ├─ Initial: Identity matrix
       │  [1  0  0]
       │  [0  1  0]
       │  [0  0  1]
       │
       ├─ After Pan: Translate (Tx, Ty)
       │  [1  0  Tx]
       │  [0  1  Ty]
       │  [0  0  1]
       │
       ├─ After Zoom: Scale (factor, factor)
       │  [scale  0   Tx]
       │  [0    scale Ty]
       │  [0      0   1]
       │
       └─ Result: Lane coordinates được transform theo user pan/zoom
           Original: (100, 200)
           After transform: (150, 250) on screen
```

---

## Kết Luận

**Tóm tắt cây UI:**
- **Stage** → **Scene** → **BorderPane** (3 cột)
  - **Left**: ScrollPane → ControlPanel (Nút điều khiển)
  - **Center**: MapPanel → Viewport → World Group (Bản đồ chính)
    - LaneLayer (đường)
    - TrafficLightLayer (đèn)
    - VehicleLayer (xe)
  - **Right**: ScrollPane → Dashboard (Thông tin)

**Đặc điểm:**
- ✅ Responsive design (resize cùng window)
- ✅ Modular (mỗi panel độc lập)
- ✅ Efficient rendering (chỉ update cần thiết)
- ✅ macOS styling (modern, clean)
- ✅ Event-driven (reactive to user input)

**Update Cycle:**
1. User bấm nút → Event fired
2. SimulationEngine chạy → gọi mapPanel.updateVehicles()
3. MapPanel update layers → Scene rendered
4. Dashboard update info → người dùng nhìn thấy
5. Loop lặp ~30-60 FPS
