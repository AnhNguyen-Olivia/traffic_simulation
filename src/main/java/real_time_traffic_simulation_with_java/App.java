package real_time_traffic_simulation_with_java;

import javafx.application.Platform;
import real_time_traffic_simulation_with_java.gui.MainWindow;
import real_time_traffic_simulation_with_java.wrapper.SumoTraasConnection;

//import real_time_traffic_simulation_with_java.gui.MainWindow;

public class App {

    private SumoTraasConnection connection;
    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run(){
        try{
            connection = new SumoTraasConnection();
            connection.startConnection();
            launchGui();
            //runSimulation();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void launchGui(){
        Platform.startup(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.show();
        });
    }

    // private void runSimulation() throws Exception{
    //     while(true){
    //         connection.nextStep();
    //         Thread.sleep(100);
    //     }
    // }
}
