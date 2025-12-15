package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.objects.SumoStringList;

import java.util.List;


/** Wrapper class for TraaS to manage routes in the simulation */
public class RouteManager {

    /** Connection to Sumo */
    private final SumoTraciConnection conn;

    /**
     * Wrapper class for TraaS to manage routes in the simulation
     * @param connection connection to Sumo
     * @throws Exception
    */
    public RouteManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    /**
     * Get list of route IDs in current simulation
     * @return a List type String of route IDs
     * @throws Exception
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(Route.getIDList());
    }


    /**
     * Get number of routes in current simulation
     * @return an int number of routes
     * @throws Exception
    */ 
    public int getCount() throws Exception {
        return (int) conn.do_job_get(Route.getIDCount());
    }


    /**
     * Get the edges of a route in order, route with disconnected edges are allowed 
     * @param routeID ID of the route
     * @return a List type String of edges in the route
     * @throws Exception
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getEdges(String routeID) throws Exception {
        return (List<String>) conn.do_job_get(Route.getEdges(routeID));
    }


    /**
     * Add a new route to the simulation (disconnected edges are allowed)
     * @param routeID ID of the route
     * @param start_edge where the route starts
     * @param end_edge where the route ends
     * @throws Exception
    */
    public void add(String routeID, String start_edge, String end_edge) throws Exception {
        conn.do_job_set(Route.add(routeID, new SumoStringList(List.of(start_edge, end_edge))));
    }

}
