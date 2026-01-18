package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.objects.SumoStringList;

import java.util.List;


/** Wrapper class for TraaS to manage routes in the simulation */
public class RouteManager {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(RouteManager.class.getName());

    /** Connection to Sumo */
    private final SumoTraciConnection conn;

    /**
     * Wrapper class for TraaS to manage routes in the simulation
     * @param connection connection to Sumo
    */
    public RouteManager(SumoTraciConnection connection) {
        this.conn = connection;
    }


    /**
     * Add a new route to the simulation (disconnected edges are allowed)
     * @param routeID ID of the route
     * @param start_edge where the route starts
     * @param end_edge where the route ends
    */
    public void add(String routeID, String start_edge, String end_edge) throws Exception {
        try{
            conn.do_job_set(Route.add(routeID, new SumoStringList(List.of(start_edge, end_edge))));
        } catch (Exception e) {
            LOGGER.log(java.util.logging.Level.WARNING, "No available route between edge " + start_edge + " and edge " + end_edge, e);
            throw e;
        }
    }

}
