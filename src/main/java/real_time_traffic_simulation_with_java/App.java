package real_time_traffic_simulation_with_java;
import javafx.application.Platform;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.MainWindow;

//import real_time_traffic_simulation_with_java.gui.MainWindow;

public class App {
    private SimulationEngine simulationEngine;
    private volatile boolean isRun = true;

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
                MainWindow mainWindow = new MainWindow();
                mainWindow.show();

                startSimThread();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void startSimThread(){        
        Thread simulationThread = new Thread(() -> {
            try {
                while (isRun){
                    simulationEngine.stepSimulation();
                    Thread.sleep(50);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    public void shutdown(){
        isRun = false;
    }
    
}

