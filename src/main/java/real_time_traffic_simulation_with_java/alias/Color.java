package real_time_traffic_simulation_with_java.alias;

/** This is where we put all the sumo color code definitions to prevent user from generting so many color types.
 * Thus make the code easier to look and fix as we only need the change the color code here.
 * We use final class and private constructor to make it unextendable and not be use as an object.
 * */


import de.tudresden.sumo.objects.SumoColor;

public final class Color {
    private Color(){
    }

    public static SumoColor RED = new SumoColor(255,0,0,0);
    public static SumoColor BLUE = new SumoColor(0,0,255,0);
    public static SumoColor GREEN = new SumoColor(0,255,0,0);
    public static SumoColor YELLOW = new SumoColor(255,255,0,0);
    public static SumoColor BLACK = new SumoColor(0,0,0,0);
    public static SumoColor WHITE = new SumoColor(255,255,255,0);
}
