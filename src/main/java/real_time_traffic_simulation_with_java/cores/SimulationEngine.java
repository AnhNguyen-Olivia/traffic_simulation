package real_time_traffic_simulation_with_java.cores;

import javafx.animation.AnimationTimer;
import real_time_traffic_simulation_with_java.gui.MapPanel;
import real_time_traffic_simulation_with_java.wrapper.SumoTraasConnection;

/**
 * SimulationEngine - Quản lý vòng lặp simulation của SUMO
 * Sử dụng JavaFX AnimationTimer để update UI mượt mà
 */
public class SimulationEngine {
    private boolean running = false;
    private final SumoTraasConnection connection;
    private final MapPanel mapPanel;
    private AnimationTimer simulationLoop;
    
    // Callback để update time label
    private Runnable onTimeUpdate;
    
    /**
     * Constructor
     */
    public SimulationEngine(SumoTraasConnection connection, MapPanel mapPanel) {
        this.connection = connection;
        this.mapPanel = mapPanel;
        setupSimulationLoop();
    }
    
    /**
     * Setup simulation loop với AnimationTimer
     */
    private void setupSimulationLoop() {
        simulationLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            private static final long UPDATE_INTERVAL = 100_000_000; // 100ms = 0.1s
            
            @Override
            public void handle(long now) {
                if (!running) return;
                
                // Throttle updates to ~10 FPS
                if (now - lastUpdate >= UPDATE_INTERVAL) {
                    try {
                        // Tiến SUMO 1 step
                        connection.nextStep();
                        
                        // Update vehicles trên map
                        mapPanel.updateVehicles();
                        
                        // Callback to update time label
                        if (onTimeUpdate != null) {
                            onTimeUpdate.run();
                        }
                        
                        lastUpdate = now;
                    } catch (Exception e) {
                        System.err.println("❌ Error in simulation loop: " + e.getMessage());
                        e.printStackTrace();
                        stop();
                    }
                }
            }
        };
    }
    
    /**
     * Bắt đầu simulation
     */
    public void start() {
        if (!running) {
            running = true;
            simulationLoop.start();
            System.out.println("▶️  Simulation started!");
        }
    }
    
    /**
     * Dừng simulation
     */
    public void stop() {
        if (running) {
            running = false;
            simulationLoop.stop();
            System.out.println("⏸️  Simulation stopped!");
        }
    }
    
    /**
     * Kiểm tra xem simulation có đang chạy không
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Lấy thời gian hiện tại của simulation
     */
    public double getCurrentTime() {
        try {
            return connection.getCurrentStep();
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Set callback để update time
     */
    public void setOnTimeUpdate(Runnable callback) {
        this.onTimeUpdate = callback;
    }
}
