package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Vehicle;
import java.util.List;
import de.tudresden.sumo.objects.SumoColor;

public class VehicleWrapper {
    private final SumoTraciConnection conn;


    // Connection to Sumo
    public VehicleWrapper(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }

    
    // Get list of vehicle IDs & numer of vehicles
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(Vehicle.getIDList());
    }

    public int getCount() throws Exception {
        return (int) conn.do_job_get(Vehicle.getIDCount());
    }


    // Get position of a vehicle in map to visualize map
    // Returns double[2] with [0] = x, [1] = y
    public double[] getPosition(String vehicleID) throws Exception {
        return (double[]) conn.do_job_get(Vehicle.getPosition(vehicleID));
    }


    // Get Lane ID that the vehicle is currently on
    public String getLane(String vehicleID) throws Exception {
        return (String) conn.do_job_get(Vehicle.getLaneID(vehicleID));
    }


    // Manage vehicle color
    public void setColor(String vehicleID, SumoColor color) throws Exception {
        conn.do_job_set(Vehicle.setColor(vehicleID, color));
    }

    public SumoColor getColor(String vehicleID) throws Exception {
        return (SumoColor) conn.do_job_get(Vehicle.getColor(vehicleID));
    }


    // Inject vehicle, type ID is DEFAULT_VEHTYPE (car)
    // depart: current simulation time (in sec)
    // position: 0.0 (start of edge), lane = 0 (rightmost lane)
    public void inject(String vehicleID, String routeID, double position, double speed, byte lane) throws Exception {
        conn.do_job_set(Vehicle.add(vehicleID, "", routeID, (int)SumoTraasConnection.getCurrentStep(), position, speed, lane));
    }
}
