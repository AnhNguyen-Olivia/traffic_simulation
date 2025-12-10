package gui;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/**
 * SimulationController - Backend logic điều khiển simulation
 * Học: Tách logic ra khỏi UI (MVC Pattern - Model View Controller)
 */
public class SimulationController {
    
    // Attributes
    private ControlPanel controlPanel;  // Tham chiếu đến UI
    private Timeline timeline;          // Timer của JavaFX
    private int currentTime;            // Thời gian hiện tại (giây)
    private boolean isRunning;          // Trạng thái đang chạy?
    
    /**
     * Constructor
     * @param controlPanel - Panel UI để cập nhật
     */
    public SimulationController(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        this.currentTime = 0;
        this.isRunning = false;
        
        // Tạo Timeline (timer chạy mỗi 1 giây)
        createTimeline();
        
        // Gắn sự kiện cho buttons
        setupButtonActions();
    }
    
    /**
     * Tạo Timeline - Timer đếm mỗi giây
     */
    private void createTimeline() {
        timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                // Chạy mỗi 1 giây
                currentTime++;
                controlPanel.updateTime(currentTime);
            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);  // Chạy vô hạn
    }
    
    /**
     * Setup sự kiện cho buttons
     * Học: Event Handling - Xử lý sự kiện khi click button
     */
    private void setupButtonActions() {
        // Lấy button từ ControlPanel
        controlPanel.getStartButton().setOnAction(event -> {
            if (!isRunning) {
                startSimulation();
            } else {
                stopSimulation();
            }
        });
    }
    
    /**
     * Bắt đầu simulation
     */
    private void startSimulation() {
        isRunning = true;
        timeline.play();  // Bắt đầu timer
        
        // Đổi button thành Stop
        controlPanel.getStartButton().setText("⏸ Stop");
        controlPanel.getStartButton().setStyle(
            "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px;"
        );
        
        System.out.println("✅ Simulation Started!");  // Log để test
    }
    
    /**
     * Dừng simulation
     */
    private void stopSimulation() {
        isRunning = false;
        timeline.pause();  // Dừng timer (không reset)
        
        // Đổi button thành Start
        controlPanel.getStartButton().setText("▶ Start");
        controlPanel.getStartButton().setStyle(
            "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px;"
        );
        
        System.out.println("⏸ Simulation Stopped!");
    }
    
    /**
     * Reset simulation về 0
     */
    public void resetSimulation() {
        stopSimulation();
        currentTime = 0;
        controlPanel.updateTime(currentTime);
    }
    
    /**
     * Getter cho trạng thái
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    public int getCurrentTime() {
        return currentTime;
    }
}
