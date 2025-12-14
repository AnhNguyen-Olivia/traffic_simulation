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
* Utility class responsible for loading a SUMO <code>.net.xml</code> file
* and constructing an in-memory representation of the road network.
* <p>
* In addition to parsing static network data (edges and lanes), this class
* also provides a lightweight vehicle simulation layer used by the GUI.
* </p>
*
* <h2>Design Role</h2>
* <ul>
* <li><b>Model loader:</b> Parses XML into {@link Edge} and {@link Lane} objects.</li>
* <li><b>Simulation provider:</b> Updates vehicle positions along lane geometry.</li>
* <li><b>Bridge:</b> Connects SUMO data with JavaFX rendering via {@link VehicleData}.</li>
* </ul>
* 
* @author Pham Tran Minh Anh
*/
public class SumoNetworkLoader {
	/**
    * Holds the entire loaded SUMO network structure (Edges and Lanes) and
    * manages the state and simulation logic for vehicles running on the network.
    */
    public static class SumoNetwork {
        public final List<Edge> edges = new ArrayList<>();
        public final List<Lane> lanes = new ArrayList<>();
        public final List<VehicleData> vehicles = new ArrayList<>();

        /**
        * Internal class representing the simulation state of a single vehicle.
        * This data is used to calculate the vehicle's position and orientation
        * during the simulation update loop, separate from the core {@link VehicleData}
        * which holds the display-ready attributes.
        */
        private static class VehicleState {
        	/** Links to the VehicleData instance. */
            VehicleData vehicle; 		
            /** The lane the vehicle is currently traveling on. */
            Lane lane;
            /** The index of the current polyline segment (p1 -> p2) on the lane's shape. */
            int segmentIndex;
            /** The distance covered along the current segment in meters. */
            double progress;
            /** The speed of the vehicle in meters per frame (m/frame). */
            double speed;
        }

        /** Internal list tracking simulation state for each vehicle */
        private final List<VehicleState> states = new ArrayList<>();
        
        /**
        * Initializes vehicles on the network.
        * <p>
        * Vehicles are injected onto the first half of the available lanes
        * and assigned an initial direction and color.
        * </p>
        *
        * <ol>
        * <li>Clear existing vehicles and simulation state</li>
        * <li>Select a subset of lanes</li>
        * <li>Create {@link VehicleData} objects</li>
        * <li>Create corresponding {@link VehicleState} trackers</li>
        * </ol>
        */
        public void initVehicles() {
            vehicles.clear();
            states.clear();

            /** Inject one vehicle onto the first half of the available lanes */
            int count = lanes.size() / 2; 

            for (int i = 0; i < count; i++) {
                Lane lane = lanes.get(i); 
                if (lane.shape.size() < 2) continue;

                SumoPosition2D start = lane.shape.get(0);
                
                /** Define initial angle and color */
                double initialAngle = calculateAngle(lane.shape.get(0), lane.shape.get(1));
                SumoColor vColor = new SumoColor(
                    i % 3 == 0 ? 255 : 0, 			// Red, Blue, or Green cycle
                    i % 3 == 1 ? 255 : 0, 
                    i % 3 == 2 ? 255 : 0, 
                    255
                );
                
                /** Inject the VehicleData object (core model) */
                VehicleData v = new VehicleData(
                    "veh_" + i, 
                    start.x, 
                    start.y, 
                    initialAngle, 
                    vColor
                );
                
                vehicles.add(v);

                /** Create the internal state tracker (simulation model) */
                VehicleState vs = new VehicleState();
                vs.vehicle = v;
                vs.lane = lane;
                vs.segmentIndex = 0;
                vs.progress = 0;
                vs.speed = 1.0; 		// Speed in meters per frame

                states.add(vs);
            }
        }
        
        /**
         * Performs one step of the vehicle simulation.
         * <p>
         * For each vehicle:
         * <ul>
         * <li>Increments {@code progress} by {@code speed}.</li>
         * <li>Enters a loop to handle movement past the current lane segment boundary,
         * carrying over any remaining progress to the next segment via {@code nextSegment(vs)}.</li>
         * <li>Calculates the final position and angle within the current segment
         * using linear interpolation (parameter $t = progress / distance$).</li>
         * <li>Updates the corresponding {@link VehicleData} object's position and angle.</li>
         * </ul>
         */
        public void updateVehicles() {
            for (VehicleState vs : states) {
                List<SumoPosition2D> shape = vs.lane.shape;
                
                vs.progress += vs.speed;

                /** Handle segment transition (if vehicle moves past current segment) */
                while (vs.segmentIndex < shape.size() - 1) {
                    SumoPosition2D p1 = shape.get(vs.segmentIndex);
                    SumoPosition2D p2 = shape.get(vs.segmentIndex + 1);

                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    if (dist < 0.001) { 		// Skip zero-length segments
                        nextSegment(vs);
                        continue;
                    }
                    
                    if (vs.progress < dist) {
                        break; 					// Vehicle is still on this segment
                    }
                    
                    /** Vehicle passed the segment, carry over remaining progress */
                    vs.progress -= dist;
                    nextSegment(vs);
                }
                
                /** Final position calculation (linear interpolation) */
                if (vs.segmentIndex < shape.size() - 1) {
                    SumoPosition2D p1 = shape.get(vs.segmentIndex);
                    SumoPosition2D p2 = shape.get(vs.segmentIndex + 1);
                    
                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    /** Interpolation parameter t, where t=0 is p1 and t=1 is p2 */
                    double t = vs.progress / dist;
                    double x = p1.x + t * dx;
                    double y = p1.y + t * dy;
                    double newAngle = calculateAngle(p1, p2);

                    /** Update the core VehicleData instance */
                    vs.vehicle.setPosition(new SumoPosition2D(x, y));
                    vs.vehicle.setAngle(newAngle);
                } else {
                	/** Vehicle finished the lane, reset to start to loop the animation */
                    vs.vehicle.setPosition(shape.get(0));
                    vs.vehicle.setAngle(calculateAngle(shape.get(0), shape.get(1)));
                }
            }
        }
        
        /**
        * Retrieves an {@link Edge} by its ID.
        *
        * @param edgeId SUMO edge id
        * @return matching {@link Edge} or {@code null} if not found
        */
        public Edge getEdgeById(String edgeId) {
            for (Edge e : edges) {
                if (e.id.equals(edgeId)) {
                    return e;
                }
            }
            return null;
        }
        
        /**
        * Creates a new vehicle and places it at the start of the specified lane.
        * It creates and registers both the {@link VehicleData} and the internal
        * {@code VehicleState} for simulation.
        *
        * @param lane The {@link Lane} where the vehicle should be placed.
        * @param colorName The name of the color to assign to the vehicle.
        * @return The newly created {@link VehicleData} object.
        */
        public VehicleData createVehicleOnLane(Lane lane, String colorName) {
            SumoPosition2D start = lane.shape.get(0);

            VehicleData v = new VehicleData(
                "veh_" + vehicles.size(),
                start.x,
                start.y,
                0,
                Color.stringToColor(colorName)
            );
            
            VehicleState vs = new VehicleState();
            vs.vehicle = v;
            vs.lane = lane;         // Store lane here, not in VehicleData
            vs.segmentIndex = 0;
            vs.progress = 0;
            vs.speed = 1.0;
            
            states.add(vs);
            
            vehicles.add(v);

            return v;
        }

        /**
        * Finds an existing {@link Edge} by ID or creates a new one and adds it to the network.
        * This utility is primarily used during the XML parsing process.
        *
        * @param id The ID of the edge.
        * @return The existing or newly created {@link Edge}.
        */
        public Edge findOrCreateEdge(String id) {
            for (Edge e : edges) {
                if (e.id.equals(id)) return e;
            }
            Edge e = new Edge(id);
            edges.add(e);
            return e;
        }

        /**
        * Advances the vehicle's segment index and handles the loop-back logic.
        * If the end of the lane shape is reached, the vehicle wraps around
        * to the beginning of the lane.
        *
        * @param vs The {@code VehicleState} to update.
        */
        private void nextSegment(VehicleState vs) {
            vs.segmentIndex++;
            if (vs.segmentIndex >= vs.lane.shape.size() - 1) {
                vs.segmentIndex = 0; 		// loop back to the start of the lane
                vs.progress = 0;
            }
        }
        
        /**
        * Calculates the angle (in degrees) of the vector from {@code p1} to {@code p2}.
        * This determines the heading of the vehicle along that segment.
        * Uses $atan2(dy, dx)$ to handle all four quadrants correctly.
        *
        * @param p1 The starting position of the vector.
        * @param p2 The ending position of the vector.
        * @return The angle in degrees, where 0 degrees is positive X-axis.
        */
        private double calculateAngle(SumoPosition2D p1, SumoPosition2D p2) {
            double angleRad = Math.atan2(p2.y - p1.y, p2.x - p1.x);
            return Math.toDegrees(angleRad);
        }
    }

    /**
    * Statically loads a SUMO network file ({@code .net.xml}) from the specified path.
    * <p>
    * It uses {@code javax.xml.parsers} to read the XML document and iterates through
    * {@code <edge>} and nested {@code <lane>} elements to construct the {@link SumoNetwork}
    * data structure. Internal edges (traffic light logic) are skipped.
    * The polyline shape string is parsed to define the geometry of each lane.
    *
    * @param filePath The file path to the SUMO network XML file.
    * @return A fully populated {@link SumoNetwork} object, or null if loading fails.
    */
    public static SumoNetwork loadFromFile(String filePath) {
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

            return network;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
    * Parses a SUMO lane shape string into coordinate points.
    *
    * @param shapeStr shape attribute from XML
    * @param lane lane to populate
    */
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