package real_time_traffic_simulation_with_java.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import it.polito.appeal.traci.SumoTraciConnection;

//import real_time_traffic_simulation_with_java.wrapper.EdgeManager;
import real_time_traffic_simulation_with_java.wrapper.LaneManager;
import real_time_traffic_simulation_with_java.wrapper.SumoTraasConnection;

import java.util.List;

public class guiTesting extends Application {

    private SumoTraasConnection connection;
    private SumoTraciConnection conn;
    //private EdgeManager edgeManager;
    private LaneManager laneManager;

    @Override
    public void start(Stage stage) throws Exception {

        // ---- CONNECT TO SUMO ----
        connection.startConnection();   // if your wrapper does this, adjust accordingly

        // ---- WRAPPERS ----
        //edgeManager = new EdgeManager(conn);
        laneManager = new LaneManager(conn);

        Group root = new Group();

        double scale = 0.5;  // meters → pixels

        // ---- DRAW ALL LANES ----
        List<String> laneIDs = laneManager.getIDList();

        for (String laneID : laneIDs) {

            List<double[]> shape = laneManager.getCoordinateList(laneID);

            for (int i = 0; i < shape.size() - 1; i++) {

                double[] p1 = shape.get(i);
                double[] p2 = shape.get(i + 1);

                Line line = new Line(
                        p1[0] * scale,
                        -p1[1] * scale,
                        p2[0] * scale,
                        -p2[1] * scale
                );

                line.setStroke(Color.GRAY);
                line.setStrokeWidth(2);

                root.getChildren().add(line);
            }
        }

        Scene scene = new Scene(root, 1200, 800, Color.WHITE);
        stage.setTitle("SUMO Map Drawing Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
