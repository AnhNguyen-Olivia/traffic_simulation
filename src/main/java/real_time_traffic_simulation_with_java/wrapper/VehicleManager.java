package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;
import de.tudresden.sumo.objects.SumoStringList;

import java.util.List;
import real_time_traffic_simulation_with_java.cores.VehicleData;
import real_time_traffic_simulation_with_java.alias.Color;

/**
 * VehicleManager is a wrapper class for SumoTraciConnection to manage vehicles in the simulation
 * @Test Completed
 * @Javadoc Completed
 */

public class VehicleManager {

    /**
     * private SumoTraciConnection conn
    */
    private final SumoTraciConnection conn;

    /**
     * Connection to Sumo
     * @param connection
     * @throws Exception
    */
    public VehicleManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    /**
     * Get list of running vehicle IDs, vehicles finished route or not yet be injected are not included
     * @return a List type String of running vehicle IDs
     * @throws Exception
     * @Tested
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception {
        return (List<String>) conn.do_job_get(Vehicle.getIDList());
    }


    /**
     * Get number of running vehicles, vehicles finished route or not yet be injected are not included
     * @return an int number of running vehicles
     * @throws Exception
     * @Tested
    */
    public int getCount() throws Exception {
        return (int) conn.do_job_get(Vehicle.getIDCount());
    }


    /**
     * Get position of a vehicle in Cartesian coordinates
     * @return a SumoPosition2D object representing the position of the vehicle
     * @throws Exception
     * @Tested
    */ 
    public SumoPosition2D getPosition(String vehicleID) throws Exception {
        return (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(vehicleID));
    }


    /**
     * Get lane ID that the vehicle is currently on, including junction lanes
     * @return a String lane ID that the vehicle is currently on
     * @throws Exception
     * @Tested
    */ 
    public String getLaneID(String vehicleID) throws Exception {
        return (String) conn.do_job_get(Vehicle.getLaneID(vehicleID));
    }


    /**
     * Get edge ID that the vehicle is currently on, including junction edges
     * @return a String edge ID that the vehicle is currently on
     * @throws Exception
     * @Tested
    */ 
    public String getEdgeID(String vehicleID) throws Exception {
        return (String) conn.do_job_get(Lane.getEdgeID(getLaneID(vehicleID)));
    }


    /**
     * Get speed of a vehicle (m/s)
     * @return a double speed of the vehicle (m/s)
     * @throws Exception
     * @Tested
    */ 
    public double getSpeed(String vehicleID) throws Exception {
        return (double) conn.do_job_get(Vehicle.getSpeed(vehicleID));
    }


    /**
     * Get angle of a vehicle (degree), starting from the North (0 degree) and measured clockwise
     * @return a double angle of the vehicle (degree)
     * @throws Exception
     * @Tested
    */ 
    public double getAngle(String vehicleID) throws Exception {
        return (double) conn.do_job_get(Vehicle.getAngle(vehicleID));
    }


    /**
     * Set color of a vehicle
     * @throws Exception
     * @Tested
    */ 
    public void setColor(String vehicleID, SumoColor color) throws Exception {
        conn.do_job_set(Vehicle.setColor(vehicleID, color));
    }

    /**
     * Get color of a vehicle
     * @return a SumoColor object representing the color of the vehicle
     * @throws Exception
     * @Tested
    */
    public SumoColor getColor(String vehicleID) throws Exception {
        return (SumoColor) conn.do_job_get(Vehicle.getColor(vehicleID));
    }


    /**
     * Inject a vehicle of default type into the simulation immediately if possible when called
     * Generate a new route with a unique ID = vehicleID from start_edges to end_edges
     * Set the color of the vehicle as user's choice, default to WHITE if invalid color string is given
     * @throws Exception
     * @Tested
    */ 
    public void add(String vehID, String routeID, String color) throws Exception {
        conn.do_job_set(Vehicle.addFull(vehID, routeID, "DEFAULT_VEHTYPE", "now", "best", "base", "max", "current", "max", "current", "", "", "", 0, 0));
        this.setColor(vehID, Color.stringToColor(color));
    }


    /**
     * Create and get a List of VehicleData for all vehicles
     * @return a List of VehicleData for all vehicles
     * @throws Exception
     * @Tested
    */
    public List<VehicleData> getVehicleDataList() throws Exception {
        List<VehicleData> vehicleDataList = new java.util.ArrayList<>();
        List<String> IDs = this.getIDList();
        for (String id : IDs) {
            SumoPosition2D pos = this.getPosition(id);
            VehicleData vehicledata = new VehicleData(
                    id,
                    pos.x,
                    pos.y,
                    this.getAngle(id),
                    this.getColor(id)
            );
            vehicleDataList.add(vehicledata);
        }
        return vehicleDataList;
    }
}
