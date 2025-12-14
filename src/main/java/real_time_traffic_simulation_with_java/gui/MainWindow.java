package real_time_traffic_simulation_with_java.gui;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class MainWindow extends Stage {
    /**
     * Calling simulation engine, map panel, dashboard and statistic panel (currently only display an image)
    */
    private SimulationEngine simulationEngine;
    private MapPanel placeHolderMap;
    private AnimationTimer animationTimer;
    
    /**
     * MainWindow contructor. Its have simulation engine as parameter to pass to other comfponents 
     * @param engine
     * @throws Exception
    */
    public MainWindow(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        initializeGui();
    }

    /**
     * initializeGui method to setup the main window gui
     * @throws Exception
    */
    private void initializeGui() throws Exception {
        /**
         * Create map panel, dashboard and statistic panel
         * Pass simulation engine to map panel and dashboard
         * Set preferred width, max width for dashboard and statistic panel because we want to fix their width.
        */
        placeHolderMap = new MapPanel(this.simulationEngine);
        DashBoard dashBoard = new DashBoard(this.simulationEngine);
        dashBoard.setPrefWidth(Metrics.DASHBOARD_WIDTH);
        dashBoard.setMaxWidth(Metrics.WINDOW_HEIGHT);

        /**
         * Statistic panel still a placeholder image for now
        */
        Statistic statistic = new Statistic();
        statistic.setPrefWidth(Metrics.STATISTIC_WIDTH);
        statistic.setMaxWidth(Metrics.WINDOW_HEIGHT);
        
        /**
         * Separate the main window into 3 parts using BorderPane layout: 
         * center for map panel, left for dashboard, right for statistic panel.
        */ 
        BorderPane root = new BorderPane();
        root.setCenter(placeHolderMap);
        root.setLeft(dashBoard);
        root.setRight(statistic);
        Scene scene = new Scene(root,Metrics.WINDOW_WIDTH, Metrics.WINDOW_HEIGHT);

        /**
         * set alignment for each part in BorderPane 
        */
        BorderPane.setAlignment(placeHolderMap,Pos.CENTER);
        BorderPane.setAlignment(dashBoard,Pos.CENTER);
        BorderPane.setAlignment(statistic,Pos.CENTER);

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
     * Start animation timer method to update simulation and refresh map panel at fixed interval definned in Metrics.CONNECT_SPEED_MS.
     * This method works by create a stepIntervalNanos variable to store the interval time in nanoseconds.
     * then create an AnimationTimer object and override its handle method. The reason we need to override handle method is becaus
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
                    placeHolderMap.refresh();
                    lastStepTime = now;
                }catch(IllegalStateException closed){
                    // SUMO already closed; stop timer to avoid noisy stack traces
                    this.stop();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        animationTimer.start();
    }                                                                                                                                                             

    public void stopAnimationTimer(){
        if(animationTimer != null){
            animationTimer.stop();
        }
    }

}                       
