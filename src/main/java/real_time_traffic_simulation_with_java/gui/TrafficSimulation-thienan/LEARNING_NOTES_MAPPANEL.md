# 📚 Học JavaFX: MapPanel với Zoom, Pan, và Rotation

## 🎯 Mục tiêu bài học
Học cách tạo interactive map với camera controls:
- **Zoom In/Out** (+/- buttons)
- **Pan** (kéo chuột để di chuyển)
- **Rotation** (xoay map)
- **Canvas drawing** với transformations

---

## 📝 Các thành phần chính

### 1. Canvas - Vẽ đồ họa 2D

```java
private Canvas canvas;
private GraphicsContext gc;
```

**Giải thích:**
- `Canvas`: Bề mặt để vẽ đồ họa 2D (như một tờ giấy trắng)
- `GraphicsContext`: Bút vẽ (có các method như drawLine, fillRect, etc.)

**Khởi tạo:**
```java
canvas = new Canvas(800, 600);
gc = canvas.getGraphicsContext2D();
```

**Auto-resize:**
```java
canvas.widthProperty().bind(canvasContainer.widthProperty());
canvas.heightProperty().bind(canvasContainer.heightProperty());
canvas.widthProperty().addListener(e -> drawMap());
canvas.heightProperty().addListener(e -> drawMap());
```
- `bind()`: Canvas size tự động theo container size
- `addListener()`: Khi size thay đổi → vẽ lại map

---

### 2. Transform Variables

```java
private double scale = 1.0;        // Zoom level (1.0 = 100%)
private double translateX = 0;     // Di chuyển X
private double translateY = 0;     // Di chuyển Y
private double rotation = 0;       // Góc xoay (degrees)
```

**Ý nghĩa:**
- `scale > 1.0` → zoom in (phóng to)
- `scale < 1.0` → zoom out (thu nhỏ)
- `translateX/Y` → vị trí camera
- `rotation` → góc xoay map

---

## 🎨 Vẽ với Transformations

### 3. Apply Transformations

```java
gc.save();  // 1. Save state hiện tại

// 2. Apply transformations (theo thứ tự)
gc.translate(width / 2 + translateX, height / 2 + translateY);  // Di chuyển origin
gc.rotate(rotation);                                            // Xoay
gc.scale(scale, scale);                                        // Zoom
gc.translate(-width / 2, -height / 2);                        // Reset origin

// 3. Vẽ đồ họa
drawGrid(width, height);
drawSampleRoadNetwork(width, height);

gc.restore();  // 4. Restore state ban đầu
```

**Giải thích từng bước:**

#### 3.1. `gc.save()` và `gc.restore()`
- `save()`: Lưu trạng thái hiện tại (transformations, colors, styles)
- `restore()`: Khôi phục lại trạng thái đã save
- **Tại sao cần?** Để transformations không ảnh hưởng đến lần vẽ sau

#### 3.2. `gc.translate(x, y)`
- Di chuyển origin (điểm 0,0)
- Ví dụ: `translate(100, 50)` → mọi thứ vẽ sẽ dịch phải 100px, xuống 50px

#### 3.3. `gc.rotate(degrees)`
- Xoay canvas quanh origin
- Đơn vị: degrees (không phải radians)
- Ví dụ: `rotate(45)` → xoay 45 độ

#### 3.4. `gc.scale(scaleX, scaleY)`
- Phóng to/thu nhỏ
- `scale(2, 2)` → phóng to 2 lần
- `scale(0.5, 0.5)` → thu nhỏ 50%

---

## 🖱️ Pan - Kéo chuột để di chuyển

### 4. Mouse Events

```java
private void setupPanning() {
    // Mouse Pressed - Bắt đầu kéo
    canvas.setOnMousePressed(e -> {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
        isPanning = true;
        canvas.setCursor(Cursor.CLOSED_HAND);
    });
    
    // Mouse Dragged - Đang kéo
    canvas.setOnMouseDragged(e -> {
        if (isPanning) {
            double deltaX = e.getX() - lastMouseX;  // Khoảng cách di chuyển
            double deltaY = e.getY() - lastMouseY;
            
            translateX += deltaX;  // Cập nhật vị trí camera
            translateY += deltaY;
            
            lastMouseX = e.getX();  // Update vị trí chuột
            lastMouseY = e.getY();
            
            drawMap();  // Vẽ lại
        }
    });
    
    // Mouse Released - Thả chuột
    canvas.setOnMouseReleased(e -> {
        isPanning = false;
        canvas.setCursor(Cursor.OPEN_HAND);
    });
}
```

**Flow:**
1. **Press** → Lưu vị trí chuột, đổi cursor thành closed hand
2. **Drag** → Tính delta (khoảng cách di chuyển), update translateX/Y, vẽ lại
3. **Release** → Đổi cursor về open hand

---

## 🔍 Zoom In/Out

### 5. Zoom Methods

```java
private void zoomIn() {
    if (scale < 3.0) {           // Giới hạn max zoom
        scale += 0.2;            // Tăng 20%
        updateZoomLabel();       // Update label "120%"
        drawMap();               // Vẽ lại
    }
}

private void zoomOut() {
    if (scale > 0.3) {           // Giới hạn min zoom
        scale -= 0.2;            // Giảm 20%
        updateZoomLabel();
        drawMap();
    }
}
```

**Giải thích:**
- Mỗi lần click → thay đổi `scale` 0.2 (20%)
- Giới hạn: 30% đến 300% để tránh quá nhỏ/to
- `drawMap()` sẽ apply `gc.scale(scale, scale)`

---

## 🔄 Rotation

### 6. Rotate Methods

```java
private void rotateLeft() {
    rotation -= 15;                    // Giảm 15 độ (ngược chiều kim đồng hồ)
    if (rotation < 0) rotation += 360; // Keep trong range 0-360
    drawMap();
}

private void rotateRight() {
    rotation += 15;                    // Tăng 15 độ (cùng chiều kim đồng hồ)
    if (rotation >= 360) rotation -= 360;
    drawMap();
}
```

**Giải thích:**
- Mỗi click xoay 15 độ
- Keep rotation trong range 0-360
- `drawMap()` sẽ apply `gc.rotate(rotation)`

---

## 🎨 Vẽ đồ họa cơ bản

### 7. Drawing Methods

#### 7.1. Vẽ Grid
```java
gc.setStroke(Color.rgb(220, 220, 220));  // Màu line
gc.setLineWidth(1);                      // Độ dày line

// Vertical lines
for (int x = 0; x < width; x += 50) {
    gc.strokeLine(x, 0, x, height);
}

// Horizontal lines
for (int y = 0; y < height; y += 50) {
    gc.strokeLine(0, y, width, y);
}
```

#### 7.2. Vẽ Roads
```java
gc.setStroke(Color.rgb(80, 80, 80));  // Màu đường
gc.setLineWidth(8);                    // Đường rộng 8px

// Horizontal road
gc.strokeLine(centerX - 200, centerY, centerX + 200, centerY);

// Vertical road
gc.strokeLine(centerX, centerY - 150, centerX, centerY + 150);
```

#### 7.3. Vẽ Dashed Lines (Lane Markings)
```java
gc.setStroke(Color.YELLOW);
gc.setLineWidth(2);
gc.setLineDashes(10, 10);  // 10px line, 10px gap

gc.strokeLine(centerX - 200, centerY, centerX + 200, centerY);

gc.setLineDashes(0);  // Reset về solid line
```

#### 7.4. Vẽ Traffic Light
```java
gc.setFill(Color.BLACK);
gc.fillRect(x - 3, y - 3, 6, 6);  // Box đen 6x6

gc.setFill(color);
gc.fillOval(x - 2, y - 2, 4, 4);  // Light tròn 4x4
```

#### 7.5. Vẽ Vehicle
```java
gc.setFill(color);
gc.fillRect(x, y, 20, 8);  // Body xe 20x8

// Windows
gc.setFill(Color.WHITE);
gc.fillRect(x + 2, y + 1, 4, 2);   // Front window
gc.fillRect(x + 14, y + 1, 4, 2);  // Back window
```

---

## 🎛️ Control Panel Layout

### 8. VBox với Buttons

```java
VBox panel = new VBox(5);  // Spacing 5px giữa các children
panel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); " +  // Semi-transparent white
              "-fx-padding: 10; " +
              "-fx-background-radius: 8; " +
              "-fx-border-color: #bdbdbd; " +
              "-fx-border-radius: 8; " +
              "-fx-border-width: 1;");
```

**Components:**
```
VBox (Control Panel)
├── Label (Zoom: "100%")
├── HBox (Zoom buttons: - and +)
├── HBox (Rotate buttons: ↺ and ↻)
├── Button (Reset)
└── Label (Info: "🖱️ Drag to pan")
```

---

## 🧪 Cách Test

### Test Zoom:
1. Click nút **+** → Map phóng to (scale tăng)
2. Click nút **−** → Map thu nhỏ (scale giảm)
3. Label hiển thị zoom level (80%, 100%, 120%, etc.)

### Test Pan:
1. Click và giữ chuột trên map
2. Kéo chuột → Map di chuyển theo
3. Cursor đổi thành closed hand khi kéo

### Test Rotation:
1. Click **↺** → Map xoay ngược chiều kim đồng hồ
2. Click **↻** → Map xoay cùng chiều kim đồng hồ
3. Mỗi click xoay 15 độ

### Test Reset:
1. Zoom, pan, rotate map
2. Click **Reset** → Trở về trạng thái ban đầu (100%, center, 0°)

---

## 🎓 Kiến thức quan trọng

### 1. Canvas vs Node
- **Canvas**: Bitmap-based, vẽ pixels → performance tốt cho nhiều objects
- **Node** (Button, Label): Vector-based, mỗi object là một node → performance kém nếu nhiều

### 2. GraphicsContext Methods
```java
// Shapes
gc.strokeLine(x1, y1, x2, y2);
gc.strokeRect(x, y, width, height);
gc.fillRect(x, y, width, height);
gc.fillOval(x, y, width, height);

// Text
gc.fillText("Hello", x, y);
gc.strokeText("World", x, y);

// Styles
gc.setStroke(color);
gc.setFill(color);
gc.setLineWidth(width);
gc.setLineDashes(dashes...);
```

### 3. Coordinate System
```
(0,0) -----------> X
  |
  |
  |
  v
  Y
```
- Origin ở góc trên bên trái
- X tăng sang phải
- Y tăng xuống dưới

### 4. Transformation Order Matters!
```java
// ĐÚNG:
gc.translate(x, y);
gc.rotate(angle);
gc.scale(s, s);

// SAI (kết quả khác):
gc.scale(s, s);
gc.rotate(angle);
gc.translate(x, y);
```

---

## 📚 Bài tập mở rộng

1. **Mouse Wheel Zoom**: Thêm scroll chuột để zoom
   ```java
   canvas.setOnScroll(e -> {
       if (e.getDeltaY() > 0) zoomIn();
       else zoomOut();
   });
   ```

2. **Zoom to Mouse Position**: Zoom về vị trí chuột (không phải center)

3. **Mini Map**: Thêm mini map ở góc để hiển thị toàn cảnh

4. **Keyboard Controls**: Thêm arrow keys để pan, +/- để zoom

5. **Grid Toggle**: Button để bật/tắt grid

6. **Smooth Animations**: Dùng Transition để zoom/rotate mượt

---

## 🔗 Liên kết

- [Canvas Documentation](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/canvas/Canvas.html)
- [GraphicsContext Documentation](https://openjfx.io/javadoc/21/javafx.graphics/javafx/scene/canvas/GraphicsContext.html)
- [Transforms Tutorial](https://docs.oracle.com/javafx/2/transformations/jfxpub-transformations.htm)

---

## ✅ Checklist kiến thức

- [ ] Hiểu Canvas và GraphicsContext
- [ ] Biết cách bind canvas size với container
- [ ] Hiểu transformations (translate, rotate, scale)
- [ ] Biết thứ tự apply transformations
- [ ] Hiểu save() và restore() state
- [ ] Biết cách handle mouse events (pressed, dragged, released)
- [ ] Biết cách vẽ shapes (line, rect, oval)
- [ ] Biết cách set colors và line styles
- [ ] Hiểu coordinate system của Canvas
- [ ] Biết cách update UI khi data thay đổi (drawMap())
