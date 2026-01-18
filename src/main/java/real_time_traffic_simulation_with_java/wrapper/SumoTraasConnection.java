package real_time_traffic_simulation_with_java.wrapper;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.tudresden.sumo.cmd.Simulation;
import it.polito.appeal.traci.SumoTraciConnection;
import real_time_traffic_simulation_with_java.alias.Path;

/** Wrapper class for SumoTraciConnection to manage connection with SUMO simulator using TraaS library */
public class SumoTraasConnection {
    
    private static final Logger LOGGER = Logger.getLogger( SumoTraasConnection.class.getName() );
    
    /** Private Sumo port */
    private int port = 8813;

    /** added waiting time for Sumo in ms */
    private int wait = 500;

    /** declare field variable*/
    private SumoTraciConnection connection;

    /** 
     * Sumo binary file path. <br>
     * To change the path go to alias folder and change the path.java file
    */
    private String SumoBinary = Path.SumoPath;
    /** 
     * Sumo net file path. <br>
     * To change the path go to alias folder and change the path.java file
    */
    private String netFile = Path.NetFilePath;
    /** 
     * Sumo route file path. <br>
     * To change the path go to alias folder and change the path.java file
    */
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
            LOGGER.log(Level.SEVERE, "Error initializing SumoTraciConnection: ", e);
            throw e;
        }
    }

    /** Getter for connection 
     * @return SumoTraciConnection connection object
    */
    public SumoTraciConnection getConnection() {
        return this.connection;
    }

    /** Method to start the connection */
    public void startConnection() throws Exception{
            System.out.println("Starting Sumo, please wait....");

            /** Start Sumo automatically */
            connection.addOption("start", "true");
            
            /** Sumo remote port*/
            connection.runServer(port);
            
            /** Wait for Sumo */
            Thread.sleep(wait);
 
            System.out.println("Sumo start successfully! Thank you for waiting.");
            LOGGER.log(Level.INFO, "Sumo started successfully on port " + port);
    }

    /** Make a Sumo-move-to-the-next-step method 
     * @throws Exception 
    */
    public void nextStep() throws Exception {
        try{
            connection.do_timestep();
        } catch(Exception e){
            throw e;
        }
    }

    /** 
     * Get current simulation time in seconds
     * @return double time in seconds
    */
    public double getCurrentStep() throws Exception{
        try{
            double timeSeconds = (double)connection.do_job_get(Simulation.getTime());
            return timeSeconds; 
        } catch(Exception e){
            throw e;
        }
    }

    /** 
     * Method to close the connection.
     * Check if the connection is null or not. If yes then throw Exception.
     * @throws Exception
    */
    public void closeConnection() throws Exception{
        if (connection == null){
            LOGGER.log(Level.SEVERE, "Connection is null, cannot close connection.");
            throw new Exception("Connection is null, cannot close connection.");
        }
        connection.close();
        LOGGER.log(Level.INFO, "Sumo connection closed successfully.");
    }
}

/** Learn more about SumoTraciConnection on https://sumo.dlr.de/javadoc/traas/it/polito/appeal/traci/SumoTraciConnection.html */