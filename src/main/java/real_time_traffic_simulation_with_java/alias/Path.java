package real_time_traffic_simulation_with_java.alias;

/*
This is where we put all the path instead of paste the paths constainly
Example: SUMO_NET_DIR = "this/is/the/path"
Thus make the code easier to look and fix as we only need the change the path here
We use final class and private constructor to make it unextendable and not be use as an object.

"Design and document for inheritance or else prohibit it" -Effective Java by Joshua Bloch
*/

public final class Path {

    private Path(){
    }

    public static String TraasPath = "src/main/java/real_time_traffic_simulation_with_java/lib/TraaS.jar";
    public static String SumoPath = "src/main/java/real_time_traffic_simulation_with_java/lib/sumo.exe";
    public static String NetFilePath = "src/main/java/real_time_traffic_simulation_with_java/SumoConfig/testing.net.xml";
    public static String RouFilePath = "src/main/java/real_time_traffic_simulation_with_java/SumoConfig/testing.rou.xml";

}
