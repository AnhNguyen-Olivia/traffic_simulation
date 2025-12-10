# 📚 Học JavaFX: Implementing Export Dialog

## 🎯 Mục tiêu bài học
Học cách sử dụng **ChoiceDialog** trong JavaFX để cho người dùng chọn giữa nhiều options (CSV hoặc PDF export).

---

## 📝 Các bước thực hiện

### Bước 1: Import các class cần thiết

```java
import javafx.scene.control.ChoiceDialog;
import java.util.Optional;
```

**Giải thích:**
- `ChoiceDialog`: Class để tạo dialog với dropdown cho người dùng chọn
- `Optional`: Class để xử lý kết quả có thể null (người dùng có thể cancel dialog)

---

### Bước 2: Thêm Event Handler cho Export Button

```java
exportButton.setOnAction(e -> showExportDialog());
```

**Giải thích:**
- `setOnAction()`: Method để gắn event handler khi button được click
- `e -> showExportDialog()`: Lambda expression, khi button click sẽ gọi method `showExportDialog()`

---

### Bước 3: Tạo Method showExportDialog()

```java
private void showExportDialog() {
    // 1. Tạo các string options
    String csvOption = "CSV - Save statistics for external analysis";
    String pdfOption = "PDF - Generate summary with charts and metrics";
    
    // 2. Tạo ChoiceDialog
    ChoiceDialog<String> dialog = new ChoiceDialog<>(csvOption, csvOption, pdfOption);
    
    // 3. Customize dialog
    dialog.setTitle("Export Format");
    dialog.setHeaderText("Select export format:");
    dialog.setContentText("Choose format:");
    
    // 4. Hiển thị và lấy kết quả
    Optional<String> result = dialog.showAndWait();
    
    // 5. Xử lý kết quả
    result.ifPresent(choice -> {
        if (choice.equals(csvOption)) {
            exportToCSV();
        } else if (choice.equals(pdfOption)) {
            exportToPDF();
        }
    });
}
```

**Giải thích chi tiết:**

#### 3.1. Tạo ChoiceDialog
```java
ChoiceDialog<String> dialog = new ChoiceDialog<>(csvOption, csvOption, pdfOption);
```
- Parameter 1 (`csvOption`): Giá trị default được chọn sẵn
- Parameter 2+ (`csvOption, pdfOption`): Các options trong dropdown

#### 3.2. Customize Dialog
```java
dialog.setTitle("Export Format");           // Tiêu đề window
dialog.setHeaderText("Select export format:"); // Text phía trên dropdown
dialog.setContentText("Choose format:");     // Label của dropdown
```

#### 3.3. Hiển thị Dialog và Lấy Kết quả
```java
Optional<String> result = dialog.showAndWait();
```
- `showAndWait()`: Hiển thị dialog và chờ người dùng chọn (blocking call)
- Trả về `Optional<String>` vì người dùng có thể:
  - Chọn một option → Optional chứa String
  - Click Cancel → Optional rỗng

#### 3.4. Xử lý Kết quả với Optional
```java
result.ifPresent(choice -> {
    if (choice.equals(csvOption)) {
        exportToCSV();
    } else if (choice.equals(pdfOption)) {
        exportToPDF();
    }
});
```
- `ifPresent()`: Chỉ chạy code bên trong nếu Optional có giá trị (người dùng không cancel)
- Lambda expression `choice -> {...}`: Nhận giá trị được chọn và xử lý

---

### Bước 4: Tạo Placeholder Methods cho Export

```java
private void exportToCSV() {
    System.out.println("📄 Exporting to CSV...");
    // TODO: Implement CSV export logic
}

private void exportToPDF() {
    System.out.println("📊 Exporting to PDF...");
    // TODO: Implement PDF export logic
}
```

**Giải thích:**
- Hiện tại chỉ in console để test
- Sau này sẽ implement logic thực tế:
  - Lấy data từ checkboxes (Color, Edge filters)
  - Tạo file CSV/PDF
  - Sử dụng `FileChooser` để người dùng chọn nơi lưu

---

## 🎓 Kiến thức quan trọng

### 1. Optional Pattern
```java
Optional<String> result = dialog.showAndWait();
result.ifPresent(value -> {
    // Code chỉ chạy nếu có value
});
```

**Tại sao dùng Optional?**
- Tránh `NullPointerException`
- Code rõ ràng hơn: "giá trị này có thể không tồn tại"
- Functional programming style

### 2. Lambda Expressions
```java
// Thay vì viết:
button.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent e) {
        showExportDialog();
    }
});

// Viết ngắn gọn:
button.setOnAction(e -> showExportDialog());
```

### 3. Method Reference
```java
// Nếu method không nhận parameter, có thể viết:
button.setOnAction(e -> this.showExportDialog());

// Hoặc ngắn hơn (method reference):
// button.setOnAction(this::showExportDialog);
```

---

## 🧪 Cách test

1. Chạy application: `mvn javafx:run`
2. Click nút **"📤 Export"** trong Dashboard (bên phải)
3. Dialog hiện ra với 2 lựa chọn
4. Chọn CSV hoặc PDF
5. Check console để thấy message:
   - `📄 Exporting to CSV...` hoặc
   - `📊 Exporting to PDF...`

---

## 📚 Bài tập mở rộng

1. **Thêm validation:** Kiểm tra xem có checkbox nào được chọn không trước khi export
2. **Thêm confirmation dialog:** Sau khi chọn format, hiện dialog confirm
3. **Customize dialog style:** Thêm CSS để đổi màu, font của dialog
4. **Add more options:** Thêm option thứ 3 là "Excel"

---

## 🔗 Liên kết

- [JavaFX ChoiceDialog Documentation](https://openjfx.io/javadoc/21/javafx.controls/javafx/scene/control/ChoiceDialog.html)
- [Java Optional Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html)
- [Lambda Expressions Tutorial](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)

---

## ✅ Checklist kiến thức

- [ ] Hiểu cách tạo ChoiceDialog
- [ ] Biết cách customize dialog (title, header, content)
- [ ] Hiểu Optional pattern và tại sao dùng
- [ ] Biết cách dùng ifPresent() để xử lý Optional
- [ ] Hiểu lambda expression trong event handler
- [ ] Biết cách gọi method khác từ event handler
