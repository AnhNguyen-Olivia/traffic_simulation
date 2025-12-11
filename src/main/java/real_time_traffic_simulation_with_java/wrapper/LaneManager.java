package real_time_traffic_simulation_with_java.wrapper;

import java.util.ArrayList;
import java.util.List;

import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;

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
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getLength(laneID));
    }

    public double getWidth(String laneID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getWidth(laneID));
    }


    // Get coordinations of lanes (list of double arrays [x,y])
    public List<double[]> getCoordinateList(String laneID) throws Exception {
        SumoGeometry geometry = 
            (SumoGeometry) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getShape(laneID));
        
        // Convert LinkedList<SumoPosition2D> to List<double[]>
        List<double[]> coordinates = new ArrayList<>();
        for (SumoPosition2D pos : geometry.coords) {
            coordinates.add(new double[]{pos.x, pos.y});
        }
        return coordinates;
    }
}
