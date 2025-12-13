package real_time_traffic_simulation_with_java.gui;
import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

import java.io.IOException;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class MainWindow extends Stage {
    private SimulationEngine simulationEngine;

    public MainWindow() throws Exception {
        try {
            simulationEngine = new SimulationEngine();
            initializeGui();
        } catch (IOException | RuntimeException ex) { // use the specific checked type(s) you expect
            // handle or rethrow as unchecked
            throw new IllegalStateException("Failed to initialize MainWindow", ex);
        }
    }

    private void initializeGui() throws Exception {
        
        trafficLightPane tlPane = new trafficLightPane(simulationEngine, simulationEngine.getMapTrafficLights());
        
        // Calculate bounds to center the view
        tlPane.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            // Center on the traffic lights after layout
            double centerX = (newBounds.getMinX() + newBounds.getMaxX()) / 2;
            double centerY = (newBounds.getMinY() + newBounds.getMaxY()) / 2;
            tlPane.setTranslateX(700 - centerX); // Half of window width
            tlPane.setTranslateY(415 - centerY); // Half of window height
        });

        // Wrap in ScrollPane to handle SUMO coordinates and allow panning
        ScrollPane scrollPane = new ScrollPane(tlPane);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        // Prevent ScrollPane from consuming mouse clicks
        scrollPane.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            // Don't consume clicks, let them through to children
            if(event.getClickCount() == 2) {
                System.out.println("Double-click detected in ScrollPane");
            }
        });
        
        // Separate the main window into 3 parts using BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(scrollPane);
        Scene scene = new Scene(root,1400, 830, Color.WHITE);

        BorderPane.setAlignment(scrollPane, Pos.CENTER);

        Image icon = new Image(getClass().getResource(Path.IconImage).toExternalForm());
        this.getIcons().add(icon);
        this.setTitle("Traffic Simulation Beta");
        this.setResizable(false);
        this.setScene(scene);
        this.show();

        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            private long last = 0;
            private static final long INTERVAL = 200_000_000; // 200ms in ns
            @Override
            public void handle(long now) {
                if (now - last < INTERVAL) return;
                last = now;
                try {
                    simulationEngine.stepSimulation(); // advance SUMO one tick
                    var lights = simulationEngine.getMapTrafficLights(); // updates color lists
                    for (var tl : lights) {
                        String phase = String.join("", tl.getColorList());
                        tlPane.updatePhase(tl.getTlID(), phase);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        timer.start();
    }

}
