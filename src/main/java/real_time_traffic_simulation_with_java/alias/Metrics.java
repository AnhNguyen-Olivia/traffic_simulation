package real_time_traffic_simulation_with_java.alias;

/**
 * Utility class: Stores all the metrics used in the simulation, 
 *      thus make the code easier to look and fix.
*/

public final class Metrics {
    private Metrics() {
    }
    
    /** Length of DEFAULT_VEHTYPE of SUMO (in meters) */
    public static final double DEFAULT_VEHICLE_LENGTH = 5.0;
    /** Width of DEFAULT_VEHTYPE of SUMO (in meters) */
    public static final double DEFAULT_VEHICLE_WIDTH = 1.8;
    /** Default lane width of SUMO (in meters) */
    public static final double DEFAULT_LANE_WIDTH = 3.2;
    /** Arc size for rendering vehicle corners */
    public static final double DEFAULT_VEHICLE_ARC = 1.8;
    /** Width of traffic light box */
    public static final double TLS_WIDTH = 1.0;
    /** Weight of edge borderline */
    public static final double EDGE_DIVIDER_WEIGHT = 0.2;
    /** Weight of lane divider line */
    public static final double LANE_DIVIDER_WEIGHT = 0.4;
    /** Length of dash in lane divider */
    public static final double LANE_DASHED_LENGTH = 5.0;
    /** Gap between dashes in lane divider */
    public static final double LANE_DASHED_GAP = 4.0;

    /** Threshold for high halting duration (in simulation seconds) to determine congestion */
    public static final int HIGH_HALTING_DURATION_THRESHOLD = 60;
    /** Threshold for high halting rate (average halting vehicles/lane) */
    public static final double HIGH_HALTING_RATE_THRESHOLD = 5.0;

    /** Hide delay for tooltips (in milliseconds) */
    public static final double HIDE_DELAY = 100;

    /** Factor to zoom out the map */
    public static final double ENLARGE_FACTOR = 1.05;
    /** Factor to zoom in the map */
    public static final double SHRINK_FACTOR = 0.95;
    /** Maximum zoom level */
    public static final double MAX_ZOOM_LEVEL = 15.0;
    /** Minimum zoom level */
    public static final double MIN_ZOOM_LEVEL = 0.5;

    /** Connection speed (in milliseconds): How often does the simulation update */
    public static final int CONNECT_SPEED_MS = 200;

    // Metrics for window pane size
    public static final double WINDOW_WIDTH = 1400;
    public static final double WINDOW_HEIGHT = 810;
    public static final double DASHBOARD_WIDTH = 250;
    public static final double STATISTIC_WIDTH = 250;
}
