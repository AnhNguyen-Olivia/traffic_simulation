package real_time_traffic_simulation_with_java.alias;

/**
*This is where we put all the path instead of paste the paths constainly
*Example: SUMO_NET_DIR = "this/is/the/path"
*Thus make the code easier to look and fix as we only need the change the path here
*We use final class and private constructor to make it unextendable and not be use as an object.

*"Design and document for inheritance or else prohibit it" -Effective Java by Joshua Bloch
*/

public final class Path {

    private Path(){
    }
    /** Sumo, TraaS and cfg path*/
    public static final String TraasPath = "src/main/java/real_time_traffic_simulation_with_java/lib/TraaS.jar";
    public static final String SumoPath = "src/main/java/real_time_traffic_simulation_with_java/lib/sumo.exe";
    public static final String NetFilePath = "src/main/java/real_time_traffic_simulation_with_java/SumoConfig/map.net.xml";
    public static final String RouFilePath = "src/main/java/real_time_traffic_simulation_with_java/SumoConfig/map.rou.xml";

    /** Image path if needed*/
    public static final String IconImage = "/real_time_traffic_simulation_with_java/gui/resources/cat.png";
    public static final String CatMapImage = "/real_time_traffic_simulation_with_java/gui/resources/please do not the cat.jpg";
    public static final String DashboardImage = "/real_time_traffic_simulation_with_java/gui/resources/please do not the cat.jpg";
    public static final String StatisticImage = "/real_time_traffic_simulation_with_java/gui/resources/please do not the cat.jpg";
}

// py findAllRoutes.py -n map.net.xml -o map.rou.xml -s -t
// All edges can be start edges or target edges
// 39 edges in total
// Retrieve edge IDs anytime by calling EdgeManager.getIDList()
// Edge IDs:
// -E10,-E11,-E12,-E13,-E14,-E19,-E20,-E21,-E23,-E24,-E25,-E26,-E27,-E28,-E30,-E31,-E32,-E33,E0,E10,E11,E15,E16,E17,E19,E20,E21,E23,E24,E25,E26,E27,E28,E30,E31,E32,E33,E35,E36