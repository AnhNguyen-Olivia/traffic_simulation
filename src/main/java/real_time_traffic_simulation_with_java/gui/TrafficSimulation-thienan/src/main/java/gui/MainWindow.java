<<<<<<< HEAD:src/main/java/real_time_traffic_simulation_with_java/gui/MainWindow.java
package real_time_traffic_simulation_with_java.gui;

=======
package gui;
>>>>>>> 8a9f5fa6ee07407c35537c27ee2e102ca7fb50eb:src/main/java/real_time_traffic_simulation_with_java/gui/TrafficSimulation-thienan/src/main/java/gui/MainWindow.java
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * MainWindow - Cửa sổ chính
 * Học cách chia cửa sổ thành 3 phần: Trái, Giữa, Phải
 */
public class MainWindow extends Application {
    
    @Override
    public void start(Stage stage) {
        // Bước 1: Đặt tên cửa sổ
        stage.setTitle("Real Time SUMO Traffic Simulation");
        
        // Bước 2: Tạo BorderPane (bố cục có 5 vùng: top, bottom, left, center, right)
        BorderPane root = new BorderPane();
        
        // Bước 3: Tạo 3 panels
        ControlPanel leftPanel = new ControlPanel();
        MapPanel centerPanel = new MapPanel();
        Dashboard rightPanel = new Dashboard();
        
        // 🔥 Bước 3.5: Tạo Controller (Backend Logic điều khiển)
        SimulationController controller = new SimulationController(leftPanel);
        
        // Bước 3.6: Wrap panels trong ScrollPane cho vertical scrolling
        ScrollPane leftScroll = new ScrollPane(leftPanel);
        leftScroll.setFitToWidth(true);  // Panel sẽ chiếm full width
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);  // Ẩn horizontal scrollbar
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // Vertical scrollbar khi cần
        leftScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        leftScroll.setPannable(true);  // Cho phép scroll bằng chuột
        
        ScrollPane rightScroll = new ScrollPane(rightPanel);
        rightScroll.setFitToWidth(true);  // Panel sẽ chiếm full width
        rightScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);  // Ẩn horizontal scrollbar
        rightScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // Vertical scrollbar khi cần
        rightScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        rightScroll.setPannable(true);  // Cho phép scroll bằng chuột
        
        // Bước 4: Đặt panels vào vị trí (với ScrollPane)
        root.setLeft(leftScroll);    // Panel bên trái với scroll
        root.setCenter(centerPanel); // Panel ở giữa (không cần scroll - có zoom)
        root.setRight(rightScroll);   // Panel bên phải với scroll
        
        // Bước 5: Tạo Scene với responsive design
        Scene scene = new Scene(root, 1200, 700);  // Kích thước khởi đầu vừa phải
        scene.setFill(Color.web("#F5F5F7"));  // macOS background
        
        // Thêm global stylesheet
        root.setStyle("-fx-background-color: #F5F5F7;");
        
        // Bước 6: Thiết lập responsive window
        stage.setMinWidth(1000);   // Chiều rộng tối thiểu
        stage.setMinHeight(600);   // Chiều cao tối thiểu
        stage.setMaximized(false); // Không tự động maximize
        stage.setResizable(true);  // Cho phép resize
        
        // Hiển thị
        stage.setScene(scene);
        stage.show();
    }
    
    // Main method - chạy chương trình
    public static void main(String[] args) {
        launch(args);
    }
}
