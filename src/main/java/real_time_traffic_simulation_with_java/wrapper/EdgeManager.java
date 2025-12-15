package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.objects.SumoGeometry;

import java.util.ArrayList;
import java.util.List;
import real_time_traffic_simulation_with_java.cores.EdgeData;


/**
 * Wrapper class for TraaS to manage edges in the simulation
 */
public class EdgeManager {

    /** Connection to Sumo */
    private final SumoTraciConnection conn;
    /** Stores List of visualization objects for edges */
    private List<EdgeData> edgeDataList = new java.util.ArrayList<>();

    /**
     * Wrapper class for TraaS to manage edges in the simulation
     * @param connection connection to Sumo
     * @throws Exception
    */
    public EdgeManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    /**
     * Get list of edge IDs, excluding junction edges
     * @return a List type String of edge IDs, excluding junction edges
     * @throws Exception
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
     * Get number of edges, excluding junction edges
     * @return an int number of edges, excluding junction edges
     * @throws Exception
    */
    public int getCount() throws Exception {
        return this.getIDList().size();
    }


    /**
     * Get number of lanes on the edge
     * @param edgeID the ID of the edge
     * @return an int number of lanes on the edge
     * @throws Exception
    */ 
    public int getLaneCount(String edgeID) throws Exception {
        return (int) conn.do_job_get(Edge.getLaneNumber(edgeID));
    }


    /**
     * Get lane IDs on the edge
     * @param edgeID the ID of the edge
     * @return a List of lane IDs on the edge
     * @throws Exception
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
     * @throws Exception
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
     * @throws Exception
    */ 
    public double getMaxSpeed(String edgeID) throws Exception {
        return (double) conn.do_job_get(Lane.getMaxSpeed(edgeID + "_0")) * 3.6;
    }


    /**
     * Get length of the edge (m)
     * @param edgeID the ID of the edge
     * @return a double length of the edge (m)
     * @throws Exception
    */ 
    public double getLength(String edgeID) throws Exception {
        return (double) conn.do_job_get(Lane.getLength(edgeID + "_0"));
    }


    /**
     * Get vehicle ID and number of vehicles on the edge in the last step
     * @param edgeID the ID of the edge
     * @return an List type String of vehicle IDs on the edge in the last step
     * @throws Exception
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getVehicleIDList(String edgeID) throws Exception {
        return (List<String>) conn.do_job_get(Edge.getLastStepVehicleIDs(edgeID));
    }

    
    /**
     * Get number of vehicles on the edge in the last step
     * @param edgeID the ID of the edge
     * @return an int number of vehicles on the edge in the last step
     * @throws Exception
    */
    public int getVehicleCount(String edgeID) throws Exception {
        return (int) conn.do_job_get(Edge.getLastStepVehicleNumber(edgeID));
    }


    /**
     * Get average speed of vehicles on the edge in the last step (km/h)
     * @param edgeID the ID of the edge
     * @return a double average speed of vehicles on the edge in the last step (km/h)
     * @throws Exception
    */
    public double getAverageSpeed(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getLastStepMeanSpeed(edgeID)) * 3.6;
    }


    /**
     * Get density of vehicles on the edge in the last step (vehicle/km)
     * @param edgeID the ID of the edge
     * @return a double density of vehicles on the edge in the last step (vehicle/km)
     * @throws Exception
    */
    public double getDensity(String edgeID) throws Exception {
        double vehicleCount = (int) conn.do_job_get(Edge.getLastStepVehicleNumber(edgeID));
        return vehicleCount / this.getLength(edgeID) * 1000;
    }


    /**
     * Get estimate travel time on the edge in the last step (s)
     * @param edgeID the ID of the edge
     * @return a double estimate travel time on the edge in the last step (s)
     * @throws Exception
    */
    public double getTravelTime(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getTraveltime(edgeID));
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

}