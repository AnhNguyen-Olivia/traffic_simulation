package real_time_traffic_simulation_with_java;
import javafx.application.Platform;
import real_time_traffic_simulation_with_java.gui.MainWindow;

//import real_time_traffic_simulation_with_java.gui.MainWindow;

public class App {

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
                MainWindow mainWindow = new MainWindow();
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
