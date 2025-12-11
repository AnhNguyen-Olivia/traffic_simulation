package real_time_traffic_simulation_with_java;
import java.util.List;

import de.tudresden.sumo.cmd.Edge;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoColor;
import real_time_traffic_simulation_with_java.wrapper.*;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.cores.VehicleData;

public class VehicleEdgeLaneWrapper {
    public static void main(String[] args) {
        try {
            SumoTraasConnection conn = new SumoTraasConnection();
            VehicleManager vehicleManager = new VehicleManager(conn.getConnection());
            EdgeManager edgeManager = new EdgeManager(conn.getConnection());
            LaneManager laneManager = new LaneManager(conn.getConnection());
            RouteManager routeManager = new RouteManager(conn.getConnection());
            conn.startConnection();


            // System.out.println("---------Total " + edgeManager.getCount() + " edges in the simulation---------");
            // List<String> edgeIDs = edgeManager.getIDList();
            // // for(String edgeID : edgeIDs){
            // //     System.out.println("Edge ID: \"" + edgeID + "\" length: " + edgeManager.getLength(edgeID) + "m.");
            // // }
            // System.out.println("------------------------------------------------------------------------------");

            vehicleManager.add("0", "0", "RED");
            for (int i = 0; i < 100; i++) {
                System.out.println("Current step (" + i + "): " + conn.getCurrentStep());
                if(vehicleManager.getCount() != 0){
                    System.out.println("Vehicle is on edge: " + vehicleManager.getEdgeID("0"));
                }

                // for(String vehicle : vehicleManager.getIDList()){
                //     System.out.println("Vehicle ID: " + vehicle + "is on edge: " + vehicleManager.getEdgeID(vehicle));
                // }

                Thread.sleep(100);
                conn.nextStep();
            }

            conn.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
