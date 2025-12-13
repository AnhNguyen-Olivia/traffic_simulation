package real_time_traffic_simulation_with_java.cores;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import real_time_traffic_simulation_with_java.wrapper.*;

public class SimulationEngine {
    private SumoTraasConnection conn;
    private VehicleManager vehicleManager;
    private EdgeManager edgeManager;
    private LaneManager laneManager;
    private RouteManager routeManager;
    private TrafficLightManager trafficLightManager;
    private JunctionManager junctionManager;

    /**
     * Constructor
     * @throws Exception
     */
    public SimulationEngine() throws Exception {
        this.conn = new SumoTraasConnection();
        this.vehicleManager = new VehicleManager(this.conn.getConnection());
        this.edgeManager = new EdgeManager(this.conn.getConnection());
        this.laneManager = new LaneManager(this.conn.getConnection());
        this.routeManager = new RouteManager(this.conn.getConnection());
        this.trafficLightManager = new TrafficLightManager(this.conn.getConnection());
        this.junctionManager = new JunctionManager(this.conn.getConnection());
        this.conn.startConnection();
    }


    /**
     * Control simulation: advance simulation by one step
     * @throws Exception
     */
    public void stepSimulation() throws Exception {
        this.conn.nextStep();
    }
    /**
     * Control simulation: stop
     * @throws Exception
     */
    public void stopSimulation() throws Exception {
        this.conn.closeConnection();
    }


    /**
     * Get all IDs: edges
     * @throws Exception
     */
    public List<String> getAllEdgeIDs() throws Exception {
        return this.edgeManager.getIDList();
    }
    /**
     * Get all IDs: traffic lights
     * @throws Exception
     */
    public List<String> getAllTrafficLightIDs() throws Exception {
        return this.trafficLightManager.getIDList();
    }


    /**
     * Inject vehicle: single and batch injection
     */
    public void injectVehicle(int numVehicles, String start_edge_ID, String end_edge_ID, String color) throws Exception {
        // Generate a unique ID
        String routeID = this.conn.getCurrentStep() + "";
        this.routeManager.add(routeID, start_edge_ID, end_edge_ID);
        for (int i = 0; i < numVehicles; i++) {
            String vehicleID = routeID + "_" + i;
            this.vehicleManager.add(vehicleID, routeID, color);
        }
    }
    /**
     * Inject vehicle: stress test tool
     */
    public void stressTest(String start_edge_ID, String end_edge_ID, String color) throws Exception {
        this.injectVehicle(100, start_edge_ID, end_edge_ID, color);
    }


    /**
     * Control traffic lights: toggle all traffic lights
     */
    public void toggleAllTls() throws Exception {
        for (String tlsID : this.trafficLightManager.getIDList()) {
            this.trafficLightManager.nextPhase(tlsID);
        }
    }
    /**
     * Control traffic light: toggle single traffic light
     */
    public void toggleSingleTl(String tlID) throws Exception {
        this.trafficLightManager.nextPhase(tlID);
    }


    /**
     * Get mapping data: edges
     * @throws Exception
     */
    public List<Group> getMapEdges() throws Exception {
        List<Group> edgeGroups =  new ArrayList<>();
        if(this.edgeManager.getEdgeDataList() != null) {
            for(EdgeData edgeData: this.edgeManager.getEdgeDataList()) {
                edgeGroups.add(edgeData.getShape());
            }
        }
        return edgeGroups;
    }
    /**
     * Get mapping data: junctions
     */
    public List<Polygon> getMapJunctions() throws Exception {
        List<Polygon> polygons =  new ArrayList<>();
        if(this.junctionManager.getJunctionDataList() != null) {
            for(JunctionData junctionData: this.junctionManager.getJunctionDataList()) {
                polygons.add(junctionData.getShape());
            }
        }
        return polygons;
    }
    /**
     * Get mapping data: vehicles
     */
    public List<Rectangle> getMapVehicles() throws Exception {
        List<Rectangle> rectangles =  new ArrayList<>();
        if(this.vehicleManager.getVehicleDataList() != null) {
            for(VehicleData vehicleData: this.vehicleManager.getVehicleDataList()) {
                rectangles.add(vehicleData.getShape());
            }
        }
        return rectangles;
    }
    /**
     * Get mapping data: traffic lights
     */
    public List<TrafficLightData> getMapTrafficLights() throws Exception {
        return this.trafficLightManager.getTrafficLightDataList();
    }


    /**
     * Get statistics: edges
     */
    /**
     * Get statistics: vehicles
     */
    /**
     * Get statistics: traffic lights
     */

}
