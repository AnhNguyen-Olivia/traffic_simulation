package real_time_traffic_simulation_with_java.gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import real_time_traffic_simulation_with_java.cores.Edge;
import real_time_traffic_simulation_with_java.cores.Lane;
import real_time_traffic_simulation_with_java.cores.VehicleData;
import real_time_traffic_simulation_with_java.wrapper.SumoNetworkLoader;

public class MainWindow extends Application {

    @Override
    public void start(Stage stage) {
        System.out.println("Launching Real-time Traffic Simulation Application...");
        stage.setTitle("Real-time Traffic Simulation");

        BorderPane root = new BorderPane();

        ControlPanel leftPanel = new ControlPanel();
        MapPanel centerPanel = new MapPanel();
        Dashboard rightPanel = new Dashboard();

        try {
            // Load SUMO network (.net.xml)
            String mapPath = "C:/Users/ASUS/Downloads/traffic_simulation-main (2)/traffic_simulation-main/src/main/java/real_time_traffic_simulation_with_java/SumoConfig/map.net.xml";

            System.out.println("Loading SUMO map: " + mapPath);

            SumoNetworkLoader.SumoNetwork network = SumoNetworkLoader.loadFromFile(mapPath);
            if (network == null) {
                throw new RuntimeException("Failed to load network from " + mapPath);
            }

            // Give network to the map panel
            centerPanel.setNetwork(network);
            // Render the map
            centerPanel.renderMap();
            
            network.initVehicles();
            centerPanel.setVehicles(network.vehicles);
            
            leftPanel.setEdges(network.edges.stream().map(e -> e.id).toList());
            
            leftPanel.setVehicleInjectListener((edgeId, color, quantity) -> {

                Edge edge = network.getEdgeById(edgeId);
                if (edge == null || edge.lanes.isEmpty()) return;

                Lane lane = edge.lanes.get(0); // first lane

                for (int i = 0; i < quantity; i++) {

                    VehicleData v = network.createVehicleOnLane(lane, color);
                    network.vehicles.add(v);
                }

                centerPanel.setVehicles(network.vehicles);
            });


            System.out.println("Map loaded and rendered successfully!");
            
            AnimationTimer timer = new AnimationTimer() {
                private long lastUpdate = 0;
                // Target ~30 frames per second (1 second = 1,000,000,000 nanoseconds)
                // This controls the visual update rate.
                private final long frameInterval = 1_000_000_000L / 30L; 

                @Override
                public void handle(long now) {
                    if (now - lastUpdate >= frameInterval) {
                        
                        // a) Update Simulation State: Moves the vehicle data models (VehicleData)
                        // This uses the logic defined in SumoNetworkLoader.SumoNetwork.updateVehicles()
                        network.updateVehicles(); 

                        // b) Update View: Rerenders the vehicle shapes in MapPanel
                        // This uses the efficient shape-reuse logic in MapPanel.updateVehicles()
                        centerPanel.updateVehicles(); 
                        
                        lastUpdate = now;
                    }
                }
            };
            timer.start();

        } catch (Exception e) {
            System.err.println("Error loading simulation: " + e.getMessage());
            e.printStackTrace();
        }

        // Scroll panels
        ScrollPane leftScroll = new ScrollPane(leftPanel);
        leftScroll.setFitToWidth(true);
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        leftScroll.setPannable(true);

        ScrollPane rightScroll = new ScrollPane(rightPanel);
        rightScroll.setFitToWidth(true);
        rightScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        rightScroll.setPannable(true);

        root.setLeft(leftScroll);
        root.setCenter(centerPanel);
        root.setRight(rightScroll);

        Scene scene = new Scene(root, 1200, 700);
        scene.setFill(Color.web("#F5F5F7"));
        root.setStyle("-fx-background-color: #F5F5F7;");

        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.setMaximized(false);
        stage.setResizable(true);

        stage.setScene(scene);
        stage.show();
     
    }

    public static void main(String[] args) {
        launch(args);
    }
}
