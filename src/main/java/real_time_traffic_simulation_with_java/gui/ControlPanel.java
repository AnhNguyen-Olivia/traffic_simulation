package real_time_traffic_simulation_with_java.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ControlPanel extends VBox {
    
    // PHẦN 1: Connect to SUMO - Attributes (Biến)
    private Button startButton;
    private Label timeLabel;
    
    // PHẦN 2: Vehicle Injection - Attributes
    private ComboBox<String> edgeComboBox;
    private ComboBox<String> colorComboBox;
    private TextField quantityField;  // Nhập số lượng xe
    private Button injectButton;
    
    /**
     * Constructor - Khởi tạo ControlPanel
     */
    public ControlPanel() {
        // Thiết lập VBox chính - macOS style với responsive
        setPadding(new Insets(12));
        setMinWidth(220);   // Chiều rộng tối thiểu
        setPrefWidth(240);  // Chiều rộng ưa thích
        setMaxWidth(280);   // Chiều rộng tối đa
        setSpacing(12);     // 12px spacing (giảm từ 16px)
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #FFFFFF; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 2, 0);");
        
        // Tạo 3 phần
        createConnectSection();      // Phần 1
        getChildren().add(new Separator());  // Đường kẻ phân cách
        
        createVehicleSection();      // Phần 2
        getChildren().add(new Separator());
        
        createTrafficLightManagementSection(); // Phần 3
    }
    
    /**
     * PHẦN 1: Connect to SUMO
     * Học: Tách code thành method nhỏ (Clean Code)
     */
    private void createConnectSection() {
        // VBox con - macOS card style - Responsive
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        VBox.setVgrow(section, Priority.ALWAYS);  // Grow vertically
        section.setStyle("-fx-background-color: #FFFFFF; " +
                         "-fx-padding: 14; " +
                         "-fx-border-radius: 10; " +
                         "-fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);");
        
        // Title
        Label title = new Label("🔗 Connect to SUMO");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        title.setMaxWidth(Double.MAX_VALUE);  // Responsive title
        
        // Start Button - macOS green with shadow
        startButton = new Button("▶ Start Simulation");
        startButton.setMaxWidth(Double.MAX_VALUE);  // Chiếm toàn bộ width có sẵn
        startButton.setPrefHeight(36);
        startButton.setStyle("-fx-background-color: #34C759; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 13px; " +
                            "-fx-font-weight: 600; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.3), 6, 0, 0, 2);");
        
        // Hover effect for Start button
        startButton.setOnMouseEntered(e -> {
            startButton.setStyle("-fx-background-color: #30B350; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 13px; " +
                                "-fx-font-weight: 600; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.4), 10, 0, 0, 2); " +
                                "-fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });
        startButton.setOnMouseExited(e -> {
            startButton.setStyle("-fx-background-color: #34C759; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 13px; " +
                                "-fx-font-weight: 600; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.3), 6, 0, 0, 2);");
        });
        
        // Time Label - secondary text
        timeLabel = new Label("Time: 0s");
        timeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #86868B;");
        
        // Thêm vào section
        section.getChildren().addAll(title, startButton, timeLabel);
        
        // Thêm section vào ControlPanel
        getChildren().add(section);
    }
    
    /**
     * PHẦN 2: Vehicle Injection & Control
     */
    private void createVehicleSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        VBox.setVgrow(section, Priority.ALWAYS);  // Grow vertically
        section.setStyle("-fx-background-color: #FFFFFF; " +
                         "-fx-padding: 14; " +
                         "-fx-border-radius: 10; " +
                         "-fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);");
        
        // Title
        Label title = new Label("🚗 Vehicle Injection");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        title.setMaxWidth(Double.MAX_VALUE);  // Responsive title
        
        // Edge Selection
        Label edgeLabel = new Label("Edge:");
        edgeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1D1D1F;");
        
        edgeComboBox = new ComboBox<>();
        edgeComboBox.getItems().addAll("Edge 1", "Edge 2", "Edge 3", "Edge 4");
        edgeComboBox.setValue("Edge 1");
        edgeComboBox.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        edgeComboBox.setPrefHeight(32);
        edgeComboBox.setStyle("-fx-font-size: 13px; " +
                             "-fx-border-color: #D1D1D6; " +
                             "-fx-border-radius: 6; " +
                             "-fx-background-radius: 6;");
        
        // Color Selection
        Label colorLabel = new Label("Color:");
        colorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1D1D1F;");
        
        colorComboBox = new ComboBox<>();
        colorComboBox.getItems().addAll("Red", "Blue", "Green", "Yellow", "White");
        colorComboBox.setValue("Red");
        colorComboBox.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        colorComboBox.setPrefHeight(32);
        colorComboBox.setStyle("-fx-font-size: 13px; " +
                               "-fx-border-color: #D1D1D6; " +
                               "-fx-border-radius: 6; " +
                               "-fx-background-radius: 6;");
        
        // Quantity Input
        Label quantityLabel = new Label("Quantity (1-50):");
        quantityLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1D1D1F;");
        
        quantityField = new TextField("1");
        quantityField.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        quantityField.setPrefHeight(32);
        quantityField.setStyle("-fx-font-size: 13px; " +
                               "-fx-border-color: #D1D1D6; " +
                               "-fx-border-radius: 6; " +
                               "-fx-background-radius: 6; " +
                               "-fx-padding: 8;");
        
        // Focus effect for TextField
        quantityField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                quantityField.setStyle("-fx-font-size: 13px; " +
                                      "-fx-border-color: #007AFF; " +
                                      "-fx-border-width: 2; " +
                                      "-fx-border-radius: 6; " +
                                      "-fx-background-radius: 6; " +
                                      "-fx-padding: 8; " +
                                      "-fx-effect: dropshadow(gaussian, rgba(0, 122, 255, 0.2), 6, 0, 0, 0);");
            } else {
                quantityField.setStyle("-fx-font-size: 13px; " +
                                      "-fx-border-color: #D1D1D6; " +
                                      "-fx-border-radius: 6; " +
                                      "-fx-background-radius: 6; " +
                                      "-fx-padding: 8;");
            }
        });
        
        // Inject Button - macOS blue
        injectButton = new Button("➕ Inject Vehicles");
        injectButton.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        injectButton.setPrefHeight(32);
        injectButton.setStyle("-fx-background-color: #007AFF; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 13px; " +
                             "-fx-font-weight: 600; " +
                             "-fx-border-radius: 8; " +
                             "-fx-background-radius: 8; " +
                             "-fx-cursor: hand;");
        
        // Hover effect
        injectButton.setOnMouseEntered(e -> {
            injectButton.setStyle("-fx-background-color: #0051D5; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-size: 13px; " +
                                 "-fx-font-weight: 600; " +
                                 "-fx-border-radius: 8; " +
                                 "-fx-background-radius: 8; " +
                                 "-fx-cursor: hand; " +
                                 "-fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });
        injectButton.setOnMouseExited(e -> {
            injectButton.setStyle("-fx-background-color: #007AFF; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-size: 13px; " +
                                 "-fx-font-weight: 600; " +
                                 "-fx-border-radius: 8; " +
                                 "-fx-background-radius: 8; " +
                                 "-fx-cursor: hand;");
        });
        
        // Thêm vào section
        section.getChildren().addAll(title, edgeLabel, edgeComboBox, colorLabel, colorComboBox, quantityLabel, quantityField, injectButton);
        
        getChildren().add(section);
    }
    
    /**
     * PHẦN 3: Traffic Light Management
     */
    private void createTrafficLightManagementSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        VBox.setVgrow(section, Priority.ALWAYS);  // Grow vertically
        section.setStyle("-fx-background-color: #FFFFFF; " +
                         "-fx-padding: 12; " +
                         "-fx-border-radius: 10; " +
                         "-fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);");
        
        // Title
        Label title = new Label("🚦 Traffic Light Management");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        title.setMaxWidth(Double.MAX_VALUE);  // Responsive title
        section.getChildren().add(title);
        
        // Tạo 4 cột đèn giao thông
        section.getChildren().add(createTrafficLightBox("TL-01"));
        section.getChildren().add(createTrafficLightBox("TL-02"));
        section.getChildren().add(createTrafficLightBox("TL-03"));
        section.getChildren().add(createTrafficLightBox("TL-04"));
        
        getChildren().add(section);
    }
    
    /**
     * Tạo một box cho 1 cột đèn
     * Học: Method tái sử dụng (Reusable Code)
     * 
     * @param lightName - Tên cột đèn (VD: "TL-01")
     * @return HBox chứa tên + 3 buttons màu
     */
    private HBox createTrafficLightBox(String lightName) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        box.setStyle("-fx-background-color: #F5F5F7; " +
                     "-fx-padding: 6; " +
                     "-fx-border-radius: 6; " +
                     "-fx-background-radius: 6;");
        
        // Tên cột đèn
        Label nameLabel = new Label(lightName);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);  // Chiếm hết không gian còn lại
        HBox.setHgrow(nameLabel, Priority.ALWAYS);  // Đẩy buttons sang phải
        
        // Container cho 3 buttons (căn phải)
        HBox buttonsBox = new HBox(4);  // Spacing 4px giữa các button
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        
        // Button Xanh (Green) - Modern traffic light style
        Button greenBtn = new Button("");
        greenBtn.setPrefSize(16, 16);
        greenBtn.setMinSize(16, 16);
        greenBtn.setMaxSize(16, 16);
        greenBtn.setStyle("-fx-background-color: #34C759; " +
                         "-fx-border-radius: 8; " +
                         "-fx-background-radius: 8; " +
                         "-fx-cursor: hand; " +
                         "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.4), 4, 0, 0, 1);");
        
        // Button Vàng (Yellow)
        Button yellowBtn = new Button("");
        yellowBtn.setPrefSize(16, 16);
        yellowBtn.setMinSize(16, 16);
        yellowBtn.setMaxSize(16, 16);
        yellowBtn.setStyle("-fx-background-color: #FFCC00; " +
                          "-fx-border-radius: 8; " +
                          "-fx-background-radius: 8; " +
                          "-fx-cursor: hand; " +
                          "-fx-effect: dropshadow(gaussian, rgba(255, 204, 0, 0.4), 4, 0, 0, 1);");
        
        // Button Đỏ (Red)
        Button redBtn = new Button("");
        redBtn.setPrefSize(16, 16);
        redBtn.setMinSize(16, 16);
        redBtn.setMaxSize(16, 16);
        redBtn.setStyle("-fx-background-color: #FF3B30; " +
                       "-fx-border-radius: 8; " +
                       "-fx-background-radius: 8; " +
                       "-fx-cursor: hand; " +
                       "-fx-effect: dropshadow(gaussian, rgba(255, 59, 48, 0.4), 4, 0, 0, 1);");
        
        // Thêm buttons vào container
        buttonsBox.getChildren().addAll(greenBtn, yellowBtn, redBtn);
        
        // Thêm vào HBox chính
        box.getChildren().addAll(nameLabel, buttonsBox);
        
        return box;
    }
    
    // ===== GETTER METHODS (Encapsulation) =====
    // Cho phép class khác truy cập các thành phần
    
    public Button getStartButton() {
        return startButton;
    }
    
    public Label getTimeLabel() {
        return timeLabel;
    }
    
    public ComboBox<String> getEdgeComboBox() {
        return edgeComboBox;
    }
    
    public ComboBox<String> getColorComboBox() {
        return colorComboBox;
    }
    
    public Button getInjectButton() {
        return injectButton;
    }
    
    // ===== HELPER METHODS =====
    // Methods tiện ích để làm việc dễ hơn
    
    /**
     * Cập nhật thời gian hiển thị
     */
    public void updateTime(int seconds) {
        timeLabel.setText("Time: " + seconds + "s");
    }
    
    /**
     * Lấy Edge đã chọn
     */
    public String getSelectedEdge() {
        return edgeComboBox.getValue();
    }
    
    /**
     * Lấy Color đã chọn
     */
    public String getSelectedColor() {
        return colorComboBox.getValue();
    }
}
