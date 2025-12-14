package real_time_traffic_simulation_with_java.wrapper;
import java.util.Objects;

import de.tudresden.sumo.cmd.Simulation;
import it.polito.appeal.traci.SumoTraciConnection;
import real_time_traffic_simulation_with_java.alias.Path;

public class SumoTraasConnection {
    
    /** 
     * Private Sumo port 
    */
    private int port = 8813;

    /** added waiting time for Sumo in ms */
    private int wait = 2000;

    /** declare field variable*/
    private SumoTraciConnection connection;

    /** 
     * Sumo binary, netfile and route file path. 
     * To change the path go to alias folder and change the path.java file
    */
    private String SumoBinary = Path.SumoPath;
    private String netFile = Path.NetFilePath;
    private String rouFile = Path.RouFilePath;
    
    /** 
     * Constructor for SumoTraasConnection 
     * @throws Exception
    */
    public SumoTraasConnection() throws Exception{
        try {
            /** Creat a new sumo connection object */
            connection = new SumoTraciConnection(SumoBinary, netFile, rouFile);
            /** Throws (output) the errors */
        } catch(Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    /** 
     * Getter for connection 
    */
    public SumoTraciConnection getConnection() {
        return this.connection;
    }

    /** 
     * Method to start the connection 
    */
    public void startConnection() throws Exception{
            System.out.println("Starting Sumo, please wait....");

            /** 
             * Start Sumo automatically 
            */
            connection.addOption("start", "true");
            
            /** 
             * Sumo remote port 
            */
            connection.runServer(port);
            
            /** 
             * Wait for Sumo 
            */
            Thread.sleep(wait);
 
            System.out.println("Sumo start successfully! Thank you for waiting.");
    }

    /** 
     * Make a Sumo-move-to-the-next-step method 
    */
    public void nextStep() throws Exception{
        connection.do_timestep();
    }

    /** 
     * Get current simulation time in seconds
     * @return double time in seconds
    */
    public double getCurrentStep() throws Exception{
        double timeSeconds = (double)connection.do_job_get(Simulation.getTime());
        return timeSeconds; 
    }

    /** 
     * Method to close the connection.
     * Check if the connection is null or not. If yes then throw Exception.
     * @throws Exception
    */
    public void closeConnection() throws Exception{
        Objects.requireNonNull(connection, "Connection isn't initialize");
        connection.close();
        System.out.println("Sumo close successfully, thank you for using!");
    }
}

/** 
 * Learn more about SumoTraciConnection on https://sumo.dlr.de/javadoc/traas/it/polito/appeal/traci/SumoTraciConnection.html 
*/