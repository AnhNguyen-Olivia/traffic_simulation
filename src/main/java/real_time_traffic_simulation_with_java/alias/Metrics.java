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
    public static final double TLS_WIDTH = 1.0; // For drawing traffic light box
    public static final double EDGE_DIVIDER_WEIGHT = 0.2;
    public static final double LANE_DIVIDER_WEIGHT = 0.4;
    public static final double LANE_DASHED_LENGTH = 5.0; // Length of dash in lane divider
    public static final double LANE_DASHED_GAP = 4.0;    // Gap between dashes in lane divider

    public static final double HIDE_DELAY = 100; // Hide delay for tooltips in milliseconds

    public static final double ENLARGE_FACTOR = 1.05; // Factor to zoom out the map
    public static final double SHRINK_FACTOR = 0.95;      // Factor to zoom in the map
    public static final int CONNECT_SPEED = 100;
}
