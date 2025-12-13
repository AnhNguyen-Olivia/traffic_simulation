package real_time_traffic_simulation_with_java.alias;

/**
 * This is where we put all the default metrics used in the simulation
 * Thus make the code easier to look and fix as we only need the change the color code here
 * We use final class and private constructor to make it unextendable and not be use as an object.
 *
*/

public final class Metrics {
    private Metrics() {
    }

    public static final double DEFAULT_VEHICLE_LENGTH = 5.0; // in meters
    public static final double DEFAULT_VEHICLE_WIDTH = 1.8;  // in meters
    public static final double DEFAULT_LANE_WIDTH = 3.2;    // in meters
    public static final double TLS_WIDTH = 0.53; // For drawing traffic light box
    public static final double HIDE_DELAY = 100; // Hide delay for tooltips in milliseconds
}
