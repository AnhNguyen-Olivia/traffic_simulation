package real_time_traffic_simulation_with_java.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import real_time_traffic_simulation_with_java.alias.Path;

/**
 * The statistic panel on the right side of the main window
 * Use to display statistic infomation
 * It is currently placeing an image as a placeholder, we will implement it later
*/
public class Statistic extends Pane {
    public Statistic(){
        /**
         * This is the placeholder image, yopu can replace it by changinge the path
        */
        Image image = new Image(getClass().getResource(Path.StatisticImage).toExternalForm());
        ImageView imageView = new ImageView(image);

        /**
         * Bind the image size to the pane size. 
        */
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
        this.getChildren().add(imageView);
        this.setVisible(true);
    }
    
}
