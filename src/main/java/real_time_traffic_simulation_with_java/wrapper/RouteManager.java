package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Route;

import java.util.List;

/**
 * RouteManager is a wrapper class for SumoTraciConnection to manage routes in the simulation
 * @TestedCompleted
 */

public class RouteManager {

    /**
     * private SumoTraciConnection conn
    */
    private final SumoTraciConnection conn;

    /**
     * Connection to Sumo
     * @param connection
     * @throws Exception
    */
    public RouteManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    /**
     * Get list of route IDs in current simulation
     * @return a List type String of route IDs
     * @throws Exception
     * @Tested
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(Route.getIDList());
    }


    /**
     * Get number of routes in current simulation
     * @return an int number of routes
     * @throws Exception
     * @Tested
    */ 
    public int getCount() throws Exception {
        return (int) conn.do_job_get(Route.getIDCount());
    }


    /**
     * Get the edges of a route in order, route with disconnected edges are allowed 
     * @return a List type String of edges in the route
     * @throws Exception
     * @Tested
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getEdges(String routeID) throws Exception {
        return (List<String>) conn.do_job_get(Route.getEdges(routeID));
    }

}
