package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import java.util.List;

public class LaneManager {
    private final SumoTraciConnection conn;

    // Connection to Sumo
    public LaneManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    // Get list of edge IDs & number of lanes
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getIDList());
    }

    public int getCount() throws Exception {
        return (int) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getIDCount());
    }


    // Get edge ID of the lane
    public String getEdgeID(String laneID) throws Exception {
        return (String) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getEdgeID(laneID));
    }


    // Get length & width of the lane (m)
    public double getLength(String laneID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getParameter(laneID, "length"));
    }

    public double getWidth(String laneID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getParameter(laneID, "width"));
    }


    // Get coordinations of lanes (list of double arrays [x,y])
    @SuppressWarnings("unchecked")
    public List<double[]> getCoordinateList(String laneID) throws Exception {
        return (List<double[]>) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getShape(laneID));
    }
}
