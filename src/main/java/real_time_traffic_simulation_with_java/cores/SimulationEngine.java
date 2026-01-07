package real_time_traffic_simulation_with_java.cores;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import real_time_traffic_simulation_with_java.wrapper.*;
import real_time_traffic_simulation_with_java.alias.Color;


/**
 * Core to control the simulation, center of backend operations. <br>
 * Manages all other manager classes.
 */
public class SimulationEngine {
    private SumoTraasConnection conn;
    private VehicleManager vehicleManager;
    private EdgeManager edgeManager;
    private RouteManager routeManager;
    private TrafficLightManager trafficLightManager;
    private JunctionManager junctionManager;

    /**
     * Core to control the simulation, center of backend operations. <br>
     * Manages all other manager classes.
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
     * @return List of String edge IDs
     * @throws Exception
     */
    public List<String> getAllEdgeIDs() throws Exception {
        return this.edgeManager.getIDList();
    }
    /**
     * Get all IDs: vehicles
     * @return List of String vehicle IDs
     * @throws Exception
     */
    public List<String> getAllVehicleIDs() throws Exception {
        return this.vehicleManager.getIDList();
    }
    /**
     * Get all IDs: traffic lights
     * @return List of String traffic light IDs
     * @throws Exception
     */
    public List<String> getAllTrafficLightIDs() throws Exception {
        return this.trafficLightManager.getIDList();
    }
    /**
     * Validate edge ID
     * @return true if valid, false otherwise
     * @throws Exception
     */
    public boolean validateEdgeID(String edgeID) throws Exception {
        List<String> edgeIDs = this.edgeManager.getIDList();
        return edgeIDs.contains(edgeID);
    }


    /**
     * Inject vehicle: single and batch injection, create route first then create vehicles on that route. <br>
     * Route ID format: [currentTime] <br>
     * Vehicle ID format: [routeID]_[index]
     * @param numVehicles Number of vehicles to inject
     * @param start_edge_ID Starting edge ID
     * @param end_edge_ID Ending edge ID
     * @param color Color of the vehicles
     * @throws Exception
     */
    public void injectVehicle(int numVehicles, String start_edge_ID, String end_edge_ID, String color, String speed) throws Exception {
        // Generate a unique ID
        String routeID = (long) System.currentTimeMillis() + "";
        this.routeManager.add(routeID, start_edge_ID, end_edge_ID);
        for (int i = 0; i < numVehicles; i++) {
            String vehicleID = routeID + "_" + i;
            this.vehicleManager.add(vehicleID, routeID, color, speed);
        }
    }

    
    /**
     * Inject vehicle: stress test tool, inject 100 vehicles on up to 10 different random routes. 
     *      Create routes first then create vehicles on those routes. <br>
     * Route ID format: [currentTime]_[index] <br>
     * Vehicle ID format: [routeID]_[index_of_vehicle_in_this_stress_test]
     * @param number_of_vehicles Number of vehicles to inject
     * @param start_edge_ID Edge ID to inject to
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
        String ID = (long) System.currentTimeMillis() + "";
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
                this.vehicleManager.add(ID + "_" + j + "_" + i, ID + "_" + j, colorList.get(0), "max");
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
     * @param tlID Traffic light ID
     * @throws Exception
     */
    public void toggleSingleTl(String tlID) throws Exception {
        this.trafficLightManager.nextPhase(tlID);
    }


    /**
     * Get mapping data: edges
     * @return List of EdgeData representing edges shape to be rendered
     * @throws Exception
     */
    public List<EdgeData> getMapEdges() throws Exception {
        return this.edgeManager.getEdgeDataList();
    }
    /**
     * Get mapping data: junctions
     * @return List of JunctionData representing junctions shape to be rendered
     * @throws Exception
     */
    public List<JunctionData> getMapJunctions() throws Exception {
        return this.junctionManager.getJunctionDataList();
    }
    /**
     * Get mapping data: vehicles
     * @return List of VehicleData representing vehicles shape to be rendered
     * @throws Exception
     */
    public List<VehicleData> getMapVehicles() throws Exception {
        return this.vehicleManager.getVehicleDataList();
    }
    /**
     * Set mapping data: filter vehicles by color and edge
     * @throws Exception
     */
    public void setVehicleFilter(String color, String edgeID) throws Exception {
        vehicleManager.setFilter(color, edgeID);
    }
    /**
     * Get mapping data: traffic lights
     * @return List of TrafficLightData representing traffic lights shape to be rendered
     * @throws Exception
     */
    public List<TrafficLightData> getMapTls() throws Exception {
        return this.trafficLightManager.getTrafficLightDataList();
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
     * @param edgeID ID of the edge
     * @return Formatted tooltip string
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
     * Get tooltip: traffic lights
     * @param tlID ID of the traffic light
     * @return Formatted tooltip string
     * @throws Exception
     */
    public String getTlTooltip(String tlID) throws Exception {
        return String.format(
"Traffic Light ID: %s (%d phase) controlled Junction: %s\n Currently at phase: %d (Total: %.0f seconds)\n Remain: %.0f seconds \n Current time step: %.0f",  
                    tlID, trafficLightManager.getPhaseCount(tlID), tlID,
                    trafficLightManager.getPhaseID(tlID), trafficLightManager.getDuration(tlID),
                    trafficLightManager.getNextSwitch(tlID),
                    conn.getCurrentStep()
                );
    }
    /**
     * Get statistic: vehicle
     * @param vehicleID ID of the vehicle
     * @return Formatted statistic string for Dashboard
     * @throws Exception
     */
    public String getVehicleStats(String vehicleID) throws Exception {
        return String.format("Vehicle ID: %s\n Speed: %.2f km/h\n Is running on edge: %s",  
                    vehicleID, 
                    vehicleManager.getSpeed(vehicleID), 
                    vehicleManager.getAngle(vehicleID),
                    vehicleManager.getEdgeID(vehicleID)
                );
    }


    /**
     * Debug tool (might for future dashboard): return edge ID vehicle running on
     * @param vehicleID ID of the vehicle
     * @return ID of the edge the vehicle is on
     * @throws Exception
     */
    public String vehIsOnEdge(String vehicleID) throws Exception {
        return this.vehicleManager.getEdgeID(vehicleID);
    }
}
