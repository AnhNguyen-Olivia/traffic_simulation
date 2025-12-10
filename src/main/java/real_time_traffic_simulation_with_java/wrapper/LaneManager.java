package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;

import java.util.List;
import real_time_traffic_simulation_with_java.cores.LaneData;

public class LaneManager {
    private final SumoTraciConnection conn;
    private List<LaneData> laneDataList = new java.util.ArrayList<>();


    // Connection to Sumo
    public LaneManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    // Get list of lane IDs & number of lanes
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


    // Get length of the lane (m), there is no width attribute, but sumo define default width as 3.2m
    public double getLength(String laneID) throws Exception {
        return (double) conn.do_job_get(Lane.getLength(laneID));
    }


    // Get coordinations of lanes (list of double arrays [x,y])
    public List<SumoPosition2D> getCoordinateList(String laneID) throws Exception {
        SumoGeometry a = (SumoGeometry) conn.do_job_get(Lane.getShape(laneID));
        return a.coords;
    }

    // Create lane data list
    public List<LaneData> getLaneDataList() throws Exception {
        if(laneDataList.isEmpty()){
            List<String> IDs = this.getIDList();
            for (String id : IDs) {
                LaneData lanedata = new LaneData(
                        id,
                        this.getEdgeID(id),
                        this.getLength(id),
                        3.2, // default width in sumo
                        this.getCoordinateList(id)
                );
                laneDataList.add(lanedata);
            }
        }
        return laneDataList;
    }
}
