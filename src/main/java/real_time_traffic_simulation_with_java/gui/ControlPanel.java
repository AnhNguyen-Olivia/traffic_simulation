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
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

public class ControlPanel extends VBox {
    
    // PHẦN 1: Connect to SUMO - Attributes (Biến)
    private Button startButton;
    private Label timeLabel;
    private SimulationEngine simulationEngine;
    private boolean isSimulationRunning = false;
    
    // PHẦN 2: Vehicle Injection - Attributes
    private ComboBox<String> startEdgeComboBox;
    private ComboBox<String> endEdgeComboBox;
    private ComboBox<String> colorComboBox;
    private TextField quantityField;  // Nhập số lượng xe
    private Button injectButton;
    
    // PHẦN 3: Traffic Light Management - Attributes
    private ComboBox<String> trafficLightComboBox;
    
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
        
        // Start Edge Selection
        Label startEdgeLabel = new Label("Start Edge:");
        startEdgeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1D1D1F;");
        
        startEdgeComboBox = new ComboBox<>();
        startEdgeComboBox.setPromptText("Select start edge...");
        startEdgeComboBox.setMaxWidth(Double.MAX_VALUE);
        startEdgeComboBox.setPrefHeight(32);
        startEdgeComboBox.setStyle("-fx-font-size: 13px; " +
                             "-fx-border-color: #D1D1D6; " +
                             "-fx-border-radius: 6; " +
                             "-fx-background-radius: 6;");
        
        // End Edge Selection
        Label endEdgeLabel = new Label("End Edge:");
        endEdgeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1D1D1F;");
        
        endEdgeComboBox = new ComboBox<>();
        endEdgeComboBox.setPromptText("Select end edge...");
        endEdgeComboBox.setMaxWidth(Double.MAX_VALUE);
        endEdgeComboBox.setPrefHeight(32);
        endEdgeComboBox.setStyle("-fx-font-size: 13px; " +
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
        section.getChildren().addAll(title, startEdgeLabel, startEdgeComboBox, endEdgeLabel, endEdgeComboBox, colorLabel, colorComboBox, quantityLabel, quantityField, injectButton);
        
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
        
        // ComboBox to select traffic light
        trafficLightComboBox = new ComboBox<>();
        trafficLightComboBox.setPromptText("Select Traffic Light");
        trafficLightComboBox.setMaxWidth(Double.MAX_VALUE);
        trafficLightComboBox.setStyle("-fx-font-size: 12px;");
        section.getChildren().add(trafficLightComboBox);
        
        // Toggle Single Button
        Button toggleSingleButton = new Button("Toggle Selected");
        toggleSingleButton.setMaxWidth(Double.MAX_VALUE);
        toggleSingleButton.setStyle("-fx-font-size: 12px; " +
                                   "-fx-background-color: #FF9500; " +
                                   "-fx-text-fill: white; " +
                                   "-fx-font-weight: 600; " +
                                   "-fx-padding: 8; " +
                                   "-fx-border-radius: 6; " +
                                   "-fx-background-radius: 6; " +
                                   "-fx-cursor: hand;");
        toggleSingleButton.setOnAction(e -> handleToggleSingleTrafficLight());
        section.getChildren().add(toggleSingleButton);
        
        // Toggle All Button
        Button toggleAllButton = new Button("Toggle All Traffic Lights");
        toggleAllButton.setMaxWidth(Double.MAX_VALUE);
        toggleAllButton.setStyle("-fx-font-size: 12px; " +
                                "-fx-background-color: #FF3B30; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-weight: 600; " +
                                "-fx-padding: 8; " +
                                "-fx-border-radius: 6; " +
                                "-fx-background-radius: 6; " +
                                "-fx-cursor: hand;");
        toggleAllButton.setOnAction(e -> handleToggleAllTrafficLights());
        section.getChildren().add(toggleAllButton);
        
        getChildren().add(section);
    }
    

    
    // ===== GETTER METHODS (Encapsulation) =====
    // Cho phép class khác truy cập các thành phần
    
    public Button getStartButton() {
        return startButton;
    }
    
    public Label getTimeLabel() {
        return timeLabel;
    }
    
    public ComboBox<String> getStartEdgeComboBox() {
        return startEdgeComboBox;
    }
    
    public ComboBox<String> getEndEdgeComboBox() {
        return endEdgeComboBox;
    }
    
    public ComboBox<String> getColorComboBox() {
        return colorComboBox;
    }
    
    public Button getInjectButton() {
        return injectButton;
    }
    
    /**
     * Set SimulationEngine và kết nối với Start button
     */
    public void setSimulationEngine(SimulationEngine engine) {
        this.simulationEngine = engine;
        
        // Kết nối Start button với SimulationEngine
        startButton.setOnAction(e -> toggleSimulation());
        
        // Kết nối Inject button với SimulationEngine
        injectButton.setOnAction(e -> handleInjectVehicles());
        
        // Load edge list and traffic light list
        populateEdgeList();
        populateTrafficLightList();
    }
    
    /**
     * Toggle simulation (Start/Stop)
     */
    private void toggleSimulation() {
        if (simulationEngine == null) return;
        
        if (isSimulationRunning) {
            // Stop animation timer (keep SUMO connection alive)
            isSimulationRunning = false;
            
            // Update button style
            startButton.setText("▶ Start Simulation");
            startButton.setStyle("-fx-background-color: #34C759; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 13px; " +
                                "-fx-font-weight: 600; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.3), 6, 0, 0, 2);");
        } else {
            // Start animation timer - will call stepSimulation() automatically
            isSimulationRunning = true;
            
            // Update button style to red (Stop)
            startButton.setText("⏸ Stop Simulation");
            startButton.setStyle("-fx-background-color: #FF3B30; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 13px; " +
                                "-fx-font-weight: 600; " +
                                "-fx-border-radius: 8; " +
                                "-fx-background-radius: 8; " +
                                "-fx-cursor: hand; " +
                                "-fx-effect: dropshadow(gaussian, rgba(255, 59, 48, 0.3), 6, 0, 0, 2);");
        }
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
     * Lấy Start Edge đã chọn
     */
    public String getSelectedStartEdge() {
        return startEdgeComboBox.getValue();
    }
    
    /**
     * Lấy End Edge đã chọn
     */
    public String getSelectedEndEdge() {
        return endEdgeComboBox.getValue();
    }
    
    /**
     * Populate edge list từ SimulationEngine
     */
    public void populateEdgeList() {
        try {
            if (simulationEngine != null) {
                var edges = simulationEngine.getAllEdgeIDs();
                startEdgeComboBox.getItems().clear();
                endEdgeComboBox.getItems().clear();
                startEdgeComboBox.getItems().addAll(edges);
                endEdgeComboBox.getItems().addAll(edges);
                
                // Set default values
                if (!edges.isEmpty()) {
                    startEdgeComboBox.setValue(edges.get(0));
                    endEdgeComboBox.setValue(edges.size() > 1 ? edges.get(1) : edges.get(0));
                }
                
                System.out.println("✅ Loaded " + edges.size() + " edges into ComboBox");
            }
        } catch (Exception e) {
            System.err.println("⚠️  Error loading edges: " + e.getMessage());
        }
    }
    
    /**
     * Lấy Color đã chọn
     */
    public String getSelectedColor() {
        return colorComboBox.getValue();
    }
    
    /**
     * Check xem simulation đang chạy hay không
     */
    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }
    
    /**
     * Xử lý inject xe vào simulation
     */
    private void handleInjectVehicles() {
        if (simulationEngine == null) {
            System.err.println("⚠️  SimulationEngine chưa được khởi tạo!");
            return;
        }
        
        try {
            // Lấy giá trị từ UI
            String startEdge = startEdgeComboBox.getValue();
            String endEdge = endEdgeComboBox.getValue();
            String color = colorComboBox.getValue();
            String quantityText = quantityField.getText();
            
            // Validate input
            if (startEdge == null || startEdge.isEmpty()) {
                System.err.println("⚠️  Vui lòng chọn Start Edge!");
                return;
            }
            
            if (endEdge == null || endEdge.isEmpty()) {
                System.err.println("⚠️  Vui lòng chọn End Edge!");
                return;
            }
            
            if (color == null || color.isEmpty()) {
                color = "White";  // Default color
            }
            
            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
                if (quantity < 1 || quantity > 50) {
                    System.err.println("⚠️  Số lượng xe phải từ 1-50!");
                    return;
                }
            } catch (NumberFormatException e) {
                System.err.println("⚠️  Số lượng xe không hợp lệ!");
                return;
            }
            
            // Inject vehicles
            simulationEngine.injectVehicle(quantity, startEdge, endEdge, color);
            System.out.println("✅ Injected " + quantity + " " + color + " vehicle(s) from " + startEdge + " to " + endEdge);
            
        } catch (Exception e) {
            System.err.println("⚠️  Error injecting vehicles: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Populate traffic light list from SUMO
     */
    private void populateTrafficLightList() {
        try {
            java.util.List<String> tlIDs = simulationEngine.getAllTrafficLightIDs();
            trafficLightComboBox.getItems().clear();
            trafficLightComboBox.getItems().addAll(tlIDs);
            System.out.println("✅ Loaded " + tlIDs.size() + " traffic lights");
        } catch (Exception e) {
            System.err.println("⚠️  Error loading traffic lights: " + e.getMessage());
        }
    }
    
    /**
     * Handle toggle single traffic light
     */
    private void handleToggleSingleTrafficLight() {
        try {
            String selectedTL = trafficLightComboBox.getValue();
            if (selectedTL == null || selectedTL.isEmpty()) {
                System.out.println("⚠️  Please select a traffic light");
                return;
            }
            
            simulationEngine.toggleSingleTl(selectedTL);
            System.out.println("✅ Toggled traffic light: " + selectedTL);
            
        } catch (Exception e) {
            System.err.println("⚠️  Error toggling traffic light: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle toggle all traffic lights
     */
    private void handleToggleAllTrafficLights() {
        try {
            java.util.List<String> allTLs = simulationEngine.getAllTrafficLightIDs();
            for (String tlID : allTLs) {
                simulationEngine.toggleSingleTl(tlID);
            }
            System.out.println("✅ Toggled all " + allTLs.size() + " traffic lights");
            
        } catch (Exception e) {
            System.err.println("⚠️  Error toggling all traffic lights: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
