package real_time_traffic_simulation_with_java.alias;

/**
* Utility class: Stores all the paths used in the simulation, 
*       thus make the code easier to look and fix. <br>
* <i>"Design and document for inheritance or else prohibit it" -Effective Java by Joshua Bloch</i>
*/
public final class Path {
    private Path(){
    }

    /** Relative path to TraaS library */
    public static final String TraasPath = "src/main/java/real_time_traffic_simulation_with_java/lib/TraaS.jar";
    /** Relative path to SUMO executable file */
    public static final String SumoPath = "src/main/java/real_time_traffic_simulation_with_java/lib/sumo.exe";
    /** Relative path to SUMO configuration file net */
    public static final String NetFilePath = "src/main/java/real_time_traffic_simulation_with_java/SumoConfig/map.net.xml";
    /** Relative path to SUMO configuration file rou */
    public static final String RouFilePath = "src/main/java/real_time_traffic_simulation_with_java/SumoConfig/map.rou.xml";

    
    public static final String IconImage = "/real_time_traffic_simulation_with_java/gui/resources/cat.png";
    public static final String ConTrolPanelImage = "/real_time_traffic_simulation_with_java/gui/resources/ChristmasCat.jpg";
    public static final String StatisticImage = "/real_time_traffic_simulation_with_java/gui/resources/CatBanner.jpg";
    public static final String mapBackgroundImage = "/real_time_traffic_simulation_with_java/gui/resources/CatChristmas.jpg";
}