package real_time_traffic_simulation_with_java;
import javafx.application.Platform;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.MainWindow;

//import real_time_traffic_simulation_with_java.gui.MainWindow;

public class App {
    private SimulationEngine simulationEngine;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run(){
        try{
            launchGui();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void launchGui(){
        Platform.startup(() -> {
            try {
                simulationEngine = new SimulationEngine();
                MainWindow mainWindow = new MainWindow(simulationEngine);
                mainWindow.setOnCloseRequest(e -> {
                    try {
                        simulationEngine.stopSimulation();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Platform.exit();
                });
                mainWindow.show();
                mainWindow.startAnimationTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
}