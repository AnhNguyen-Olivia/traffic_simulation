package real_time_traffic_simulation_with_java.gui;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * Dashboard - Panel thống kê bên phải
 * Hiển thị Statistics và Export Reports
 */
public class Dashboard extends VBox {
    
    // Reference to SimulationEngine for fetching real-time data
    private SimulationEngine simulationEngine;
    
    // Statistics Labels (updated with real data)
    private Label totalVehiclesLabel;
    private Label avgSpeedLabel;
    private Label avgTravelTimeLabel;
    private Label densityLabel;
    private Label simulationTimeLabel;
    
    // Export Filter Checkboxes
    private CheckBox redCheckBox;
    private CheckBox blueCheckBox;
    private CheckBox greenCheckBox;
    private CheckBox yellowCheckBox;
    private CheckBox whiteCheckBox;
    
    private CheckBox edge1CheckBox;
    private CheckBox edge2CheckBox;
    private CheckBox edge3CheckBox;
    private CheckBox edge4CheckBox;
    
    // Export Button
    private Button exportButton;
    
    /**
     * Constructor - Khởi tạo Dashboard
     */
    public Dashboard() {
        // macOS modern design với responsive
        setPadding(new Insets(12));
        setMinWidth(250);   // Chiều rộng tối thiểu
        setPrefWidth(280);  // Chiều rộng ưa thích
        setMaxWidth(320);   // Chiều rộng tối đa
        setSpacing(12);     // 12px spacing (giảm từ 16px)
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #FFFFFF; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, -2, 0);");
        
        // Tạo 2 phần
        createStatisticsSection();
        Separator sep = new Separator();
        sep.setMaxWidth(Double.MAX_VALUE);  // Responsive separator
        getChildren().add(sep);
        createExportSection();
    }
    
    /**
     * PHẦN 1: Statistics (Thống kê)
     */
    private void createStatisticsSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.TOP_LEFT);
        section.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        VBox.setVgrow(section, Priority.ALWAYS);  // Grow vertically
        section.setStyle("-fx-background-color: #FFFFFF; " +
                         "-fx-padding: 14; " +
                         "-fx-border-radius: 10; " +
                         "-fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);");
        
        // Title
        Label title = new Label("📊 Statistics");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        title.setMaxWidth(Double.MAX_VALUE);  // Responsive title
        
        // Metrics as individual cards - Responsive
        totalVehiclesLabel = new Label("🚗 Total Vehicles\n0");
        totalVehiclesLabel.setMaxWidth(Double.MAX_VALUE);  // Responsive
        totalVehiclesLabel.setStyle("-fx-font-size: 11px; " +
                                    "-fx-text-fill: #86868B; " +
                                    "-fx-background-color: #F5F5F7; " +
                                    "-fx-padding: 8; " +
                                    "-fx-border-radius: 6; " +
                                    "-fx-background-radius: 6;");
        
        densityLabel = new Label("📏 Density\n0.0 veh/km");
        densityLabel.setMaxWidth(Double.MAX_VALUE);  // Responsive
        densityLabel.setStyle("-fx-font-size: 11px; " +
                      "-fx-text-fill: #86868B; " +
                      "-fx-background-color: #F5F5F7; " +
                      "-fx-padding: 8; " +
                      "-fx-border-radius: 6; " +
                      "-fx-background-radius: 6;");
        avgSpeedLabel = new Label("⚡ Avg Speed\n0.0 km/h");
        avgSpeedLabel.setMaxWidth(Double.MAX_VALUE);  // Responsive
        avgSpeedLabel.setStyle("-fx-font-size: 11px; " +
                               "-fx-text-fill: #86868B; " +
                               "-fx-background-color: #F5F5F7; " +
                               "-fx-padding: 8; " +
                               "-fx-border-radius: 6; " +
                               "-fx-background-radius: 6;");
        
        avgTravelTimeLabel = new Label("⏱️ Avg Travel Time\n0.0 s");
        avgTravelTimeLabel.setMaxWidth(Double.MAX_VALUE);  // Responsive
        avgTravelTimeLabel.setStyle("-fx-font-size: 11px; " +
                                    "-fx-text-fill: #86868B; " +
                                    "-fx-background-color: #F5F5F7; " +
                                    "-fx-padding: 8; " +
                                    "-fx-border-radius: 6; " +
                                    "-fx-background-radius: 6;");
        
        simulationTimeLabel = new Label("⏰ Simulation Time\n0.0 s");
        simulationTimeLabel.setMaxWidth(Double.MAX_VALUE);  // Responsive
        simulationTimeLabel.setStyle("-fx-font-size: 11px; " +
                                     "-fx-text-fill: #86868B; " +
                                     "-fx-background-color: #F5F5F7; " +
                                     "-fx-padding: 8; " +
                                     "-fx-border-radius: 6; " +
                                     "-fx-background-radius: 6;");
        
        section.getChildren().addAll(
            title,
            totalVehiclesLabel,
            avgSpeedLabel,
            avgTravelTimeLabel,
            densityLabel,
            simulationTimeLabel
        );
        
        getChildren().add(section);
    }
    
    /**
     * PHẦN 2: Export Reports
     */
    private void createExportSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.TOP_LEFT);
        section.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        VBox.setVgrow(section, Priority.ALWAYS);  // Grow vertically
        section.setStyle("-fx-background-color: #FFFFFF; " +
                         "-fx-padding: 14; " +
                         "-fx-border-radius: 10; " +
                         "-fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2);");
        
        // Title
        Label title = new Label("📁 Export Reports");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        title.setMaxWidth(Double.MAX_VALUE);  // Responsive title
        
        // Filter by Color
        Label colorLabel = new Label("Filter by Color:");
        colorLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        
        // Color checkboxes - modern style
        HBox colorRow1 = new HBox(8);
        redCheckBox = new CheckBox("Red");
        redCheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D1D1F;");
        blueCheckBox = new CheckBox("Blue");
        blueCheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D1D1F;");
        greenCheckBox = new CheckBox("Green");
        greenCheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D1D1F;");
        colorRow1.getChildren().addAll(redCheckBox, blueCheckBox, greenCheckBox);
        
        HBox colorRow2 = new HBox(8);
        yellowCheckBox = new CheckBox("Yellow");
        yellowCheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D1D1F;");
        whiteCheckBox = new CheckBox("White");
        whiteCheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D1D1F;");
        colorRow2.getChildren().addAll(yellowCheckBox, whiteCheckBox);
        
        // Filter by Edge
        Label edgeLabel = new Label("Filter by Edge:");
        edgeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #1D1D1F;");
        
        // Edge checkboxes
        HBox edgeRow1 = new HBox(8);
        edge1CheckBox = new CheckBox("Edge 1");
        edge1CheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D1D1F;");
        edge2CheckBox = new CheckBox("Edge 2");
        edge2CheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D1D1F;");
        edgeRow1.getChildren().addAll(edge1CheckBox, edge2CheckBox);
        
        HBox edgeRow2 = new HBox(8);
        edge3CheckBox = new CheckBox("Edge 3");
        edge3CheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D1D1F;");
        edge4CheckBox = new CheckBox("Edge 4");
        edge4CheckBox.setStyle("-fx-font-size: 12px; -fx-text-fill: #1D1D1F;");
        edgeRow2.getChildren().addAll(edge3CheckBox, edge4CheckBox);
        
        // Export button - macOS style
        exportButton = new Button("📤 Export Data");
        exportButton.setMaxWidth(Double.MAX_VALUE);  // Responsive width
        exportButton.setPrefHeight(36);
        exportButton.setStyle("-fx-background-color: #007AFF; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 14px; " +
                             "-fx-font-weight: 600; " +
                             "-fx-border-radius: 8; " +
                             "-fx-background-radius: 8; " +
                             "-fx-cursor: hand;");
        
        // Hover effect
        exportButton.setOnMouseEntered(e -> {
            exportButton.setStyle("-fx-background-color: #0051D5; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-size: 14px; " +
                                 "-fx-font-weight: 600; " +
                                 "-fx-border-radius: 8; " +
                                 "-fx-background-radius: 8; " +
                                 "-fx-cursor: hand; " +
                                 "-fx-scale-x: 1.02; -fx-scale-y: 1.02;");
        });
        exportButton.setOnMouseExited(e -> {
            exportButton.setStyle("-fx-background-color: #007AFF; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-size: 14px; " +
                                 "-fx-font-weight: 600; " +
                                 "-fx-border-radius: 8; " +
                                 "-fx-background-radius: 8; " +
                                 "-fx-cursor: hand;");
        });
        
        // Event handler
        exportButton.setOnAction(e -> showExportDialog());
        
        section.getChildren().addAll(
            title,
            colorLabel,
            colorRow1,
            colorRow2,
            edgeLabel,
            edgeRow1,
            edgeRow2,
            exportButton
        );
        
        getChildren().add(section);
    }
    
    // ===== SETTER METHOD =====
    
    /**
     * Set SimulationEngine reference
     */
    public void setSimulationEngine(SimulationEngine engine) {
        this.simulationEngine = engine;
    }
    
    // ===== GETTER METHODS =====
    
    public Label getTotalVehiclesLabel() {
        return totalVehiclesLabel;
    }
    
    
    public Label getAvgSpeedLabel() {
        return avgSpeedLabel;
    }
    
    public Label getAvgTravelTimeLabel() {
        return avgTravelTimeLabel;
    }
    
    public Label getSimulationTimeLabel() {
        return simulationTimeLabel;
    }
    
    public Button getExportButton() {
        return exportButton;
    }
    
    // ===== UPDATE METHODS =====
    
    /**
     * Update all statistics with real data from SimulationEngine
     * This method fetches live data from SUMO and updates all labels
     */
    public void updateStatistics() {
        if (simulationEngine == null) {
            return;  // Engine not set yet, skip update
        }
        
        try {
            // 1. Get total vehicles (running only - vehicles that finished are not counted)
            int totalVehicles = simulationEngine.getVehicleCount();
            // 2. Get average speed across all edges (km/h)
            double avgSpeed = simulationEngine.getAverageSpeed();
            // 3. Get average travel time across all edges (seconds)
            double avgTravelTime = simulationEngine.getAverageTravelTime();
            // 4. Get average density across all edges (veh/km)
            double avgDensity = simulationEngine.getAverageDensity();
            // 5. Get current simulation time
            double simulationTime = simulationEngine.getCurrentTime();

            // Update all labels with formatted data
            totalVehiclesLabel.setText("🚗 Total Vehicles\n" + totalVehicles);
            avgSpeedLabel.setText("⚡ Avg Speed\n" + (avgSpeed > 0 ? String.format("%.1f", avgSpeed) : "-") + " km/h");
            avgTravelTimeLabel.setText("⏱️ Avg Travel Time\n" + (avgTravelTime > 0 ? String.format("%.1f", avgTravelTime) : "-") + " s");
            densityLabel.setText("📏 Density\n" + (avgDensity > 0 ? String.format("%.2f", avgDensity) : "-") + " veh/km");
            simulationTimeLabel.setText("⏰ Simulation Time\n" + String.format("%.1f", simulationTime) + " s");
        } catch (Exception e) {
            System.err.println("Error updating Dashboard statistics: " + e.getMessage());
        }
    }
    
    /**
     * Hiển thị dialog cho người dùng chọn định dạng export (CSV hoặc PDF)
     */
    private void showExportDialog() {
        // Tạo danh sách lựa chọn
        String csvOption = "CSV - Save statistics for external analysis";
        String pdfOption = "PDF - Generate summary with charts and metrics";
        
        // Tạo ChoiceDialog với 2 lựa chọn - không có default selection
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, csvOption, pdfOption);
        dialog.setTitle("Export Format");
        dialog.setHeaderText("Select export format:");
        dialog.setContentText("Choose format:");
        
        // Hiển thị dialog và lấy kết quả
        Optional<String> result = dialog.showAndWait();
        
        // Xử lý lựa chọn của người dùng
        result.ifPresent(choice -> {
            if (choice.equals(csvOption)) {
                exportToCSV();
            } else if (choice.equals(pdfOption)) {
                exportToPDF();
            }
        });
    }
    
    /**
     * Export dữ liệu simulation ra file CSV
     */
    private void exportToCSV() {
        System.out.println("📄 Exporting to CSV...");
        // TODO: Implement CSV export logic
        // - Lấy dữ liệu từ các filter checkboxes
        // - Tạo file CSV với các cột: timestamp, vehicle data, edge data, statistics
        // - Sử dụng FileChooser để người dùng chọn nơi lưu file
    }
    
    /**
     * Export dữ liệu simulation ra file PDF với charts và metrics
     */
    private void exportToPDF() {
        System.out.println("📊 Exporting to PDF...");
        // TODO: Implement PDF export logic
        // - Lấy dữ liệu từ các filter checkboxes
        // - Tạo PDF report với charts, metrics, timestamps
        // - Sử dụng FileChooser để người dùng chọn nơi lưu file
    }
}
