package real_time_traffic_simulation_with_java.cores;

// import de.tudresden.sumo.objects.SumoGeometry;

import java.util.List;
import javafx.geometry.Point2D;

public class TrafficLightData {
    public String tlID;
    public List<Coordinate> coordinates;
    public List<String> colorList;

    /**
     * Inner class to represent 4 JavaFX Point2D coordinates
     */
    public class Coordinate {
        public Point2D topLeft;
        public Point2D topRight;
        public Point2D bottomRight;
        public Point2D bottomLeft;

        public Coordinate(Point2D topLeft, Point2D topRight, Point2D bottomRight, Point2D bottomLeft) {
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomRight = bottomRight;
            this.bottomLeft = bottomLeft;
        }
    }

}
