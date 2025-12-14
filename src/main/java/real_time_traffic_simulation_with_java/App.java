package real_time_traffic_simulation_with_java;
import javafx.application.Platform;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.MainWindow;

public class App {
    private SimulationEngine simulationEngine;

    /**
     * The main application entry point to launch and run the traffic simulation GUI
     * @param args
    */
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    /**
     * private method to run, calling launchGui(), surround by try-catch block to handle exceptions
    */
    private void run(){
        try{
            launchGui();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * private method to actually launch the Gui, becasue we are using javafx, we need to use Platform.startup()
     * to initialize the javafx toolkit and launch the main window.
     * Platform.startup​(Runnable runnable) is a static method meaning
     * it belongs to the class Platform itself rather than to any specific instance of the class.
     * It is used to start the JavaFx runtime
    */

    private void launchGui(){
        Platform.startup(() -> {
            try {
                simulationEngine = new SimulationEngine();
                MainWindow mainWindow = new MainWindow(simulationEngine);
                mainWindow.show();
                mainWindow.startAnimationTimer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
}