package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;

import java.util.List;
import real_time_traffic_simulation_with_java.cores.LaneData;

/**
 * LaneManager is a wrapper class for SumoTraciConnection to manage lanes in the simulation
 * @TestedCompleted
 */

public class LaneManager {

    /**
     * private SumoTraciConnection conn
     * private List<LaneData> laneDataList
    */
    private final SumoTraciConnection conn;
    private List<LaneData> laneDataList = new java.util.ArrayList<>();

    /**
     * Connection to Sumo
     * @param connection
     * @throws Exception
    */
    public LaneManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    /**
     * Get list of lane IDs, including junction lanes
     * @return a List type String of lane IDs, including junction lanes
     * @throws Exception
     * @Tested
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(Lane.getIDList());
    }


    /**
     * Get number of lanes, including junction lanes
     * @return an int number of lanes, including junction lanes
     * @throws Exception
     * @Tested
    */ 
    public int getCount() throws Exception {
        return (int) conn.do_job_get(Lane.getIDCount());
    }


    /**
     * Get edge ID that the lane belongs to
     * @return a String edge ID that the lane belongs to
     * @throws Exception
     * @Tested
    */ 
    public String getEdgeID(String laneID) throws Exception {
        return (String) conn.do_job_get(Lane.getEdgeID(laneID));
    }


    /**
     * Get length of the lane (m), there is no width attribute, but sumo defines default width as 3.2m
     * @return a double length of the lane (m)
     * @throws Exception
     * @Tested
    */ 
    public double getLength(String laneID) throws Exception {
        return (double) conn.do_job_get(Lane.getLength(laneID));
    }


    /**
     * Get coordinations of the lane
     * @return a List of SumoPosition2D type coordinations of the lane
     * @throws Exception
     * @Tested
    */
    @SuppressWarnings("unchecked")
    public SumoGeometry getCoordinateList(String laneID) throws Exception {
        return (SumoGeometry) conn.do_job_get(Lane.getShape(laneID));
    }


    /**
     * Create and get a List of LaneData for all lanes
     * @return a List of LaneData for all lanes
     * @throws Exception
     * @Tested
    */
    public List<LaneData> getLaneDataList() throws Exception {
        if(laneDataList.isEmpty()){
            List<String> IDs = this.getIDList();
            for (String id : IDs) {
                LaneData lanedata = new LaneData(
                        id,
                        this.getEdgeID(id),
                        this.getLength(id), // default width in sumo
                        this.getCoordinateList(id)
                );
                laneDataList.add(lanedata);
            }
        }
        return laneDataList;
    }
}
