package real_time_traffic_simulation_with_java.gui;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * MapPanel - Hiển thị bản đồ từ SimulationEngine
 * 
 * Đơn giản: chỉ display Group đã vẽ sẵn từ SimulationEngine
 * SimulationEngine cung cấp:
 * - getMapEdges() - đường
 * - getMapTls() - cột đèn giao thông
 * - getMapVehicles() - xe
 * - getMapJunctions() - ngã tư
 */
public class MapPanel extends StackPane {
    private final Group mapContent = new Group();
    private SimulationEngine simulationEngine;

    /**
     * Constructor - khởi tạo với SimulationEngine
     */
    public MapPanel(SimulationEngine simulationEngine) throws Exception {
        this.simulationEngine = simulationEngine;
        
        setStyle("-fx-background-color: white;");
        getChildren().add(mapContent);
        
        // Lấy và hiển thị map từ SimulationEngine
        updateDisplay();
        
        System.out.println("✅ MapPanel initialized");
    }

    /**
     * Cập nhật hiển thị map
     */
    public void updateDisplay() throws Exception {
        mapContent.getChildren().clear();
        
        // Lấy tất cả Group từ SimulationEngine
        Group edgesGroup = simulationEngine.getMapEdges();
        Group tlsGroup = simulationEngine.getMapTls();
        Group vehiclesGroup = simulationEngine.getMapVehicles();
        Group junctionsGroup = simulationEngine.getMapJunctions();
        
        // Thêm vào display (theo thứ tự: edges -> junctions -> tls -> vehicles)
        if (edgesGroup != null) mapContent.getChildren().add(edgesGroup);
        if (junctionsGroup != null) mapContent.getChildren().add(junctionsGroup);
        if (tlsGroup != null) mapContent.getChildren().add(tlsGroup);
        if (vehiclesGroup != null) mapContent.getChildren().add(vehiclesGroup);
    }
}
