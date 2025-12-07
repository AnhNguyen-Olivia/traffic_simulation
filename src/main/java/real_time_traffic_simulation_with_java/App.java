package real_time_traffic_simulation_with_java;

import real_time_traffic_simulation_with_java.wrapper.SumoTraasConnection;

public class App {
    public static void main(String[] args) {
        System.out.println("Traffic Simulation Launcher");
        try {
            new SumoTraasConnection();
            System.out.println("Simulation completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
