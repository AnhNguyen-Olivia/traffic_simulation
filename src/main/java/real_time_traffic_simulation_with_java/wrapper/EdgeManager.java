package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.objects.SumoGeometry;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import real_time_traffic_simulation_with_java.cores.EdgeData;


/**
 * Wrapper class for TraaS to manage edges in the simulation
 */
public class EdgeManager {
    private static final Logger LOGGER = Logger.getLogger(EdgeManager.class.getName());

    /** Connection to Sumo */
    private final SumoTraciConnection conn;
    /** Stores List of visualization objects for edges */
    private List<EdgeData> edgeDataList = new ArrayList<>();

    /**
     * Wrapper class for TraaS to manage edges in the simulation
     * @param connection connection to Sumo
    */
    public EdgeManager(SumoTraciConnection connection) {
        this.conn = connection;
    }


    /**
     * Get list of edge IDs, excluding junction edges
     * @return a List type String of edge IDs, excluding junction edges
    */ 
    public List<String> getIDList() throws Exception {
        List<String> IDs = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<String> allEdgeIDList = (List<String>) conn.do_job_get(Edge.getIDList());
        for (String ID : allEdgeIDList) {
            if (ID.startsWith(":J")) {
                continue;
            }
            IDs.add(ID);
        }
        return IDs;
    }

    
    /** 
     * Get list of congested edges IDs
     * @return a List type String of congested edge IDs, excluding junction edges
     */
    public List<String> getCongestedEdgeIDList() {
        List<String> congestedEdgeIDs = new ArrayList<>();
        for (EdgeData edge : this.edgeDataList) {
            if (edge.isCongested()) {
                congestedEdgeIDs.add(edge.getId());
            }
        }
        return congestedEdgeIDs;
    }


    /**
     * Get congested status of the edge
     * @param edgeID the ID of the edge
     * @return boolean congested status of the edge
     */
    public boolean getCongestedStatus(String edgeID) {
        for (EdgeData edge : this.edgeDataList) {
            if (edge.getId().equals(edgeID)) {
                return edge.isCongested();
            }
        }
        // LOGGER.log(Level.WARNING, "Edge ID: " + edgeID + " not found in EdgeDataList.");
        return false;
    }


    /**
     * Get number of edges, excluding junction edges
     * @return an int number of edges, excluding junction edges
    */
    public int getCount() {
        try{
            return this.getIDList().size();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get number of edges.", e);
            return -1;
        }
    }


    /**
     * Get number of lanes on the edge
     * @param edgeID the ID of the edge
     * @return an int number of lanes on the edge
    */ 
    public int getLaneCount(String edgeID) throws Exception {
        return (int) conn.do_job_get(Edge.getLaneNumber(edgeID));
    }


    /**
     * Get lane IDs on the edge
     * @param edgeID the ID of the edge
     * @return a List of lane IDs on the edge
    */ 
    public List<String> getLaneIDList(String edgeID) throws Exception {
        List<String> laneIDs = new ArrayList<>();
        for(int i=0; i<getLaneCount(edgeID); i++){
            laneIDs.add(edgeID + "_" + i);
        }
        return laneIDs;
    }


    /**
     * Get coordinations of lanes the edge
     * @param edgeID the ID of the edge
     * @return a List of SumoGeometry type coordinations of the lanes on the edge
    */
    public List<SumoGeometry> getLanesCoordinate(String edgeID) throws Exception {
        List<SumoGeometry> laneCoords = new ArrayList<>();
        for(String laneID : getLaneIDList(edgeID)){
            SumoGeometry coords = (SumoGeometry) conn.do_job_get(Lane.getShape(laneID));
            laneCoords.add(coords);
        }
        return laneCoords;
    }


    /**
     * Get max speed allowed on the edge (km/h)
     * @param edgeID the ID of the edge
     * @return a double max speed allowed on the edge (km/h)
    */ 
    public double getMaxSpeed(String edgeID) {
        try{
            return (double) conn.do_job_get(Lane.getMaxSpeed(edgeID + "_0")) * 3.6;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get edge max speed from SUMO for edge ID: " + edgeID, e);
            return -1;
        }
    }


    /**
     * Get length of the edge (m)
     * @param edgeID the ID of the edge
     * @return a double length of the edge (m)
    */ 
    public double getLength(String edgeID) {
        try{
            return (double) conn.do_job_get(Lane.getLength(edgeID + "_0"));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get edge length from SUMO for edge ID: " + edgeID, e);
            return -1;
        }
    }

    
    /**
     * Get number of vehicles on the edge in the last step
     * @param edgeID the ID of the edge
     * @return an int number of vehicles on the edge in the last step
    */
    public int getVehicleCount(String edgeID) {
        try{
            return (int) conn.do_job_get(Edge.getLastStepVehicleNumber(edgeID));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get edge vehicle count from SUMO for edge ID: " + edgeID, e);
            return -1;
        }
    }


    /**
     * Get average speed of vehicles on the edge in the last step (km/h)
     * @param edgeID the ID of the edge
     * @return a double average speed of vehicles on the edge in the last step (km/h)
    */
    public double getAverageSpeed(String edgeID) {
        try{
            return (double) conn.do_job_get(Edge.getLastStepMeanSpeed(edgeID)) * 3.6;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get edge average speed from SUMO for edge ID: " + edgeID, e);
            return -1;
        }
    }


    /**
     * Get density of vehicles on the edge in the last step (vehicle/km)
     * @param edgeID the ID of the edge
     * @return a double density of vehicles on the edge in the last step (vehicle/km)
    */
    public double getDensity(String edgeID) {
        try{
            double vehicleCount = (int) conn.do_job_get(Edge.getLastStepVehicleNumber(edgeID));
            return vehicleCount / this.getLength(edgeID) * 1000;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get edge density from SUMO for edge ID: " + edgeID, e);
            return -1;
        }
    }


    /**
     * Get estimate travel time on the edge in the last step (s)
     * @param edgeID the ID of the edge
     * @return a double estimate travel time on the edge in the last step (s)
    */
    public double getTravelTime(String edgeID) {
        try{
            return (double) conn.do_job_get(Edge.getTraveltime(edgeID));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get edge travel time from SUMO for edge ID: " + edgeID, e);
            return -1;
        }
    }


    /**
     * Get number of halting vehicles on the edge in the last step
     * @param edgeID the ID of the edge
     * @return an int number of halting vehicles on the edge in the last step
    */
    public int getHaltingNumber(String edgeID) throws Exception {
        return (int) conn.do_job_get(Edge.getLastStepHaltingNumber(edgeID));
    }


    /**
     * Create and get a List of EdgeData for all edges
     * @return a List of EdgeData for all edges
     * @throws Exception
    */
    public List<EdgeData> getEdgeDataList() throws Exception {
        if(edgeDataList.isEmpty()){
            List<String> IDs = this.getIDList();
            for (String id : IDs) {
                EdgeData edgedata = new EdgeData(
                        id,
                        this.getLaneCount(id),
                        this.getLanesCoordinate(id)
                );
                edgeDataList.add(edgedata);
            }
        }
        return edgeDataList;
    }


    /**
     * Update congestion status for all edges based on TTI and set edge colors
     * @throws Exception
     */
    public void updateEdgeDataList() throws Exception {
        for (EdgeData edge : this.edgeDataList) {
            // Halting rate = halting vehicles / number of lanes
            edge.updateCongestedStatus(this.getHaltingNumber(edge.getId())/this.getLaneCount(edge.getId()));
            edge.setColor();
        }
    }

}