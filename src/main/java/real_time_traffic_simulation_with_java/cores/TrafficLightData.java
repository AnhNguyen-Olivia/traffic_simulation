package real_time_traffic_simulation_with_java.cores;

import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;
import real_time_traffic_simulation_with_java.alias.Metrics;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.Group;

/**
 * @Finished
 * @Test Completed
 * @Javadoc Completed
 */

public class TrafficLightData {
    private String tlID;
    /**
     * Each Polygon represents the shape of corresponding sub-light
     */
    private Group light_group;
    /**
     * Each is a 1-char string representing the color state of corresponding sub-light ("r","g","y")
     */
    private List<String> colorList;

    /**
     * Constructor
     * @param coordinates each SumoGeometry represents the coordinates of incoming lane shape for sub-light
     * @param colorList a string representing the color states of the traffic light's sub-lights
     */
    public TrafficLightData(String tlID, List<SumoGeometry> coordinates, String colorString) {
        this.tlID = tlID;
        this.light_group = createLightGroup(tlID, coordinates, colorString);
        this.colorList = convertColorToList(colorString);
    }

    /**
     * Getter
     */
    public String getTlID() {
        return tlID;
    }
    public Group getShape() {
        return light_group;
    }
    public List<String> getColorList() {
        return colorList;
    }

    /**
     * Setter
     */
    public void setColor(String colorString) {
        this.colorList = convertColorToList(colorString);
        for (int i = 0; i < this.light_group.getChildren().size(); i++) {
            Polygon light_shape = (Polygon) this.light_group.getChildren().get(i);
            light_shape.setFill(javafx.scene.paint.Paint.valueOf(this.colorList.get(i)));
        }
    }

    /**
     * Private helper method: Grouping the sub-light shapes together
     */
    private Group createLightGroup(String tlID, List<SumoGeometry> geometries, String colorString) {
        List<Polygon> light_shapes = convertSumoGeometryToShapeList(geometries, colorString);
        Group lightGroup = new Group();
        lightGroup.getChildren().addAll(light_shapes);
        lightGroup.setId(tlID);
        lightGroup.setPickOnBounds(true); // Allow mouse event on transparent area
        return lightGroup;
    }

    /**
     * Private helper method: convert list of lanes' coordinations to list of Polygon
     */
    private List<Polygon> convertSumoGeometryToShapeList(List<SumoGeometry> geometries, String colorString) {
        List<Polygon> shapeList = new ArrayList<>();
        List<String> color = convertColorToList(colorString);
        for (int i = 0; i < geometries.size(); i++) {
            Polygon shape = createPolygon(geometries.get(i));
            shape.setFill(javafx.scene.paint.Paint.valueOf(color.get(i)));
            shapeList.add(shape);
        }
        return shapeList;
    }

    /**
     * Private helper method: convert color string to list of single-char strings for each sub-light
     */
    private List<String> convertColorToList(String colorString) {
        List<String> colorlist = new ArrayList<>();
        for (int i = 0; i < colorString.length(); i++) {
            String temp = String.valueOf(colorString.charAt(i));
            if(temp.equals("r")) {
                colorlist.add("RED");
            } else if(temp.equals("g")) {
                colorlist.add("GREEN");
            }  else {
                colorlist.add("YELLOW");
            }
        }
        return colorlist;
    }

    /**
     * Private helper method: return Polygon representing 4 corner points of the sub-light
     */
    private Polygon createPolygon(SumoGeometry geometry) {
        SumoPosition2D start_pos = geometry.coords.get(0); // Get the 1st point of the lane shape
        SumoPosition2D end_pos = geometry.coords.get(geometry.coords.size() - 1); // Get the last point of the lane shape
        Point2D end_point = new Point2D(end_pos.x, end_pos.y);
        // Get vector from start to end and perpendicular vector
        Point2D direction
            = new Point2D(end_pos.x - start_pos.x, end_pos.y - start_pos.y).normalize().multiply(Metrics.DEFAULT_LANE_WIDTH);
        Point2D perpendicular_vec 
            = new Point2D(end_pos.y - start_pos.y, -(end_pos.x - start_pos.x)).normalize().multiply(Metrics.TLS_WIDTH);
        // Calculate 4 corner points
        Point2D p1 = end_point.add(perpendicular_vec);
        Point2D p2 = end_point.subtract(perpendicular_vec);
        Point2D p3 = p2.subtract(direction);
        Point2D p4 = p1.subtract(direction);
        return new Polygon(
            p1.getX(), p1.getY(),
            p2.getX(), p2.getY(),
            p3.getX(), p3.getY(),
            p4.getX(), p4.getY()
        );
    }

}
