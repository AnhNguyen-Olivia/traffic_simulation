package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;

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
        return (List<String>) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getIDList());
    }

    public int getCount() throws Exception {
        return (int) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getIDCount());
    }


    // Get number of lanes
    public int getLaneCount(String edgeID) throws Exception {
        return (int) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getLaneNumber(edgeID));
    }


    // Get vehicle ID and number of vehicles on the edge last step
    @SuppressWarnings("unchecked")
    public List<String> getVehicleIDList(String edgeID) throws Exception {
        return (List<String>) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getLastStepVehicleIDs(edgeID));
    }

    public int getVehicleCount(String edgeID) throws Exception {
        return (int) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getLastStepVehicleNumber(edgeID));
    }


    // Get average speed of vehicles on the edge (m/s)
    public double getMeanSpeed(String edgeID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getLastStepMeanSpeed(edgeID));
    }


    // Get density of vehicles on the  (vehicle/m)
    public double getDensity(String edgeID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getParameter(edgeID, "density"));
    }

    // Get length & width of the edge (m)
    public double getLength(String edgeID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getParameter(edgeID, "length"));
    }

    public double getWidth(String edgeID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getParameter(edgeID, "width"));
    }

    // Get max speed allowed on the edge (m/s)
    public double getMaxSpeed(String edgeID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getParameter(edgeID, "maxSpeed"));
    }

    // Get estimate travel time on the edge (s)
    public double getTravelTime(String edgeID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Edge.getTraveltime(edgeID));
    }

}