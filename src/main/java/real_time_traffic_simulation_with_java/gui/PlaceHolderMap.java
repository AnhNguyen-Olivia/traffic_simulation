package real_time_traffic_simulation_with_java.gui;
import real_time_traffic_simulation_with_java.alias.Path;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class PlaceHolderMap extends StackPane  {
    public PlaceHolderMap(){
        Image image = new Image(getClass().getResource(Path.CatMapImage).toExternalForm());
        ImageView imageView = new ImageView(image);
        //imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(this.widthProperty());
        imageView.fitHeightProperty().bind(this.heightProperty());
        this.getChildren().add(imageView);
        this.setVisible(true);
    }
    
}
