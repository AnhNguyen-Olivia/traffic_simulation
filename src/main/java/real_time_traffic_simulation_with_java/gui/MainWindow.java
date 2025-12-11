package real_time_traffic_simulation_with_java.gui;
import real_time_traffic_simulation_with_java.alias.Path;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class MainWindow extends Stage {

    public MainWindow(){
        initializeGui();
    }

    private void initializeGui(){
        Group root = new Group();
        Scene scene = new Scene(root, Color.BLACK);

        Image icon = new Image(getClass().getResource(Path.IconImage).toExternalForm());
        this.getIcons().add(icon);
        this.setTitle("Traffic Simulation Beta");
        this.setWidth(1400);
        this.setHeight(850);
        this.setResizable(false);
        this.setScene(scene);
        this.show();

    }

}
