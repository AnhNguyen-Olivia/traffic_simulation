package real_time_traffic_simulation_with_java;
import javafx.application.Platform;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.MainWindow;

//import real_time_traffic_simulation_with_java.gui.MainWindow;

public class App {

    public static void main(String[] args) {
        try{
            App app = new App();
            SimulationEngine simulationEngine = new SimulationEngine();
            app.run(simulationEngine);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void run(SimulationEngine simulationEngine){
        try{
            launchGui(simulationEngine);
            for(;;){
                simulationEngine.stepSimulation();
                Thread.sleep(50);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void launchGui(SimulationEngine simulationEngine){
        Platform.startup(() -> {
            try {
                MainWindow mainWindow = new MainWindow(simulationEngine);
                mainWindow.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // private void runSimulation() throws Exception{
    //     while(true){
    //         connection.nextStep();
    //         Thread.sleep(100);
    //     }
    // }
}
