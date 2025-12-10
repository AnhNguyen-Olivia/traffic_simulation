package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;

import java.util.List;
import real_time_traffic_simulation_with_java.cores.VehicleData;

public class VehicleManager {
    private final SumoTraciConnection conn;
    private final SumoTraasConnection sumoConn;

    // Connection to Sumo
    public VehicleManager(SumoTraciConnection connection, SumoTraasConnection sumoConnection) throws Exception {
        this.conn = connection;
        this.sumoConn = sumoConnection;
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


    // Get Lane ID that the vehicle is currently on
    public String getLane(String vehicleID) throws Exception {
        return (String) conn.do_job_get(Vehicle.getLaneID(vehicleID));
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

    public String colorToString(SumoColor color) {
        if(color.r == (byte)255 && color.g == 0 && color.b == 0){
            return "RED";
        } else if(color.r == 0 && color.g == 0 && color.b == (byte)255){
            return "BLUE";
        } else if(color.r == 0 && color.g == (byte)255 && color.b == 0){
            return "GREEN";
        } else if(color.r == 0 && color.g == 0 && color.b == 0){
            return "BLACK";
        } else {
            return "WHITE";
        }
    }


    // Inject vehicle, typeID ="" is DEFAULT_VEHTYPE (car)
    // depart: current simulation time (in sec)
    // position: 0.0 (start of edge), lane = 0 (rightmost lane)
<<<<<<< HEAD
<<<<<<< HEAD
    // public void inject(String vehicleID, String routeID, double position, double speed, byte lane) throws Exception {
    //     conn.do_job_set(de.tudresden.sumo.cmd.Vehicle.add(vehicleID, "", routeID, (int)SumoTraasConnection.getCurrentStep(), position, speed, lane));
    // }
=======
    public void inject(String vehicleID, String routeID, double position, double speed, byte lane) throws Exception {
        conn.do_job_set(de.tudresden.sumo.cmd.Vehicle.add(vehicleID, "", routeID, (int)sumoConn.getCurrentStep(), position, speed, lane));
=======
    public void add(String vehID, String routeID, SumoColor color) throws Exception {
        conn.do_job_set(Vehicle.addFull(vehID, routeID, "DEFAULT_VEHTYPE", "now", "best", "base", "max", "current", "max", "current", "", "", "", 0, 0));
        this.setColor(vehID, color);
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
                    this.colorToString(this.getColor(id))
            );
            vehicleDataList.add(vehicledata);
        }
        return vehicleDataList;
>>>>>>> d3e6b8adf5972d600dd66347f3759a3f3a4502b1
    }
>>>>>>> f680377e3854b30e752e82f2a66cbf64c2868564
}
