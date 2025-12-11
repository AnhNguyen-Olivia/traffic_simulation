package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;

import java.util.List;
import real_time_traffic_simulation_with_java.cores.VehicleData;
import real_time_traffic_simulation_with_java.alias.Color;

public class VehicleManager {
    private final SumoTraciConnection conn;

    // Connection to Sumo
    public VehicleManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }

    // -------------------WRAPPER FOR TRAAS-------------------
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
    public SumoPosition2D getPosition(String vehicleID) throws Exception {
        return (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(vehicleID));
    }


    // Get Lane & Edge ID that the vehicle is currently on
    public String getLaneID(String vehicleID) throws Exception {
        return (String) conn.do_job_get(Vehicle.getLaneID(vehicleID));
    }

    public String getEdgeID(String vehicleID) throws Exception {
        return (String) conn.do_job_get(Lane.getEdgeID(this.getLaneID(vehicleID)));
    }


    // Get speed of a vehicle (m/s)
    public double getSpeed(String vehicleID) throws Exception {
        return (double) conn.do_job_get(Vehicle.getSpeed(vehicleID));
    }

    // Get angle of a vehicle to map
    public double getAngle(String vehicleID) throws Exception {
        return (double) conn.do_job_get(Vehicle.getAngle(vehicleID));
    }


    // Manage vehicle color
    public void setColor(String vehicleID, SumoColor color) throws Exception {
        conn.do_job_set(Vehicle.setColor(vehicleID, color));
    }

    public SumoColor getColor(String vehicleID) throws Exception {
        return (SumoColor) conn.do_job_get(Vehicle.getColor(vehicleID));
    }


    // Inject vehicle, typeID ="" is DEFAULT_VEHTYPE (car)
    // depart: current simulation time (in sec)
    // position: 0.0 (start of edge), lane = 0 (rightmost lane)
    public void add(String vehID, String routeID, String color) throws Exception {
        SumoColor vehcolor = Color.stringToColor(color);
        conn.do_job_set(Vehicle.addFull(vehID, routeID, "DEFAULT_VEHTYPE", "now", "best", "base", "max", "current", "max", "current", "", "", "", 0, 0));
        this.setColor(vehID, vehcolor);
    }

    // Get vehicle data list
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
