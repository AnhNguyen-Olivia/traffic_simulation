package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.objects.SumoPosition2D;

import java.util.List;

public class EdgeManager {

    /**
     * private SumoTraciConnection conn
    */
    private final SumoTraciConnection conn;

    /**
     * Connection to Sumo
     * @param connection
     * @throws Exception
    */
    public EdgeManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    /**
     * Get list of edge IDs & number of edges
     * @return a List type String of edge IDs
     * @throws Exception
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(Edge.getIDList());
    }

    /**
     * Get number of edges
     * @return an int number of edges
     * @throws Exception
    */
    public int getCount() throws Exception {
        return (int) conn.do_job_get(Edge.getIDCount());
    }


    /**
     * Get number of lanes
     * @param edgeID
     * @return an int number of lanes
     * @throws Exception
    */ 
    public int getLaneCount(String edgeID) throws Exception {
        return (int) conn.do_job_get(Edge.getLaneNumber(edgeID));
    }


    /**
     * Get vehicle ID and number of vehicles on the edge last step
     * @param edgeID
     * @return List type String of vehicle IDs
     * @throws Exception
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getVehicleIDList(String edgeID) throws Exception {
        return (List<String>) conn.do_job_get(Edge.getLastStepVehicleIDs(edgeID));
    }

    /**
     * Get number of vehicles on the edge last step
     * @param edgeID
     * @return
     * @throws Exception
    */
    public int getVehicleCount(String edgeID) throws Exception {
        return (int) conn.do_job_get(Edge.getLastStepVehicleNumber(edgeID));
    }


    /**
     * Get average speed of vehicles on the edge (m/s)
     * @param edgeID
     * @return a double average speed in m/s
     * @throws Exception
    */
    public double getMeanSpeed(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getLastStepMeanSpeed(edgeID));
    }


    /**
     * Get density of vehicles on the  (vehicle/m)
     * @param edgeID
     * @return a double density in vehicle/m
     * @throws Exception
    */
    public double getDensity(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getParameter(edgeID, "density"));
    }

    /**
     * Get length & width of the edge (m)
     * @param edgeID
     * @return a double length in m
     * @throws Exception
    */
    public double getLength(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getParameter(edgeID, "length"));
    }

    /**
     * Get width of the edge (m)
     * @param edgeID
     * @return a double width in m
     * @throws Exception
    */
    public double getWidth(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getParameter(edgeID, "width"));
    }

    /**
     * Get max speed allowed on the edge (m/s)
     * @param edgeID
     * @return a double max speed in m/s
     * @throws Exception
    */
    public double getMaxSpeed(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getParameter(edgeID, "maxSpeed"));
    }

    /**
     * Get estimate travel time on the edge (s)
     * @param edgeID
     * @return a double travel time in s
     * @throws Exception
    */
    public double getTravelTime(String edgeID) throws Exception {
        return (double) conn.do_job_get(Edge.getTraveltime(edgeID));
    }

    /**
     * Get shape of the edge as list of SumoPosition2D points
     * @param edgeID
     * @return a List type SumoPosition2D of shape points
     * @throws Exception
    */
    @SuppressWarnings("unchecked")
    public List<SumoPosition2D> getShape(String edgeID) throws Exception {
        return (List<SumoPosition2D>) conn.do_job_get(Edge.getParameter(edgeID, "shape"));
    }

}