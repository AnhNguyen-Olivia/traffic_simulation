package real_time_traffic_simulation_with_java.alias;

import de.tudresden.sumo.objects.SumoColor;

import java.util.List;
import java.util.ArrayList;

/**
 * Utility classs: Stores all the sumo color code definitions to prevent user from generting so many color types, 
 *      thus make the code easier to look and fix.
*/
public final class Color {
    private Color(){
    }

    /** Color to draw road */
    public static final javafx.scene.paint.Color ROAD = javafx.scene.paint.Color.DIMGRAY;
    /** Color to draw road border */
    public static final javafx.scene.paint.Color ROAD_BORDER = javafx.scene.paint.Color.WHITE;
    /** Color to draw lane divider */
    public static final javafx.scene.paint.Color LANE_DIVIDER = javafx.scene.paint.Color.WHITE;
    /** Color to draw congested road */
    public static final javafx.scene.paint.Color CONGESTED_ROAD = javafx.scene.paint.Color.CHOCOLATE;

    /** Sumo Color(255,0,0,0) */
    public static final SumoColor RED = new SumoColor(255,0,0,0);
    /** Sumo Color(0,0,255,0) */
    public static final SumoColor BLUE = new SumoColor(0,0,255,0);
    /** Sumo Color(0,255,0,0) */
    public static final SumoColor GREEN = new SumoColor(0,255,0,0);
    /** Sumo Color(0,0,0,0) */
    public static final SumoColor BLACK = new SumoColor(0,0,0,0);
    /** Sumo Color(255,255,255,0) */
    public static final SumoColor WHITE = new SumoColor(255,255,255,0);

    /** List of all color names that user can choose for the vehicle */
    public static final List<String> ListofAllColor = new ArrayList<>(List.of("RED", "GREEN", "BLUE", "BLACK", "WHITE"));
    
    /** Convert SumoColor to its String representation, WHITE is default for unknown colors */
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

    /** Convert String representation of color to SumoColor, WHITE is default for invalid input or unknown colors */
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

    /** Check if a given color name is valid and return color name (return WHITE if invalid) */
    public static String checkAvailableColor(String colorName) {
        if (ListofAllColor.contains(colorName.toUpperCase())) {
            return colorName.toUpperCase();
        } else {
            return "WHITE";
        }
    }
}
