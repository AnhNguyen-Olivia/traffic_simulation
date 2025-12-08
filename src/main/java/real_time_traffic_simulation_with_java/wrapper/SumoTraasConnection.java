package real_time_traffic_simulation_with_java.wrapper;
import java.util.Objects;
import it.polito.appeal.traci.SumoTraciConnection;
import real_time_traffic_simulation_with_java.alias.Path;
import de.tudresden.sumo.cmd.Simulation;

public class SumoTraasConnection {
    
    /** private Sumo port */
    private int port = 8813;

    /** added waiting time for Sumo in ms */
    private int wait = 500;

    /** declare field variable*/
    private SumoTraciConnection connection;

    /** Sumo binary, netfile and route file path, 
     *  to change the path go to alias folder and change the path.java file
     */
    private String SumoBinary = Path.SumoPath;
    private String netFile = Path.NetFilePath;
    private String rouFile = Path.RouFilePath;

    
    /** Constructor that initializes TraCI and start SUMO*/
    public SumoTraasConnection() throws Exception{
        try {
            /** Creat a new sumo connection object */
            connection = new SumoTraciConnection(SumoBinary, netFile, rouFile);

            System.out.println("Starting Sumo, please wait....");

            /** Start Sumo automatically */
            connection.addOption("start", "true");
            
            /** Sumo remote port */
            connection.runServer(port);
            
            /** Wait for Sumo */
            Thread.sleep(wait);
 
            System.out.println("Sumo start successfully! Thank you for waiting.");
        
            /** Throw/output errors */
        } catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    /** Make a Sumo-move-to-the-next-step method */
    public void nextStep() throws Exception{
        connection.do_timestep();
    }

    /** Get the current time (step) in s (double) */
    public void getCurrentStep() throws Exception{
        Simulation.getTime();
    }

    /** Method to close the connection.
     * Check if the connection is null or not. If yes then throw Exception.
     * @throws Exception
    */
    public void closeConnection() throws Exception{
        Objects.requireNonNull(connection, "Connection isn't initialize");
        connection.close();
        System.out.println("Sumo close successfully, thank you for using!");
    }
}

/** Learn more about SumoTraciConnection in https://sumo.dlr.de/javadoc/traas/it/polito/appeal/traci/SumoTraciConnection.html */