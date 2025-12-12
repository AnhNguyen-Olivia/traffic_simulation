package real_time_traffic_simulation_with_java.cores;
import java.util.List;

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
    }


    /**
     * Get all IDs: edges
     * @throws Exception
     */
    /**
     * Get all IDs: junctions
     * @throws Exception
     */
    /**
     * Get all IDs: vehicles
     * @throws Exception
     */
    /**
     * Get all IDs: traffic lights
     * @throws Exception
     */


    /**
     * Control Simulation: start 
     * @throws Exception
     */
    public void startSimulation() throws Exception {
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
     * Inject vehicle: single and batch injection
     */
    /**
     * Inject vehicle: stress test tool
     */


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
    public List<EdgeData> getMapEdges() throws Exception {
        return this.edgeManager.getEdgeDataList();
    }
    /**
     * Get mapping data: junctions
     */
    public List<JunctionData> getMapJunctions() throws Exception {
        return this.junctionManager.getJunctionDataList();
    }
    /**
     * Get mapping data: vehicles
     */
    public List<VehicleData> getMapVehicles() throws Exception {
        return this.vehicleManager.getVehicleDataList();
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
