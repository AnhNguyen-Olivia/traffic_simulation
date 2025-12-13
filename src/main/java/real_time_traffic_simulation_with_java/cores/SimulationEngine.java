package real_time_traffic_simulation_with_java.cores;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import real_time_traffic_simulation_with_java.wrapper.*;
import real_time_traffic_simulation_with_java.alias.Color;

public class SimulationEngine {
    private SumoTraasConnection conn;
    private VehicleManager vehicleManager;
    private EdgeManager edgeManager;
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
     * Get all IDs: vehicles
     * @throws Exception
     */
    public List<String> getAllVehicleIDs() throws Exception {
        return this.vehicleManager.getIDList();
    }
    /**
     * Get all IDs: traffic lights
     * @throws Exception
     */
    public List<String> getAllTrafficLightIDs() throws Exception {
        return this.trafficLightManager.getIDList();
    }
    /**
     * Validate edge ID
     * @throws Exception
     */
    public boolean validateEdgeID(String edgeID) throws Exception {
        List<String> edgeIDs = this.edgeManager.getIDList();
        return edgeIDs.contains(edgeID);
    }


    /**
     * Inject vehicle: single and batch injection
     * Route ID format: [currentStep]
     * Vehicle ID format: [routeID]_[index]
     * @throws Exception
     */
    public void injectVehicle(int numVehicles, String start_edge_ID, String end_edge_ID, String color) throws Exception {
        // Generate a unique ID
        String routeID = (int) this.conn.getCurrentStep() + "";
        this.routeManager.add(routeID, start_edge_ID, end_edge_ID);
        for (int i = 0; i < numVehicles; i++) {
            String vehicleID = routeID + "_" + i;
            this.vehicleManager.add(vehicleID, routeID, color);
        }
    }
    /**
     * Inject vehicle: stress test tool, inject 100 vehicles on up to 10 different random routes
     * Route ID format: [currentStep]_[index]
     * Vehicle ID format: [routeID]_[index_of_vehicle_in_this_stress_test]
     * @throws Exception
     */
    public void stressTest(int number_of_vehicles, String start_edge_ID) throws Exception {
        // Randomly select 10 end_edge to inject vehicles, if less than 10 edges, use all edges
        List<String> edgeIDs =  this.getAllEdgeIDs();
        Collections.shuffle(edgeIDs);
        int n;
        if(edgeIDs.size() < 10) {n = edgeIDs.size();} else {n = 10;}
        List<String> end_edge_IDs = edgeIDs.subList(0, n);
        // Generate a unique ID
        String ID = (int) this.conn.getCurrentStep() + "";
        //Generate color list to inject vehicles with random color
        List<String> colorList = new ArrayList<>(Color.ListofAllColor);
        // Generate n random routes to inject vehicles
        for (int j = 0; j < n; j++) {
            this.routeManager.add(ID + "_" + j, start_edge_ID, end_edge_IDs.get(j));
        }
        // Inject vehicles
        for (int i = 0; i < number_of_vehicles; i++) {
            for (int j = 0; j < n; j++) {
                Collections.shuffle(colorList);
                this.vehicleManager.add(ID + "_" + j + "_" + i, ID + "_" + j, colorList.get(0));
            }
        }
    }


    /**
     * Control traffic lights: toggle all traffic lights
     * @throws Exception
     */
    public void toggleAllTls() throws Exception {
        for (String tlsID : this.trafficLightManager.getIDList()) {
            this.trafficLightManager.nextPhase(tlsID);
        }
    }
    /**
     * Control traffic light: toggle single traffic light
     * @throws Exception
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
     * @throws Exception
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
     * @throws Exception
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
     * @throws Exception
     */
    public List<Group> getMapTls() throws Exception {
        List<Group> lightGroups =  new ArrayList<>();
        if(this.trafficLightManager.getTrafficLightDataList() != null) {
            for(TrafficLightData trafficLightData: this.trafficLightManager.getTrafficLightDataList()) {
                lightGroups.add(trafficLightData.getShape());
            }
        }
        return lightGroups;
    }
    /**
     * Update mapping data: traffic lights
     * @throws Exception
     */
    public void updateMapTls() throws Exception {
        this.trafficLightManager.updateTrafficLightDataList();
    }


    /**
     * Get tooltip: edges
     * @throws Exception
     */
    public String getEdgeTooltip(String edgeID) throws Exception {
        return String.format(
"Edge ID: %s (%d lane), Max speed: %.2f km/h, Length: %.2f m\n Vehicle Count: %d, Average Speed: %.2f km/h\nDensity: %.2f veh/km, Estimated Travel Time: %.2f s",  
                    edgeID, edgeManager.getLaneCount(edgeID), 
                    edgeManager.getMaxSpeed(edgeID), edgeManager.getLength(edgeID),
                    edgeManager.getVehicleCount(edgeID), edgeManager.getAverageSpeed(edgeID),
                    edgeManager.getDensity(edgeID), edgeManager.getTravelTime(edgeID)
                );
    }
    /**
     * Get tooltip: vehicles
     * @throws Exception
     */
    public String getVehicleTooltip(String vehicleID) throws Exception {
        return String.format("Vehicle ID: %s\n Speed: %.2f km/h\n Is running on edge: %s",  
                    vehicleID, 
                    vehicleManager.getSpeed(vehicleID), 
                    vehicleManager.getAngle(vehicleID),
                    vehicleManager.getEdgeID(vehicleID)
                );
    }
    /**
     * Get tooltip: traffic lights
     */
    public String getTlTooltip(String tlID) throws Exception {
        return String.format(
"Traffic Light ID: %s (%d phase) controlled Junction: %s\n Currently at phase: %d (Total: %d seconds)\n Remain: %d seconds",  
                    tlID, trafficLightManager.getPhaseCount(tlID), tlID,
                    trafficLightManager.getPhaseID(tlID), trafficLightManager.getDuration(tlID),
                    trafficLightManager.getNextSwitch(tlID)
                );
    }


    /**
     * Debug tool: return edge ID vehicle running on
     * @throws Exception
     */
    public String vehIsOnEdge(String vehicleID) throws Exception {
        return this.vehicleManager.getEdgeID(vehicleID);
    }
}
