package real_time_traffic_simulation_with_java.gui;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.tools.ExportingFiles;

import java.util.logging.Level;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.application.Platform;

/** Main window class that sets up the primary GUI components and manages the animation timer */
public class MainWindow extends Stage {
    /** Calling simulation engine, map panel, animation timer, logger and exporting files */
    private SimulationEngine simulationEngine;
    private MapPanel mapPanel;
    private AnimationTimer animationTimer;
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(MainWindow.class.getName());
    private ExportingFiles exportingFiles;

    /**
     * MainWindow contructor. Its have simulation engine as parameter to pass to other comfponents 
     * @param engine The simulation engine instance
    */
    public MainWindow(SimulationEngine engine) {
        this.simulationEngine = engine;
        this.exportingFiles = new ExportingFiles(); 
        
        try {initializeGui();} catch(Exception e){
            LOGGER.log(Level.SEVERE, "Failed to initialize MainWindow GUI: " + e.getMessage(), e);
        }
        LOGGER.log(Level.INFO, "MainWindow initialized successfully.");
    }

    /**
     * initializeGui method to setup the main window gui
    */
    private void initializeGui() {
        /**
         * Create map panel, control panel and statistic panel
         * Pass simulation engine to map panel and control panel
         * Set preferred width, max width for control panel and statistic panel because we want to fix their width.
        */
        mapPanel = new MapPanel(this.simulationEngine);
        ControlPanel controlPanel = new ControlPanel(this.simulationEngine, this.exportingFiles); 
        controlPanel.setPrefWidth(Metrics.CONTROL_PANEL_WIDTH);
        controlPanel.setMaxWidth(Metrics.WINDOW_HEIGHT);

        /**
         * Statistic panel still a placeholder image for now
        */
        Dashboard dashboard = new Dashboard(this.simulationEngine);
        dashboard.setPrefWidth(Metrics.DASHBOARD_WIDTH);
        dashboard.setMaxWidth(Metrics.WINDOW_HEIGHT);
        
        /**
         * Separate the main window into 3 parts using BorderPane layout: 
         * center for map panel, left for control panel, right for statistic panel.
        */ 
        BorderPane root = new BorderPane();
        root.setCenter(mapPanel);
        root.setLeft(controlPanel);
        root.setRight(dashboard);
        Scene scene = new Scene(root,Metrics.WINDOW_WIDTH, Metrics.WINDOW_HEIGHT);

        /**
         * set alignment for each part in BorderPane 
        */
        BorderPane.setAlignment(mapPanel,Pos.CENTER);
        BorderPane.setAlignment(controlPanel,Pos.CENTER);
        BorderPane.setAlignment(dashboard,Pos.CENTER);

        /**
         * Set up title, icon and scene for main window
        */
        Image icon = new Image(getClass().getResource(Path.IconImage).toExternalForm());
        this.getIcons().add(icon);
        this.setTitle("Traffic Simulation Beta");
        this.setResizable(false);
        this.setScene(scene);
        this.show();
    }
    
    /**
     * Start animation timer method to update simulation and refresh map panel at fixed interval definned in Metrics.CONNECT_SPEED_MS. <br>
     * This method works by create a stepIntervalNanos variable to store the interval time in nanoseconds. <br>
     * then create an AnimationTimer object and override its handle method. <br>
     * <p>
     * class AnimationTimer allows us to create a timer, that is called in each frame while it is active. <br>
     * An extending class has to override the method handle(long) which will be called in every frame. <br>
     *                                                              -Oracle AnimationTimer Doc-
     * <p>
     * Which is what we want because we need a method to update the simulation (call SimulationEngine and use it stepSimulation method)
     * and call Map panel refresh methos to update the map in each frame. The method will be talk over the MapPanel.java
     * <p>
     * the handle parameter "now" is the timestamp of the current frame given in nanoseconds. 
     * This value will be the same for all AnimationTimers called during one frame. (Read more at the Oracle documentation! really recommended)
     * <p>
     * Inside the handle method, we check if the time between the current frame (now) 
     * and the last time step is less than the stepIntervalNanos, if yes we simply return and do nothing.
     * But if not, meaning the time has passesd more than the interval we set, 
     * we call the stepSimulation method and refresh the map, then update the lastStepTime to the current time.
     * <p>
     * Incase Sumo connection gets closed, we catch the IllegalStateException and stop the animation timer.
     */
    public void startAnimationTimer(){
        final long stepIntervalNanos = Metrics.CONNECT_SPEED_MS * 1_000_000L;
        animationTimer = new AnimationTimer() {
            private long lastStepTime = 0L;
            @Override
            public void handle(long now){
                if (now - lastStepTime < stepIntervalNanos)return;
                try{
                    simulationEngine.stepSimulation();
                    mapPanel.refresh();
                    LOGGER.log(Level.FINE, "Thread: " + Thread.currentThread().getName());

                    // The simulation engine produces the vehicle data, which is then passed to ExportingFiles.
                    // ExportingFiles wraps this data in a ReportData object and places it into a BlockingQueue,
                    // allowing a background worker thread to process and export the data asynchronously.
                    exportingFiles.queueCSV(simulationEngine.dataForCSV());
                    lastStepTime = now;
                    LOGGER.log(Level.FINE, "MainWindow AnimationTimer step executed at: " + now);

                }catch(IllegalStateException closed){   
                    this.stop();
                    Platform.runLater(() -> MainWindow.this.close());
                    LOGGER.log(Level.WARNING, "Simulation connection closed. AnimationTimer stopped.");
                }catch(Exception e){
                    e.printStackTrace();
                    LOGGER.log(Level.SEVERE, "Error during AnimationTimer step: " + e.getMessage(), e);
                }
            }
        };
        animationTimer.start(); //start the animation timer
    }                  

    /**
     * Stop animation timer method to stop the animation timer and exporting files service when main window is closed
    */
    public void stopAnimationTimer(){
        if(animationTimer != null){ //This means the animation timer is running, stop it
            animationTimer.stop();
            LOGGER.log(Level.INFO, "MainWindow AnimationTimer stopped.");
        }
        if (exportingFiles != null) {
            exportingFiles.shutdown();
            LOGGER.log(Level.INFO, "ExportingFiles service shut down initiated.");
        }
    }

}                       
