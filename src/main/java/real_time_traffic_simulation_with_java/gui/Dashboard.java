package real_time_traffic_simulation_with_java.gui;

import javafx.scene.control.ScrollPane;

import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.dashboardSection.*;

/**
 * The statistic panel on the right side of the main window
 * Use to display statistics about the simulation
*/
public class Dashboard extends ScrollPane {
    private SimulationEngine simulationEngine;
    private TextSection textSection;
    private ChartSection chartSection;
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Dashboard.class.getName());

    /**
     * The statistic panel on the right side of the main window
     * Use to display statistics about the simulation
     * Includes a text section and a chart section
     * @param engine The simulation engine
     */
    public Dashboard(SimulationEngine engine) {
        try{
            this.simulationEngine = engine;

            textSection = new TextSection(simulationEngine);
            chartSection = new ChartSection(simulationEngine);
            
            this.setContent(new javafx.scene.layout.VBox(10, textSection, chartSection));
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize Dashboard.");
        }

        LOGGER.info("Dashboard initialized.");
    }
}
