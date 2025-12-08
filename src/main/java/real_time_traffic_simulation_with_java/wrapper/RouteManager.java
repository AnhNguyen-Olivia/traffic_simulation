package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import java.util.List;

public class RouteManager {
    private final SumoTraciConnection conn;

    // Connection to Sumo
    public RouteManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    // Get list of edge IDs & number of routes
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(de.tudresden.sumo.cmd.Route.getIDList());
    }

    public int getCount() throws Exception {
        return (int) conn.do_job_get(de.tudresden.sumo.cmd.Route.getIDCount());
    }


    // Get route edges list
    @SuppressWarnings("unchecked")
    public List<String> getEdges(String routeID) throws Exception {
        return (List<String>) conn.do_job_get(de.tudresden.sumo.cmd.Route.getEdges(routeID));
    }

}
