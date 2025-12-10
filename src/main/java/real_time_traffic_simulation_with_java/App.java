package real_time_traffic_simulation_with_java;

import java.util.List;

import de.tudresden.sumo.objects.SumoLink;
//import real_time_traffic_simulation_with_java.gui.MainWindow;
import real_time_traffic_simulation_with_java.wrapper.*;

public class App {
    public static void main(String[] args) {
        try {
            // Start SUMO connection
            SumoTraasConnection sumo = new SumoTraasConnection();
            sumo.startConnection();

            TrafficLightManager tlm = new TrafficLightManager(sumo.getConnection());

            // Get all traffic light IDs
            List<String> allTLs = tlm.getIDList();
            System.out.println("Traffic lights in network: " + allTLs);

            // Loop through each traffic light
            for (String tlID : allTLs) {
                System.out.println("\n==============================");
                System.out.println("Traffic Light ID: " + tlID);

                // Controlled lanes
                List<String> lanes = tlm.getLaneTraffic(tlID);
                System.out.println("Controlled Lanes: " + lanes);

                // Controlled links
                List<SumoLink> links = tlm.getLinksTraffic(tlID);
                System.out.println("Controlled Links:");
                for (SumoLink link : links) {
                    //System.out.println("  " + link.from + " direction: " + link.direction + " state: " + link.state);
                    System.out.println(" " + link.);
                }

                // Controlled junctions
                List<String> junctions = tlm.getJunctionTraffic(tlID);
                System.out.println("Controlled Junctions: " + junctions);

            }

            // for(int i = 0; i < 100 ; i++) {
            //     sumo.nextStep();

            //     List<SumoLink> links = tlm.getLinksTraffic("J12");
            //     SumoLink link = links.get(0);
            //     System.out.println("Controlled Links:");
            //     System.out.println("  " + link.from + " direction: " + link.direction + " state: " + link.state);
            //     //System.out.println(" " + link);

            //     Thread.sleep(100);
            // }

            // Close SUMO
            sumo.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
