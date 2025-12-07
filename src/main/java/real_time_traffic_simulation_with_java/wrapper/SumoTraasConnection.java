package real_time_traffic_simulation_with_java.wrapper;
import java.util.List;

//import de.tudresden.sumo.cmd.Simulation;
//import de.tudresden.sumo.cmd.Vehicle;
import it.polito.appeal.traci.SumoTraciConnection;
import real_time_traffic_simulation_with_java.alias.Path;

public class SumoTraasConnection {
    
    //Sumo listening port
    private int port = 8813;

    //Wait for sumo in ms

    private int wait = 1000;
    
    // the path
    private String SumoBinary = Path.SumoPath;
    private String netFile = Path.NetFilePath;
    private String rouFile = Path.RouFilePath;

    private SumoTraciConnection connection = null;

    public SumoTraasConnection() throws Exception{
        try {
            //Creat a new sumo connection object
            connection = new SumoTraciConnection(SumoBinary, netFile, rouFile);

            System.out.println("Starting Sumo, please wait....");
             connection.addOption("start", "true"); //start Sumo automatically 
            connection.runServer(port); // Sumo remote port
            
            Thread.sleep(wait);

            try {

            double simTime = (double) connection.do_job_get(
                de.tudresden.sumo.cmd.Simulation.getTime()
            );
            
            System.out.println("TraCI connection OK! SUMO time = " + simTime);
            } catch (Exception e) {
                System.out.println("TraCI FAILED — SUMO is not listening on port " + port);
                throw e;
            }
 
            System.out.println("Sumo start successfully! Thank you for waiting.");
            
            for (int step = 0; step < 1000; step++) {
                connection.do_timestep();

                @SuppressWarnings("unchecked")
                List<String> allVehicleIds = (List<String>) connection.do_job_get(
                    de.tudresden.sumo.cmd.Vehicle.getIDList()
                );

                System.out.println("Step: " + step + ": Vehicles = " + allVehicleIds);
                Thread.sleep(1000);
                }    
                
        } catch(Exception e){
            e.printStackTrace(); //output errors
            throw e;
        } finally {
            if (connection != null){
                connection.close();
                System.out.println("Simulation finished, closing Sumo");
            }
        }
    }
}
