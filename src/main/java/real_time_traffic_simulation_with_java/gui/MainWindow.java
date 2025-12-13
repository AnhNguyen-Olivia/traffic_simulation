package real_time_traffic_simulation_with_java.gui;
import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.gui.MapPanel;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class MainWindow extends Stage {
    private SimulationEngine simulationEngine;

    public MainWindow(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        initializeGui();
    }

    private void initializeGui() throws Exception {
        MapPanel placeHolderMap = new MapPanel(this.simulationEngine);
        //placeHolderMap.setTranslateX(-200);
        placeHolderMap.setTranslateY(-200);
        placeHolderMap.setScaleY(-1.5);
        placeHolderMap.setScaleX(1.5);
        DashBoard dashBoard = new DashBoard();
        dashBoard.setPrefWidth(250);
        dashBoard.setMaxWidth(250);

        Statistic statistic = new Statistic();
        statistic.setPrefWidth(250);
        statistic.setMaxWidth(250);
        
        // Separate the main window into 3 parts using BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(placeHolderMap);
        root.setLeft(dashBoard);
        root.setRight(statistic);
        Scene scene = new Scene(root,1400, 830, Color.WHITE);

        BorderPane.setAlignment(placeHolderMap,Pos.CENTER);
        BorderPane.setAlignment(dashBoard,Pos.CENTER);
        BorderPane.setAlignment(statistic,Pos.CENTER);

        Image icon = new Image(getClass().getResource(Path.IconImage).toExternalForm());
        this.getIcons().add(icon);
        this.setTitle("Traffic Simulation Beta");
        this.setResizable(false);
        this.setScene(scene);
        this.show();
    }

}
