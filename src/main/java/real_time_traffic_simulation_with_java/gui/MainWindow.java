package real_time_traffic_simulation_with_java.gui;
import real_time_traffic_simulation_with_java.alias.Path;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class MainWindow extends Stage {

    //Sepreate the main window into 3 part using BorderPane

    public MainWindow(){
        initializeGui();
    }

    private void initializeGui(){
        //Sepreate the main window into 3 part using BorderPane
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,1400, 830, Color.WHITE);

        Image icon = new Image(getClass().getResource(Path.IconImage).toExternalForm());
        this.getIcons().add(icon);
        this.setTitle("Traffic Simulation Beta");
        this.setResizable(false);
        this.setScene(scene);
        this.show();

    }

}
