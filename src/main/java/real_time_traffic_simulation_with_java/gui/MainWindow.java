package real_time_traffic_simulation_with_java.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * MainWindow - Cửa sổ chính
 * Chia 3 phần: Trái (ControlPanel), Giữa (MapPanel), Phải (Dashboard)
 */
public class MainWindow extends Application {
    
    private SimulationEngine simulationEngine;
    
    @Override
    public void start(Stage stage) {
        try {
            // Khởi tạo SimulationEngine
            System.out.println("🚀 Initializing SimulationEngine...");
            simulationEngine = new SimulationEngine();
            System.out.println("✅ SimulationEngine initialized!");
            
            // Tạo giao diện
            initializeGui(stage);
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeGui(Stage stage) throws Exception {
        // Tạo 3 panels
        ControlPanel leftPanel = new ControlPanel();
        MapPanel centerPanel = new MapPanel();
        Dashboard rightPanel = new Dashboard();
        
        // Set SimulationEngine for panels
        leftPanel.setSimulationEngine(simulationEngine);
        centerPanel.setSimulationEngine(simulationEngine);
        
        // Wrap panels trong ScrollPane
        ScrollPane leftScroll = new ScrollPane(leftPanel);
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        ScrollPane rightScroll = new ScrollPane(rightPanel);
        rightScroll.setFitToWidth(true);
        rightScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Tạo layout
        BorderPane root = new BorderPane();
        root.setLeft(leftScroll);
        root.setCenter(centerPanel);
        root.setRight(rightScroll);
        root.setStyle("-fx-background-color: #F5F5F7;");
        
        // Tạo scene
        Scene scene = new Scene(root, 1200, 700);
        scene.setFill(Color.web("#F5F5F7"));
        
        stage.setTitle("Real Time SUMO Traffic Simulation");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        
        // Handle stage close
        stage.setOnCloseRequest(event -> {
            System.out.println("🛑 Closing application...");
            try {
                if (simulationEngine != null) {
                    simulationEngine.stopSimulation();
                }
            } catch (Exception e) {
                System.err.println("Error closing simulation: " + e.getMessage());
            }
            System.exit(0);
        });
        
        stage.show();
        
        // Animation loop - cập nhật simulation
        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            private long last = 0;
            private static final long INTERVAL = 200_000_000; // 200ms
            private boolean wasRunning = false;
            
            @Override
            public void handle(long now) {
                if (now - last < INTERVAL) return;
                last = now;
                
                boolean isRunning = leftPanel.isSimulationRunning();
                if (isRunning != wasRunning) {
                    System.out.println(isRunning ? "▶️  Simulation STARTED" : "⏸️  Simulation STOPPED");
                    wasRunning = isRunning;
                }
                
                try {
                    // Only step if simulation is running
                    if (isRunning) {
                        simulationEngine.stepSimulation();
                        // Update time display after simulation step
                        leftPanel.updateTime((int) simulationEngine.getCurrentTime());
                    }
                    
                    // Update map display
                    try {
                        centerPanel.updateDisplay();
                    } catch (Exception e) {
                        System.err.println("⚠️  updateDisplay error: " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("Error in animation loop: " + e.getMessage());
                }
            }
        };
        timer.start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
