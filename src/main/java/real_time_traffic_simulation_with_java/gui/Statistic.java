package real_time_traffic_simulation_with_java.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import real_time_traffic_simulation_with_java.alias.Path;

public class Statistic extends Pane {
    public Statistic(){
        Image image = new Image(getClass().getResource(Path.StatisticImage).toExternalForm());
        ImageView imageView = new ImageView(image);
        //imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
        this.getChildren().add(imageView);
        this.setVisible(true);
    }
    
}
