package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Lane;
import java.util.List;

public class LaneWrapper {
    private final SumoTraciConnection conn;

    // Connection to Sumo
    public LaneWrapper(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    // Get list of edge IDs & number of lanes
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(Lane.getIDList());
    }

    public int getCount() throws Exception {
        return (int) conn.do_job_get(Lane.getIDCount());
    }


    // Get edge ID of the lane
    public String getEdgeID(String laneID) throws Exception {
        return (String) conn.do_job_get(Lane.getEdgeID(laneID));
    }


    // Get length & width of the lane (m)
    public double getLength(String laneID) throws Exception {
        return (double) conn.do_job_get(Lane.getParameter(laneID, "length"));
    }

    public double getWidth(String laneID) throws Exception {
        return (double) conn.do_job_get(Lane.getParameter(laneID, "width"));
    }


    // Get coordinations of lanes (list of double arrays [x,y])
    @SuppressWarnings("unchecked")
    public List<double[]> getCoordinateList(String laneID) throws Exception {
        return (List<double[]>) conn.do_job_get(Lane.getShape(laneID));
    }
}
