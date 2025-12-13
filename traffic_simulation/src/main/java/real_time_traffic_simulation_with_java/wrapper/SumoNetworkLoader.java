package real_time_traffic_simulation_with_java.wrapper;

import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.cores.Edge;
import real_time_traffic_simulation_with_java.cores.Lane;
import real_time_traffic_simulation_with_java.cores.VehicleData;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a SUMO .net.xml file and also provides
 * simple vehicle simulation for GUI rendering.
 */
public class SumoNetworkLoader {
    public static class SumoNetwork {
        public final List<Edge> edges = new ArrayList<>();
        public final List<Lane> lanes = new ArrayList<>();
        public final List<VehicleData> vehicles = new ArrayList<>();

        // --- internal vehicle simulation state ---
        private static class VehicleState {
            VehicleData vehicle; // Links to the VehicleData instance
            Lane lane;
            int segmentIndex;
            double progress;
            double speed;
        }

        private final List<VehicleState> states = new ArrayList<>();
        
        public void initVehicles() {
            vehicles.clear();
            states.clear();

            // Inject one vehicle onto the first half of the available lanes
            int count = lanes.size() / 2; 

            for (int i = 0; i < count; i++) {
                Lane lane = lanes.get(i); 
                if (lane.shape.size() < 2) continue;

                SumoPosition2D start = lane.shape.get(0);
                
                // Define initial angle and color
                double initialAngle = calculateAngle(lane.shape.get(0), lane.shape.get(1));
                SumoColor vColor = new SumoColor(
                    i % 3 == 0 ? 255 : 0, // Red, Blue, or Green cycle
                    i % 3 == 1 ? 255 : 0, 
                    i % 3 == 2 ? 255 : 0, 
                    255
                );
                
                // 1. Inject the VehicleData object (core model)
                VehicleData v = new VehicleData(
                    "veh_" + i, 
                    start.x, 
                    start.y, 
                    initialAngle, 
                    vColor
                );
                
                vehicles.add(v);

                // 2. Create the internal state tracker (simulation model)
                VehicleState vs = new VehicleState();
                vs.vehicle = v;
                vs.lane = lane;
                vs.segmentIndex = 0;
                vs.progress = 0;
                vs.speed = 1.0; // Speed in meters per frame

                states.add(vs);
            }
        }
        
        public void updateVehicles() {
            for (VehicleState vs : states) {
                List<SumoPosition2D> shape = vs.lane.shape;
                
                // Advance progress
                vs.progress += vs.speed;

                // Handle segment transition (if vehicle moves past current segment)
                while (vs.segmentIndex < shape.size() - 1) {
                    SumoPosition2D p1 = shape.get(vs.segmentIndex);
                    SumoPosition2D p2 = shape.get(vs.segmentIndex + 1);

                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    if (dist < 0.001) { // Skip zero-length segments
                        nextSegment(vs);
                        continue;
                    }
                    
                    if (vs.progress < dist) {
                        break; // Vehicle is still on this segment
                    }
                    
                    // Vehicle passed the segment, carry over remaining progress
                    vs.progress -= dist;
                    nextSegment(vs);
                }
                
                // Final position calculation
                if (vs.segmentIndex < shape.size() - 1) {
                    SumoPosition2D p1 = shape.get(vs.segmentIndex);
                    SumoPosition2D p2 = shape.get(vs.segmentIndex + 1);
                    
                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    double t = vs.progress / dist;
                    double x = p1.x + t * dx;
                    double y = p1.y + t * dy;
                    double newAngle = calculateAngle(p1, p2);

                    // Update the core VehicleData instance
                    vs.vehicle.setPosition(new SumoPosition2D(x, y));
                    vs.vehicle.setAngle(newAngle);
                } else {
                    // This handles the vehicle at the end of the road (just started loop)
                    vs.vehicle.setPosition(shape.get(0));
                    vs.vehicle.setAngle(calculateAngle(shape.get(0), shape.get(1)));
                }
            }
        }
        
        public Edge getEdgeById(String edgeId) {
            for (Edge e : edges) {
                if (e.id.equals(edgeId)) {
                    return e;
                }
            }
            return null;
        }
        
        public VehicleData createVehicleOnLane(Lane lane, String colorName) {

            SumoPosition2D start = lane.shape.get(0);

            VehicleData v = new VehicleData(
                "veh_" + vehicles.size(),
                start.x,
                start.y,
                0,
                Color.stringToColor(colorName)
            );

            v.setLane(lane);
            return v;
        }

        public Edge findOrCreateEdge(String id) {
            for (Edge e : edges) {
                if (e.id.equals(id)) return e;
            }
            Edge e = new Edge(id);
            edges.add(e);
            return e;
        }

        private void nextSegment(VehicleState vs) {
            vs.segmentIndex++;
            if (vs.segmentIndex >= vs.lane.shape.size() - 1) {
                vs.segmentIndex = 0; // loop back to the start of the lane
                vs.progress = 0;
            }
        }
        
        private double calculateAngle(SumoPosition2D p1, SumoPosition2D p2) {
            double angleRad = Math.atan2(p2.y - p1.y, p2.x - p1.x);
            return Math.toDegrees(angleRad);
        }
    }

    public static SumoNetwork loadFromFile(String filePath) {
        System.out.println("Loading SUMO network from: " + filePath);
        SumoNetwork network = new SumoNetwork();

        try {
            File xmlFile = new File(filePath);
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList edgeNodes = doc.getElementsByTagName("edge");

            for (int i = 0; i < edgeNodes.getLength(); i++) {
                Element edgeElement = (Element) edgeNodes.item(i);

                // Skip internal edges
                if (edgeElement.hasAttribute("function")) continue;

                String edgeId = edgeElement.getAttribute("id");
                Edge edge = network.findOrCreateEdge(edgeId);

                NodeList laneNodes = edgeElement.getElementsByTagName("lane");

                for (int j = 0; j < laneNodes.getLength(); j++) {
                    Element laneElement = (Element) laneNodes.item(j);
                    String laneId = laneElement.getAttribute("id");

                    Lane lane = new Lane(laneId);
                    lane.edgeId = edgeId;
                    lane.width = 3.2; // default width

                    if (laneElement.hasAttribute("speed"))
                        lane.speed = Double.parseDouble(laneElement.getAttribute("speed"));

                    if (laneElement.hasAttribute("length"))
                        lane.length = Double.parseDouble(laneElement.getAttribute("length"));

                    if (laneElement.hasAttribute("shape"))
                        parseShapeIntoLane(laneElement.getAttribute("shape"), lane);

                    edge.lanes.add(lane);
                    network.lanes.add(lane);
                }
            }

            System.out.println("Loaded edges=" + network.edges.size() +
                    ", lanes=" + network.lanes.size());

//            network.initVehicles();
            return network;

        } catch (Exception e) {
            System.err.println("Failed to load SUMO network: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static void parseShapeIntoLane(String shapeStr, Lane lane) {
        String[] pairs = shapeStr.split(" ");
        for (String p : pairs) {
            String[] xy = p.split(",");
            if (xy.length != 2) continue;

            double x = Double.parseDouble(xy[0]);
            double y = Double.parseDouble(xy[1]);
            lane.shape.add(new SumoPosition2D(x, y));
        }
    }
}