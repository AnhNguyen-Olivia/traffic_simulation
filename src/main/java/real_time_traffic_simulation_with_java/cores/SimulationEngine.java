package real_time_traffic_simulation_with_java.cores;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level; 
import java.util.Collections;

import real_time_traffic_simulation_with_java.wrapper.*;
import real_time_traffic_simulation_with_java.alias.Color;


/**
 * Core to control the simulation, center of backend operations. <br>
 * Manages all other manager classes.
 */
public class SimulationEngine {
    private static final Logger LOGGER = Logger.getLogger(SimulationEngine.class.getName());

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


    // ----------------------------------------------------------------------------
    // Simulation Control Methods
    // ----------------------------------------------------------------------------
    /**
     * Control simulation: advance simulation by one step & update edge congestion status and traffic light states
     */
    public void stepSimulation() throws IllegalStateException {
        try {this.conn.nextStep();} catch(IllegalStateException e){
            LOGGER.log(Level.SEVERE, "Simulation has ended or connection lost: ", e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error advancing simulation step: ", e);
            return; // Stop method execution here if step fails
        }
        try {this.edgeManager.updateEdgeDataList();}catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to re-render edge.", e);
        }
        try {this.trafficLightManager.updateTrafficLightDataList();} catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to re-render traffic lights.", e);
        }
    }
    /**
     * Control simulation: stop
     */
    public void stopSimulation() {
        try {this.conn.closeConnection();} catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error closing Sumo connection: ", e);
        }
    }


    // ----------------------------------------------------------------------------
    // Object ID Management Methods
    // ----------------------------------------------------------------------------
    /**
     * Get all IDs: edges
     * @return List of String edge IDs
     * @throws Exception
     */
    public List<String> getAllEdgeIDs() throws IllegalStateException {
        try{
            return this.edgeManager.getIDList();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve edge IDs.", e);
            return new ArrayList<>();
        }
    }
    /**
     * Get all IDs: vehicles
     * @return List of String vehicle IDs
     * @throws Exception
     */
    public List<String> getAllVehicleIDs() throws IllegalStateException {
        try {
            return this.vehicleManager.getIDList();
        } catch (IllegalStateException e) {
            throw e;
        }catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve vehicle IDs.", e);
            return new ArrayList<>();
        }
    }
    /**
     * Get all IDs: traffic lights
     * @return List of String traffic light IDs
     * @throws Exception
     */
    public List<String> getAllTrafficLightIDs() throws IllegalStateException {
        try {
            return this.trafficLightManager.getIDList();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve traffic light IDs.", e);
            return new ArrayList<>();
        }
    }


    /**
     * Inject vehicle: single and batch injection, create route first then create vehicles on that route. <br>
     * Route ID format: [currentTime] <br>
     * Vehicle ID format: [routeID]_[index]
     * @param numVehicles Number of vehicles to inject
     * @param start_edge_ID Starting edge ID
     * @param end_edge_ID Ending edge ID
     * @param color Color of the vehicles
     */
    public void injectVehicle(int numVehicles, String start_edge_ID, String end_edge_ID, String color, String speed) {
        // Generate a unique ID
        String routeID = (long) System.currentTimeMillis() + "";
        try {this.routeManager.add(routeID, start_edge_ID, end_edge_ID);} catch (Exception e) {return;}
        for (int i = 0; i < numVehicles; i++) {
            String vehicleID = routeID + "_" + i;
            this.vehicleManager.add(vehicleID, routeID, color, speed);
        }
        LOGGER.log(Level.INFO, String.format("Injected %d vehicles from %s to %s.", numVehicles, start_edge_ID, end_edge_ID));
    }

    
    // ----------------------------------------------------------------------------
    // Vehicle Injection Methods
    // ----------------------------------------------------------------------------
    /**
     * Inject vehicle: stress test tool, inject 100 vehicles on up to 10 different random routes. 
     *      Create routes first then create vehicles on those routes. <br>
     * Route ID format: [currentTime]_[index] <br>
     * Vehicle ID format: [routeID]_[index_of_vehicle_in_this_stress_test]
     * @param number_of_vehicles Number of vehicles to inject
     * @param start_edge_ID Edge ID to inject to
     */
    public void stressTest(int number_of_vehicles, String start_edge_ID) {
        // Randomly select 10 end_edge to inject vehicles, if less than 10 edges, use all edges
        List<String> edgeIDs;
        try {
            edgeIDs =  this.getAllEdgeIDs();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Stress test failed.");
            return;
        }
        Collections.shuffle(edgeIDs);
        int number_of_random_routes;
        if(edgeIDs.size() < 10) {number_of_random_routes = edgeIDs.size();} else {number_of_random_routes = 10;}
        List<String> end_edge_IDs = edgeIDs.subList(0, number_of_random_routes);
        // Generate a unique ID
        String ID = (long) System.currentTimeMillis() + "";
        //Generate color list to inject vehicles with random color
        List<String> colorList = new ArrayList<>(Color.ListofAllColor);
        // Generate n random routes to inject vehicles
        for (int j = 0; j < number_of_random_routes; j++) {
            // If route creation fails, generate route of 1 edge (start_edge to start_edge)
            try{this.routeManager.add(ID + "_" + j, start_edge_ID, end_edge_IDs.get(j));} catch (Exception e) {
                try{this.routeManager.add(ID + "_" + j, start_edge_ID, end_edge_IDs.get(j));} catch (Exception ex) {}
            }
        } 
        // Inject vehicles
        int i = 0;
        while (i < number_of_vehicles) {
            for (int j = 0; j < number_of_random_routes; j++) {
                Collections.shuffle(colorList);
                this.vehicleManager.add(ID + "_" + j + "_" + i, ID + "_" + j, colorList.get(0), "max");
                i++;
                if (i >= number_of_vehicles) {
                    break;
                }
            }
        }
        LOGGER.log(Level.INFO, String.format("Stress test: Injected %d vehicles to edge: %s.", number_of_vehicles, start_edge_ID));
    }


    // ----------------------------------------------------------------------------
    // Traffic Light Control Methods
    // ----------------------------------------------------------------------------
    /**
     * Control traffic lights: toggle all traffic lights
     */
    public void toggleAllTls() {
        List<String> tlsIDs;
        try{tlsIDs = this.trafficLightManager.getIDList();} catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to toggle all traffic lights.");
            return;
        }
        for (String tlsID : tlsIDs) {
            this.trafficLightManager.nextPhase(tlsID);
        }
        LOGGER.log(Level.INFO, "Toggled all traffic lights.");
    }
    /**
     * Control traffic light: toggle single traffic light
     * @param tlID Traffic light ID
     */
    public void toggleSingleTl(String tlID) {
        this.trafficLightManager.nextPhase(tlID);
        LOGGER.log(Level.INFO, "Toggled traffic light: " + tlID);
    }
    /**
     * Control traffic light: set phase durations for a traffic light
     * @param tlID Traffic light ID
     * @param durations List of durations for each phase
     */
    public void setTlPhaseDurations(String tlID, List<Integer> durations) {
        this.trafficLightManager.setPhaseDuration(tlID, durations);
        LOGGER.log(Level.INFO, "Set phase durations for traffic light: " + tlID + " to " + durations.toString());
    }


    // ----------------------------------------------------------------------------
    // Data Retrieval Methods for Map Rendering
    // ----------------------------------------------------------------------------
    /**
     * Get mapping data: edges
     * @return List of EdgeData representing edges shape to be rendered
     */
    public List<EdgeData> getMapEdges() {
        try {return this.edgeManager.getEdgeDataList();} catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve edge data for map rendering.", e);
            return new ArrayList<>();
        }
    }
    /**
     * Get mapping data: junctions
     * @return List of JunctionData representing junctions shape to be rendered
     */
    public List<JunctionData> getMapJunctions() {
        try {return this.junctionManager.getJunctionDataList();} catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve junction data for map rendering.", e);
            return new ArrayList<>();
        }
    }
    /**
     * Get mapping data: vehicles
     * @return List of VehicleData representing vehicles shape to be rendered
     */
    public List<VehicleData> getMapVehicles(){
        try {return this.vehicleManager.getVehicleDataList();} catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve vehicle data for map rendering.", e);
            return new ArrayList<>();
        }
    }
    /**
     * Set mapping data: filter vehicles by color and edge
     */
    public void setVehicleFilter(String color, String edgeID) {
        vehicleManager.setFilter(color, edgeID);
        if (color.equals("") && edgeID.equals("")) {
            LOGGER.log(Level.INFO, "Clear vehicle filter.");
        } else {
            String colorString = color.equals("") ? "" : color + " ";
            String edgeString = edgeID.equals("") ? "" : " on edge " + edgeID;
            LOGGER.log(Level.INFO, String.format("Filter %svehicles%s.", colorString, edgeString));
        }
    }
    /**
     * Get mapping data: traffic lights
     * @return List of TrafficLightData representing traffic lights shape to be rendered
     */
    public List<TrafficLightData> getMapTls() {
        try {return this.trafficLightManager.getTrafficLightDataList();} catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve traffic light data for map rendering.", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get tooltip: edges
     * @param edgeID ID of the edge
     * @return Formatted tooltip string
     */
    public String getEdgeTooltip(String edgeID) {
        int laneCount;
        try {
            laneCount = edgeManager.getLaneCount(edgeID);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get lane count for edge ID: " + edgeID, e);
            laneCount = -1;
        }
        return String.format(
"Edge ID: %s (%d lane), Max speed: %.2f km/h, Length: %.2f m\n Vehicle Count: %d, Average Speed: %.2f km/h\nDensity: %.2f veh/km, Estimated Travel Time: %.2f s",  
                    edgeID, laneCount, 
                    edgeManager.getMaxSpeed(edgeID), edgeManager.getLength(edgeID),
                    edgeManager.getVehicleCount(edgeID), edgeManager.getAverageSpeed(edgeID),
                    edgeManager.getDensity(edgeID), edgeManager.getTravelTime(edgeID)
                );
    }
    /**
     * Get tooltip: traffic lights
     * @param tlID ID of the traffic light
     * @return Formatted tooltip string
     */
    public String getTlTooltip(String tlID) {
        int phaseCount;
        try {
            phaseCount = trafficLightManager.getPhaseCount(tlID);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get phase count for traffic light ID: " + tlID, e);
            phaseCount = -1;
        }
        return String.format(
"Traffic Light ID: %s (%d phase) controlled Junction: %s\n Currently at phase: %d (Total: %.0f seconds)\n Remain: %.0f seconds",  
                    tlID, phaseCount, tlID,
                    trafficLightManager.getPhaseID(tlID), trafficLightManager.getDuration(tlID),
                    trafficLightManager.getNextSwitch(tlID)
                );
    }


    // ----------------------------------------------------------------------------
    // Statistics Retrieval Methods for Dashboard
    // ----------------------------------------------------------------------------
    /**
     * Get statistics: current time step
     * @return Formatted statistic string for Dashboard
     */
    public String getCurrentTimeStep() throws IllegalStateException{
        try {return String.format("Current Time Step: %.1f", this.conn.getCurrentStep());
        } catch(IllegalStateException e){
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve current time step.", e);
            return "Current Time Step: N/A";
        }
    }
    /**
     * Get statistics: number of entities in the simulation
     * @return Formatted statistic string for Dashboard
     */
    public String getBasicInfo() {
        return String.format("    Current number of vehicles in simulation: %d\n    Total edges: %d\n    Total traffic lights: %d",   
                    vehicleManager.getCount(),
                    edgeManager.getCount(),
                    trafficLightManager.getCount()
                );
    }
    /**
     * Get statistics: congestion hotspots
     * @return Formatted statistic string of congested edge IDs for Dashboard
     */
    public String getCongestionHotspots() {
        List<String> congestedEdgeIDs = edgeManager.getCongestedEdgeIDList();
        if (congestedEdgeIDs.size() == 0) {
            return "No congestion hotspots detected.";
        }
        return String.format("%d Congestion hotspots (edges)\n    ID: %s",  
                    congestedEdgeIDs.size(),
                    String.join(", ", congestedEdgeIDs)
                );
    }
    /**
     * Get statistics: chart values (average speed (km/h), density (veh/km), halting number (veh)) of a specific edge
     * @param vehicleID ID of the vehicle
     * @return Formatted statistic string for Dashboard
     */
    public double[] getEdgeStats(String edgeID) throws IllegalStateException {
        int haltingNumber;
        try{
            haltingNumber = edgeManager.getHaltingNumber(edgeID);
        } catch(IllegalStateException e){
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get halting number for edge ID: " + edgeID, e);
            haltingNumber = -1;
        }
        return new double[] {
            edgeManager.getAverageSpeed(edgeID),
            edgeManager.getDensity(edgeID),
            haltingNumber
        };
    }


    // ---------------------------------------------------------------------------
    // CSV & PDF Data Preparation Methods
    // ---------------------------------------------------------------------------
    /**
     * Prepare data for CSV logging: "Simulation step", "vehicle id","vehicle color", "vehicle speed",
                                                "vehicle is on edge", "edge congestion status", 
                                                "edge average speed", "edge density"
     * @return List of String arrays representing data rows
     */
    public List<String[]> dataForCSV() throws IllegalStateException {
        String currentTimeStep = this.getTimeStepForCSV();
        List<String> vehicleIDs = this.getAllVehicleIDs();
        // Return data
        List<String[]> data = new ArrayList<>();
        for (String vehicleID: vehicleIDs) {
            String color = this.getVehicleColorForCSV(vehicleID);
            String speed = this.vehicleManager.getSpeed(vehicleID) == -1? 
                            "" : String.format("%.2f", this.vehicleManager.getSpeed(vehicleID));
            String edgeID = this.getVehicleEdgeIDForCSV(vehicleID);
            String edgeCongestionStatus = (edgeID.equals("") || edgeID.startsWith(":") ||edgeID.startsWith("J") || edgeID.startsWith("-J"))? 
                            "" : String.valueOf(this.edgeManager.getCongestedStatus(edgeID));
            String edgeAverageSpeed = this.getEdgeStatForCSV(edgeID, "average_speed");
            String edgeDensity = this.getEdgeStatForCSV(edgeID, "density");
            String[] row = {currentTimeStep, vehicleID, color, speed, 
                            edgeID, edgeCongestionStatus, edgeAverageSpeed, edgeDensity};
            data.add(row);
        }
        return data;
    }


    /**
     * Prepare data for PDF summary: 
     *      1st element is {edgeCount, tlsCount}, 
     *      2nd element is {exportedSimulationStep},
     *      following elements are {edgeID, laneCount, length}
     * @return
     * @throws IllegalStateException
     */
    public List<String[]> dataForPDF() throws IllegalStateException {
        List<String[]> data = new ArrayList<>();
        // 1st element: {edgeCount, tlsCount}
        String edgeCount = this.edgeManager.getCount() == -1 ? 
                "N/A" : String.valueOf(this.edgeManager.getCount());
        String tlsCount = this.trafficLightManager.getCount() == -1 ? 
                "N/A" : String.valueOf(this.trafficLightManager.getCount());
        data.add(new String[]{edgeCount, tlsCount});
        // 2nd element: {exportedSimulationStep}
        String exportedSimulationStep = this.getTimeStepForCSV();
        data.add(new String[]{exportedSimulationStep});
        // Next elements: {edgeID, laneCount, length}
        List<String> edgeIDs = this.getAllEdgeIDs();
        for (String edgeID : edgeIDs) {
            String laneCount;
            try{
                laneCount = String.valueOf(this.edgeManager.getLaneCount(edgeID));
            } catch (IllegalStateException e){
                throw e;
            } catch (Exception e) {
                laneCount = "N/A";
            }
            String length = this.edgeManager.getLength(edgeID) == -1 ? 
                "N/A" : String.format("%.2f", this.edgeManager.getLength(edgeID));
            data.add(new String[]{edgeID, laneCount, length});
        }
        return data;
    }


    // ---------------------------------------------------------------------------
    // Private helper functions for CSV data preparation
    // ---------------------------------------------------------------------------
    /** Private helper function for CSV: Simulation step */
    private String getTimeStepForCSV() throws IllegalStateException {
        try {
            return String.format("%.1f", this.conn.getCurrentStep());
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to retrieve current time step for CSV.", e);
            return "";
        }
    }
    /** Private helper function for CSV: vehicle color */
    private String getVehicleColorForCSV(String vehicleID) throws IllegalStateException {
        try{
            return Color.colorToString(this.vehicleManager.getColor(vehicleID));
        } catch(IllegalStateException e){
            throw e; 
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get vehicle color from SUMO for vehicle ID: " + vehicleID, e);
            return "";
        }
    }
    /** Private helper function for CSV: vehicle edge ID */
    private String getVehicleEdgeIDForCSV(String vehicleID) throws IllegalStateException {
        try{
            return this.vehicleManager.getEdgeID(vehicleID);
        } catch(IllegalStateException e){
            throw e; 
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get vehicle edge ID from SUMO for vehicle ID: " + vehicleID, e);
            return "";
        }
    }
    /** Private helper function for CSV: edge congestion status */
    private String getEdgeStatForCSV(String edgeID, String stat_type) throws IllegalStateException {
        if(edgeID.equals("")) {
            return "";
        }
        double statValue;
        if (stat_type.equals("average_speed")) {
            statValue = this.edgeManager.getAverageSpeed(edgeID);
        } else if (stat_type.equals("density")) {
            statValue = this.edgeManager.getDensity(edgeID);
        } else {
            return "";
        }
        return statValue == -1 ? "" : String.format("%.2f", statValue);
    }

}