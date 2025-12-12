package real_time_traffic_simulation_with_java.alias;

import de.tudresden.sumo.objects.SumoColor;

/**
 * This is where we put all the sumo color code definitions to prevent user from generting so many color types
 * The class have methods to convert between SumoColor and String representation of colors
 * Thus make the code easier to look and fix as we only need the change the color code here
 * We use final class and private constructor to make it unextendable and not be use as an object.
 *
*/



public final class Color {
    private Color(){
    }

    public static final SumoColor RED = new SumoColor(255,0,0,0);
    public static final SumoColor BLUE = new SumoColor(0,0,255,0);
    public static final SumoColor GREEN = new SumoColor(0,255,0,0);
    public static final SumoColor BLACK = new SumoColor(0,0,0,0);
    public static final SumoColor WHITE = new SumoColor(255,255,255,0);
    
    public static String colorToString(SumoColor color) {
        if(color.r == RED.r && color.g == RED.g && color.b == RED.b){
            return "RED";
        } else if(color.r == BLUE.r && color.g == BLUE.g && color.b == BLUE.b){
            return "BLUE";
        } else if(color.r == GREEN.r && color.g == GREEN.g && color.b == GREEN.b){
            return "GREEN";
        } else if(color.r == BLACK.r && color.g == BLACK.g && color.b == BLACK.b){
            return "BLACK";
        } else {
            return "WHITE";
        }
    }

    public static SumoColor stringToColor(String colorName) {
        switch(colorName.toUpperCase()) {
            case "RED":
                return RED;
            case "BLUE":
                return BLUE;
            case "GREEN":
                return GREEN;
            case "BLACK":
                return BLACK;
            default:
                return WHITE;
        }
    }
}
