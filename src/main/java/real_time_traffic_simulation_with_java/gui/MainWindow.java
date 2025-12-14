package real_time_traffic_simulation_with_java.gui;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class MainWindow extends Stage {
    /**
     * Calling simulation engine, map panel, dashboard and statistic panel (currently only display an image)
    */
    private SimulationEngine simulationEngine;
    private MapPanel placeHolderMap;
    private AnimationTimer animationTimer;
    
    /**
     * MainWindow contructor. Its have simulation engine as parameter to pass to other components 
     * @param engine
     * @throws Exception
    */
    public MainWindow(SimulationEngine engine) throws Exception {
        this.simulationEngine = engine;
        initializeGui();
    }

    private void initializeGui() throws Exception {
        placeHolderMap = new MapPanel(this.simulationEngine);
        DashBoard dashBoard = new DashBoard(this.simulationEngine);
        dashBoard.setPrefWidth(250);
        dashBoard.setMaxWidth(250);

        Statistic statistic = new Statistic();
        statistic.setPrefWidth(250);
        statistic.setMaxWidth(250);
        
        // Separate the main window into 3 parts using BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(placeHolderMap);
        root.setLeft(dashBoard);
        root.setRight(statistic);
        Scene scene = new Scene(root,1400, 830, Color.WHITE);

        BorderPane.setAlignment(placeHolderMap,Pos.CENTER);
        BorderPane.setAlignment(dashBoard,Pos.CENTER);
        BorderPane.setAlignment(statistic,Pos.CENTER);

        Image icon = new Image(getClass().getResource(Path.IconImage).toExternalForm());
        this.getIcons().add(icon);
        this.setTitle("Traffic Simulation Beta");
        this.setResizable(false);
        this.setScene(scene);
        this.show();
    }

    public void startAnimationTimer(){
        final long stepIntervalNanos = Metrics.CONNECT_SPEED * 1_000_000L;
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
