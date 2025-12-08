package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Route;
import java.util.List;

public class RouteWrapper {
    private final SumoTraciConnection conn;

    // Connection to Sumo
    public RouteWrapper(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    // Get list of edge IDs & number of routes
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(Route.getIDList());
    }

    public int getCount() throws Exception {
        return (int) conn.do_job_get(Route.getIDCount());
    }


    // Get route edges list
    @SuppressWarnings("unchecked")
    public List<String> getEdges(String routeID) throws Exception {
        return (List<String>) conn.do_job_get(Route.getEdges(routeID));
    }

}
