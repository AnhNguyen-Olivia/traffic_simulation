package real_time_traffic_simulation_with_java.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * ControlPanel - Left side control panel
 * Contains 3 sections: Connection, Vehicle Injection, Traffic Light Management
 */
public class ControlPanel extends VBox {
    
    // Section 1: Connection controls
    private Button startButton;
    private Label timeLabel;
    private SimulationEngine simulationEngine;
    private boolean isSimulationRunning = false;
    
    // Section 2: Vehicle injection controls
    private ComboBox<String> startEdgeComboBox;
    private ComboBox<String> endEdgeComboBox;
    private ComboBox<String> colorComboBox;
    private TextField quantityField;
    private Button injectButton;
    
    // Section 3: Traffic light controls
    private ComboBox<String> trafficLightComboBox;
    
    public ControlPanel() {
        // Setup main panel
        setPadding(new Insets(12));
        setMinWidth(220);
        setPrefWidth(240);
        setMaxWidth(280);
        setSpacing(12);
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #FFFFFF; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 2, 0);");
        
        // Create 3 sections
        createConnectSection();
        getChildren().add(new Separator());
        
        createVehicleSection();
        getChildren().add(new Separator());
        
        createTrafficLightManagementSection();
    }
    
    /**
     * Section 1: Connection to SUMO
     */
    private void createConnectSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(section, Priority.ALWAYS);
        section.setStyle("-fx-background-color: #FFFFFF; " +
                         "-fx-padding: 14; " +
                         "-fx-border-radius: 10; " +
                         "-fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);");
        
        // Title
        Label title = new Label("🔗 Connection");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        title.setMaxWidth(Double.MAX_VALUE);
        
        // Start Button
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
     * Section 2: Vehicle Injection
     */
    private void createVehicleSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(section, Priority.ALWAYS);
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
        
        // Inject Button
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
     * Section 3: Traffic Light Management
     */
    private void createTrafficLightManagementSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(section, Priority.ALWAYS);
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
    

    
    // Getter methods
    
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
     * Set SimulationEngine and connect buttons
     */
    public void setSimulationEngine(SimulationEngine engine) {
        this.simulationEngine = engine;
        
        startButton.setOnAction(e -> toggleSimulation());
        injectButton.setOnAction(e -> handleInjectVehicles());
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
    
    // Helper methods
    
    /**
     * Update time display
     */
    public void updateTime(int seconds) {
        timeLabel.setText("Time: " + seconds + "s");
    }
    
    /**
     * Get selected start edge
     */
    public String getSelectedStartEdge() {
        return startEdgeComboBox.getValue();
    }
    
    /**
     * Get selected end edge
     */
    public String getSelectedEndEdge() {
        return endEdgeComboBox.getValue();
    }
    
    /**
     * Populate edge list from SUMO
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
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get selected color
     */
    public String getSelectedColor() {
        return colorComboBox.getValue();
    }
    
    /**
     * Check if simulation is running
     */
    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }
    
    /**
     * Handle vehicle injection
     */
    private void handleInjectVehicles() {
        if (simulationEngine == null) return;
        
        try {
            String startEdge = startEdgeComboBox.getValue();
            String endEdge = endEdgeComboBox.getValue();
            String color = colorComboBox.getValue();
            String quantityText = quantityField.getText();
            
            if (startEdge == null || startEdge.isEmpty()) return;
            if (endEdge == null || endEdge.isEmpty()) return;
            
            if (color == null || color.isEmpty()) {
                color = "White";
            }
            
            int quantity;
            try {
                quantity = Integer.parseInt(quantityText);
                if (quantity < 1 || quantity > 50) return;
            } catch (NumberFormatException e) {
                return;
            }
            
            simulationEngine.injectVehicle(quantity, startEdge, endEdge, color);
            
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handle toggle single traffic light
     */
    private void handleToggleSingleTrafficLight() {
        try {
            String selectedTL = trafficLightComboBox.getValue();
            if (selectedTL == null || selectedTL.isEmpty()) return;
            
            simulationEngine.toggleSingleTl(selectedTL);
            
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
