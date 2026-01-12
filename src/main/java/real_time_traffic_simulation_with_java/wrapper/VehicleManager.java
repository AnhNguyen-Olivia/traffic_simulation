package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Lane;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import real_time_traffic_simulation_with_java.cores.VehicleData;
import real_time_traffic_simulation_with_java.alias.Color;


/** Wrapper class for TraaS to manage vehicles in the simulation */
public class VehicleManager {
    /** Logger for logging messages */
    private static final Logger LOGGER = Logger.getLogger(VehicleManager.class.getName());

    /** Connection to Sumo */
    private final SumoTraciConnection conn;

    /** Filter for vehicles by color and edge */
    private String filter_color = "";
    private String filter_edge = "";

    /**
     * Wrapper class for TraaS to manage vehicles in the simulation
     * @param connection connection to Sumo
    */
    public VehicleManager(SumoTraciConnection connection) {
        this.conn = connection;
    }


    /**
     * Set filter for vehicles (color, edge)
     */
    public void setFilter(String color, String edge) {
        this.filter_color = color;
        this.filter_edge = edge;
    }


    /**
     * Get list of running vehicle IDs, vehicles finished route or not yet be injected are not included
     * @return a List type String of running vehicle IDs
    */ 
    @SuppressWarnings("unchecked")
    public List<String> getIDList() throws Exception{
        return (List<String>) conn.do_job_get(Vehicle.getIDList());
    }


    /** 
     * Get list of filtered running vehicle IDs, vehicles finished route or not yet be injected are not included
     * @return a List type String of filtered running vehicle IDs
     */
    public List<String> getFilteredIDList() throws Exception{
        List<String> allIDs = this.getIDList();
        if(this.filter_color.isEmpty() && this.filter_edge.isEmpty()) {
            return allIDs;
        }
        List<String> filteredIDs = new java.util.ArrayList<>();
        for (String id : allIDs) {
            if(!this.filter_color.isEmpty()) {
                String vehColor = Color.colorToString(this.getColor(id));
                if(vehColor != this.filter_color) {
                    continue;
                }
            }
            if(!this.filter_edge.isEmpty()) {
                String vehEdgeID = this.getEdgeID(id);
                if(!vehEdgeID.equals(this.filter_edge)) {
                    continue;
                }
            }
            filteredIDs.add(id);
        }
        return filteredIDs;
    }


    /**
     * Get number of running vehicles, vehicles finished route or not yet be injected are not included
     * @return an int number of running vehicles
    */
    public int getCount() {
        try {return (int) conn.do_job_get(Vehicle.getIDCount());} catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get vehicle count from SUMO.");
            return -1;
        }
    }


    /**
     * Get position of a vehicle in Cartesian coordinates
     * @param vehicleID ID of the vehicle
     * @return a SumoPosition2D object representing the position of the vehicle
    */ 
    public SumoPosition2D getPosition(String vehicleID) throws Exception {
        return (SumoPosition2D) conn.do_job_get(Vehicle.getPosition(vehicleID));
    }


    /**
     * Get lane ID that the vehicle is currently on, including junction lanes
     * @return a String lane ID that the vehicle is currently on
    */ 
    public String getLaneID(String vehicleID) throws Exception {
        return (String) conn.do_job_get(Vehicle.getLaneID(vehicleID));
    }


    /**
     * Get edge ID that the vehicle is currently on, including junction edges
     * @param vehicleID ID of the vehicle
     * @return a String edge ID that the vehicle is currently on
    */ 
    public String getEdgeID(String vehicleID) throws Exception {
        return (String) conn.do_job_get(Lane.getEdgeID(getLaneID(vehicleID)));
    }


    /**
     * Get speed of a vehicle (km/h)
     * @param vehicleID ID of the vehicle
     * @return a double speed of the vehicle (km/h)
    */ 
    public double getSpeed(String vehicleID) {
        try{
            return (double) conn.do_job_get(Vehicle.getSpeed(vehicleID)) * 3.6f;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get vehicle speed from SUMO for vehicle ID: " + vehicleID);
            return -1;
        }
    }


    /**
     * Get angle of a vehicle (degree), starting from the North (0 degree) and measured clockwise
     * @param vehicleID ID of the vehicle
     * @return a double angle of the vehicle (degree)
    */ 
    public double getAngle(String vehicleID) {
        try{
            return (double) conn.do_job_get(Vehicle.getAngle(vehicleID));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to get vehicle angle from SUMO for vehicle ID: " + vehicleID + ". Rendering issue may occur.");
            return -1;
        }
    }


    /**
     * Set color of a vehicle
     * @param vehicleID ID of the vehicle
     * @param color SumoColor object representing the color to be set
    */ 
    public void setColor(String vehicleID, SumoColor color) {
        try{
            conn.do_job_set(Vehicle.setColor(vehicleID, color));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to set vehicle color in SUMO for vehicle ID: " + vehicleID + ".");
        }
    }

    /**
     * Get color of a vehicle
     * @param vehicleID ID of the vehicle
     * @return a SumoColor object representing the color of the vehicle
    */
    public SumoColor getColor(String vehicleID) throws Exception {
        return (SumoColor) conn.do_job_get(Vehicle.getColor(vehicleID));
    }


    /**
     * Inject a vehicle of default type into the simulation immediately if possible when called
     * Generate a new route with a unique ID = vehicleID from start_edges to end_edges
     * Set the color of the vehicle as user's choice, default to WHITE if invalid color string is given
     * @param vehID ID of the vehicle
     * @param routeID ID of the route
     * @param color String representing the color of the vehicle
    */ 
    public void add(String vehID, String routeID, String color, String speed_in_ms) {
        try{
            conn.do_job_set(Vehicle.addFull(vehID, routeID, "DEFAULT_VEHTYPE", "now", "best", "base", speed_in_ms, "current", "max", "current", "", "", "", 0, 0));
            this.setColor(vehID, Color.stringToColor(color));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to inject vehicle to SUMO. ");
        }
    }


    /**
     * Create and get a List of VehicleData for all vehicles
     * @return a List of VehicleData for all vehicles      
    */
    public List<VehicleData> getVehicleDataList() throws Exception {
        List<VehicleData> vehicleDataList = new java.util.ArrayList<>();
        List<String> IDs = this.getFilteredIDList();
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
