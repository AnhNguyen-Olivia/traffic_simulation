package real_time_traffic_simulation_with_java.gui;

import java.util.List;

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
    private Button startButton;
    private Label timeLabel;
    
    private ComboBox<String> edgeComboBox;
    private ComboBox<String> colorComboBox;
    private TextField quantityField;  
    private Button injectButton;
    
    private VehicleInjectListener injectListener;
    
    public ControlPanel() {
        setPadding(new Insets(12));
        setMinWidth(220);   
        setPrefWidth(240);  
        setMaxWidth(280);   
        setSpacing(12);     
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #FFFFFF; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 2, 0);");
        
        createConnectSection();  
        getChildren().add(new Separator());  
        
        createVehicleSection();      
        getChildren().add(new Separator());
        
        createTrafficLightManagementSection(); 
    }
    
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
        
        Label title = new Label("🔗 Connect to SUMO");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        title.setMaxWidth(Double.MAX_VALUE);  
        
        startButton = new Button("▶ Start Simulation");
        startButton.setMaxWidth(Double.MAX_VALUE);  
        startButton.setPrefHeight(36);
        startButton.setStyle("-fx-background-color: #34C759; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 13px; " +
                            "-fx-font-weight: 600; " +
                            "-fx-border-radius: 8; " +
                            "-fx-background-radius: 8; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.3), 6, 0, 0, 2);");
        
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
        
        timeLabel = new Label("Time: 0s");
        timeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #86868B;");
        
        section.getChildren().addAll(title, startButton, timeLabel);
        
        getChildren().add(section);
    }
    
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
        
        Label title = new Label("🚗 Vehicle Injection");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        title.setMaxWidth(Double.MAX_VALUE);  
        
        Label edgeLabel = new Label("Edge:");
        edgeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1D1D1F;");
        
        edgeComboBox = new ComboBox<>();
        edgeComboBox.getItems().addAll("Edge 1", "Edge 2", "Edge 3", "Edge 4");
        edgeComboBox.setValue("Edge 1");
        edgeComboBox.setMaxWidth(Double.MAX_VALUE);  
        edgeComboBox.setPrefHeight(32);
        edgeComboBox.setStyle("-fx-font-size: 13px; " +
                             "-fx-border-color: #D1D1D6; " +
                             "-fx-border-radius: 6; " +
                             "-fx-background-radius: 6;");
        
        Label colorLabel = new Label("Color:");
        colorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1D1D1F;");
        
        colorComboBox = new ComboBox<>();
        colorComboBox.getItems().addAll("Red", "Blue", "Green", "Yellow", "White");
        colorComboBox.setValue("Red");
        colorComboBox.setMaxWidth(Double.MAX_VALUE); 
        colorComboBox.setPrefHeight(32);
        colorComboBox.setStyle("-fx-font-size: 13px; " +
                               "-fx-border-color: #D1D1D6; " +
                               "-fx-border-radius: 6; " +
                               "-fx-background-radius: 6;");
        
        Label quantityLabel = new Label("Quantity (1-50):");
        quantityLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1D1D1F;");
        
        quantityField = new TextField("1");
        quantityField.setMaxWidth(Double.MAX_VALUE); 
        quantityField.setPrefHeight(32);
        quantityField.setStyle("-fx-font-size: 13px; " +
                               "-fx-border-color: #D1D1D6; " +
                               "-fx-border-radius: 6; " +
                               "-fx-background-radius: 6; " +
                               "-fx-padding: 8;");
        
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
        
        injectButton = new Button("➕ Inject Vehicles");
        injectButton.setMaxWidth(Double.MAX_VALUE);  
        injectButton.setPrefHeight(32);
        injectButton.setStyle("-fx-background-color: #007AFF; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 13px; " +
                             "-fx-font-weight: 600; " +
                             "-fx-border-radius: 8; " +
                             "-fx-background-radius: 8; " +
                             "-fx-cursor: hand;");
        
        injectButton.setOnAction(e -> {
            if (injectListener == null) return;

            int qty = 1;
            try {
                qty = Integer.parseInt(quantityField.getText().trim());
                qty = Math.max(1, Math.min(50, qty));
            } catch (Exception ex) {
                qty = 1;
            }

            injectListener.onInject(
                getSelectedEdge(),
                getSelectedColor(),
                qty
            );
        });
        
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
        
        section.getChildren().addAll(title, edgeLabel, edgeComboBox, colorLabel, colorComboBox, quantityLabel, quantityField, injectButton);
        
        getChildren().add(section);
    }
    
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
        
        Label title = new Label("🚦 Traffic Light Management");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        title.setMaxWidth(Double.MAX_VALUE);  
        section.getChildren().add(title);
        
        section.getChildren().add(createTrafficLightBox("TL-01"));
        section.getChildren().add(createTrafficLightBox("TL-02"));
        section.getChildren().add(createTrafficLightBox("TL-03"));
        section.getChildren().add(createTrafficLightBox("TL-04"));
        
        getChildren().add(section);
    }
    
    private HBox createTrafficLightBox(String lightName) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(Double.MAX_VALUE);  
        box.setStyle("-fx-background-color: #F5F5F7; " +
                     "-fx-padding: 6; " +
                     "-fx-border-radius: 6; " +
                     "-fx-background-radius: 6;");
        
        Label nameLabel = new Label(lightName);
        nameLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);  
        HBox.setHgrow(nameLabel, Priority.ALWAYS);  

        HBox buttonsBox = new HBox(4);  
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button greenBtn = new Button("");
        greenBtn.setPrefSize(16, 16);
        greenBtn.setMinSize(16, 16);
        greenBtn.setMaxSize(16, 16);
        greenBtn.setStyle("-fx-background-color: #34C759; " +
                         "-fx-border-radius: 8; " +
                         "-fx-background-radius: 8; " +
                         "-fx-cursor: hand; " +
                         "-fx-effect: dropshadow(gaussian, rgba(52, 199, 89, 0.4), 4, 0, 0, 1);");
        
        Button yellowBtn = new Button("");
        yellowBtn.setPrefSize(16, 16);
        yellowBtn.setMinSize(16, 16);
        yellowBtn.setMaxSize(16, 16);
        yellowBtn.setStyle("-fx-background-color: #FFCC00; " +
                          "-fx-border-radius: 8; " +
                          "-fx-background-radius: 8; " +
                          "-fx-cursor: hand; " +
                          "-fx-effect: dropshadow(gaussian, rgba(255, 204, 0, 0.4), 4, 0, 0, 1);");
        
        Button redBtn = new Button("");
        redBtn.setPrefSize(16, 16);
        redBtn.setMinSize(16, 16);
        redBtn.setMaxSize(16, 16);
        redBtn.setStyle("-fx-background-color: #FF3B30; " +
                       "-fx-border-radius: 8; " +
                       "-fx-background-radius: 8; " +
                       "-fx-cursor: hand; " +
                       "-fx-effect: dropshadow(gaussian, rgba(255, 59, 48, 0.4), 4, 0, 0, 1);");
        
        buttonsBox.getChildren().addAll(greenBtn, yellowBtn, redBtn);
        
        box.getChildren().addAll(nameLabel, buttonsBox);
        
        return box;
    }
    
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
    
    public void updateTime(int seconds) {
        timeLabel.setText("Time: " + seconds + "s");
    }
    
    public String getSelectedEdge() {
        return edgeComboBox.getValue();
    }
    
    public String getSelectedColor() {
        return colorComboBox.getValue();
    }
    
    public interface VehicleInjectListener {
        void onInject(String edgeId, String color, int quantity);
    }
    
    public void setVehicleInjectListener(VehicleInjectListener listener) {
        this.injectListener = listener;
    }
    
    public void setEdges(List<String> edgeIds) {
        edgeComboBox.getItems().setAll(edgeIds);
        if (!edgeIds.isEmpty()) {
            edgeComboBox.setValue(edgeIds.get(0));
        }
    }

}
