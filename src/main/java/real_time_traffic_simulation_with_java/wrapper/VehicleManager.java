package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import java.util.List;
import de.tudresden.sumo.objects.SumoColor;
import real_time_traffic_simulation_with_java.cores.Vehicle;

public class VehicleManager {
    private final SumoTraciConnection conn;
    private final SumoTraasConnection sumoConn;

    // Connection to Sumo
    public VehicleManager(SumoTraciConnection connection, SumoTraasConnection sumoConnection) throws Exception {
        this.conn = connection;
        this.sumoConn = sumoConnection;
    }

    
    // Get list of vehicle IDs & numer of vehicles
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(de.tudresden.sumo.cmd.Vehicle.getIDList());
    }

    public int getCount() throws Exception {
        return (int) conn.do_job_get(de.tudresden.sumo.cmd.Vehicle.getIDCount());
    }


    // Get position of a vehicle in map to visualize map
    // Returns double[2] with [0] = x, [1] = y
    public double[] getPosition(String vehicleID) throws Exception {
        return (double[]) conn.do_job_get(de.tudresden.sumo.cmd.Vehicle.getPosition(vehicleID));
    }


    // Get Lane ID that the vehicle is currently on
    public String getLane(String vehicleID) throws Exception {
        return (String) conn.do_job_get(de.tudresden.sumo.cmd.Vehicle.getLaneID(vehicleID));
    }


    // Get speed of a vehicle (m/s)
    public double getSpeed(String vehicleID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Vehicle.getSpeed(vehicleID));
    }

    // Get angle of a vehicle to map
    public double getAngle(String vehicleID) throws Exception {
        return (double) conn.do_job_get(de.tudresden.sumo.cmd.Vehicle.getAngle(vehicleID));
    }


    // Manage vehicle color
    public void setColor(String vehicleID, SumoColor color) throws Exception {
        conn.do_job_set(de.tudresden.sumo.cmd.Vehicle.setColor(vehicleID, color));
    }

    public SumoColor getColor(String vehicleID) throws Exception {
        return (SumoColor) conn.do_job_get(de.tudresden.sumo.cmd.Vehicle.getColor(vehicleID));
    }


    // Inject vehicle, type ID is DEFAULT_VEHTYPE (car)
    // depart: current simulation time (in sec)
    // position: 0.0 (start of edge), lane = 0 (rightmost lane)
    public void inject(String vehicleID, String routeID, double position, double speed, byte lane) throws Exception {
        conn.do_job_set(de.tudresden.sumo.cmd.Vehicle.add(vehicleID, "", routeID, (int)sumoConn.getCurrentStep(), position, speed, lane));
    }
}
