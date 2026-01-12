package real_time_traffic_simulation_with_java;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import javafx.application.Platform;
import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.gui.MainWindow;

public class App {
    private SimulationEngine simulationEngine;
    private static final Logger ROOT_LOGGER = Logger.getLogger("");

    /**
     * The main application entry point to launch and run the traffic simulation GUI
     * @param args
    */
    public static void main(String[] args) {
        App app = new App();
        setupLogger();
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
     * It is used to start the JavaFx runtime, the specified runable will be called on the Javafx application thread.
     * 
     * In the code Platform.startup(() -> {......}); 
     * the () -> {......} syntax is a lambda expression
     * It is a short block of code that takes in parameters () and reuturns a value, they look like methods but do not need a name
     * and can be implemented right in the body of a method.
     * 
     * Then inside the lambda expression, we create a new SimulationEngine object and a new MainWindow object
     * passing the simulationEngine as parameter to the MainWindow constructor. 
     * We then call show() method so the main window is displayed and use startAnimationTimer() method to start the animation timer. 
     * (The method is definded in MainWindow class, we will talk more about in the MainWinddow file)
     * 
     * We add a setOnCloseRequest event handler to the main window to ensure that when the window is closed 
     * (stopAnimationTimer method is called so the animation timer is stopped, more about this in MainWindow.java),
     * the simulation engine is properly stopped and the JavaFx platform exits cleanly. 
     * And Platform.ext() is a static method that initiates the termination of the JavaFx runtime.
    */

    private void launchGui(){
        Platform.startup(() -> {
            try {
                simulationEngine = new SimulationEngine();
                MainWindow mainWindow = new MainWindow(simulationEngine);

                mainWindow.setOnCloseRequest(e -> {
                    try {
                        simulationEngine.stopSimulation();
                        mainWindow.stopAnimationTimer();
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

    /**
     * private static method to setup logger configuration
    */
    private static void setupLogger(){
        try {
            ROOT_LOGGER.setLevel(Level.INFO);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());

            FileHandler fileHandler = new FileHandler(Path.LogFile, true);
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            ROOT_LOGGER.addHandler(fileHandler);

        } catch (IOException e) {
            ROOT_LOGGER.log(Level.SEVERE, "Failed to initialize logger", e);
        }
    }
    
}   