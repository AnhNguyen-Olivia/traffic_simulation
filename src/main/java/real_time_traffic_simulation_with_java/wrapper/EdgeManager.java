package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Lane;

import java.util.ArrayList;
import java.util.List;

public class EdgeManager {
    private final SumoTraciConnection conn;

    // Connection to Sumo
    public EdgeManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    // Get list of edge IDs & number of edges
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(Edge.getIDList());
    }

    public int getCount() throws Exception {
        return (int) conn.do_job_get(Edge.getIDCount());
    }


    // Get number of lanes & lane IDs on the edge
    public int getLaneCount(String edgeID) throws Exception {
        return (int) conn.do_job_get(Edge.getLaneNumber(edgeID));
    }

    public List<String> getLaneIDList(String edgeID) throws Exception {
        List<String> laneIDs = new ArrayList<>();
        for(int i=0; i<getLaneCount(edgeID); i++){
            laneIDs.add(edgeID + "_" + i);
        }
        return laneIDs;
    }


    // Get max speed allowed on the edge (m/s)
    public double getMaxSpeed(String edgeID) throws Exception {
        return (double) conn.do_job_get(Lane.getMaxSpeed(edgeID + "_0"));
    }


    // Get length of the edge (m)
    public double getLength(String edgeID) throws Exception {
        return (double) conn.do_job_get(Lane.getLength(edgeID + "_0"));
    }


    // Get vehicle ID and number of vehicles on the edge last step
    @SuppressWarnings("unchecked")
    public List<String> getVehicleIDList(String edgeID) throws Exception {
        return (List<String>) conn.do_job_get(Edge.getLastStepVehicleIDs(edgeID));
    }

    public int getVehicleCount(String edgeID) throws Exception {
        return (int) conn.do_job_get(Edge.getLastStepVehicleNumber(edgeID));
    }


    // Get average speed of vehicles on the edge last step (m/s)
    public double getAverageSpeed(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getLastStepMeanSpeed(edgeID));
    }


    // Get density of vehicles on the edge last step (vehicle/m)
    public double getDensity(String edgeID) throws Exception {
        double vehicleCount = (int) conn.do_job_get(Edge.getLastStepVehicleNumber(edgeID));
        return vehicleCount / this.getLength(edgeID);
    }


    // Get estimate travel time on the edge last step (s)
    public double getTravelTime(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getTraveltime(edgeID));
    }

}