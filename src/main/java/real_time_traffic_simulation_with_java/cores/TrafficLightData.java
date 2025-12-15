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
 * Represents the visual data of a traffic light in the simulation, 
 * grouping multiple Polygons representing the sub-lights of the traffic light. <br>
 * Each sub-light's shape is derived from the coordinates of the incoming lanes, and its color is determined by the traffic light's current state.
 */
public class TrafficLightData extends Group {
    /**
     * Represents the visual data of a traffic light in the simulation, 
     * grouping multiple Polygons representing the sub-lights of the traffic light. <br>
     * Each sub-light's shape is derived from the coordinates of the incoming lanes, and its color is determined by the traffic light's current state.
     * @param tlID the unique identifier of the traffic light
     * @param coordinates a list of SumoGeometry objects representing the shapes of incoming lanes controlled by the traffic light
     * @param colorString a string representing the color states of the traffic light
     */
    public TrafficLightData(String tlID, List<SumoGeometry> coordinates, String colorString) {
        List<Polygon> light_shapes = convertSumoGeometryToShapeList(coordinates, colorString);
        this.getChildren().addAll(light_shapes);
        this.setId(tlID);
        this.setPickOnBounds(true); // Allow mouse event on transparent area
    }

    /**
     * Refresh the color of the traffic light's sub-lights
     * @param colorString a string representing the new color states of the traffic light
     */
    public void setColor(String colorString) {
        List<String> colorList = convertColorToList(colorString);
        for (int i = 0; i < this.getChildren().size(); i++) {
            Polygon light_shape = (Polygon) this.getChildren().get(i);
            light_shape.setFill(javafx.scene.paint.Paint.valueOf(colorList.get(i)));
        }
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
            = new Point2D(end_pos.x - start_pos.x, end_pos.y - start_pos.y).normalize().multiply(Metrics.TLS_WIDTH);
        Point2D perpendicular_vec 
            = new Point2D(end_pos.y - start_pos.y, -(end_pos.x - start_pos.x)).normalize().multiply(Metrics.DEFAULT_LANE_WIDTH/2);
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
