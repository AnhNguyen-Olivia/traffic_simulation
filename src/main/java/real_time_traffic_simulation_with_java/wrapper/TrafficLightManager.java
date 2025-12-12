package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoLink;
import de.tudresden.sumo.objects.SumoTLSController;
import de.tudresden.sumo.objects.SumoTLSProgram;

import java.util.List;
import real_time_traffic_simulation_with_java.cores.TrafficLightData;

/**
 * TrafficLightManager is a wrapper class for SumoTraciConnection to manage traffic lights in the simulation
 * @Test Completed
 * @Javadoc Completed
 */

public class TrafficLightManager{

    /**
     * private SumoTraciConnection conn
     * private List<TrafficLightData> TrafficLightDataList
    */
    private final SumoTraciConnection conn;
    private List<TrafficLightData> trafficLightDataList = new java.util.ArrayList<>();

    /**
     * Connection to Sumo
     * @param connection
     * @throws Exception
    */
    public TrafficLightManager(SumoTraciConnection connection){
        this.conn = connection;
    }


    /**
     * Get list of traffic light IDs
     * @return a List type String of traffic light IDs
     * @throws Exception
     * @Tested
    */ 
    @SuppressWarnings("unchecked")    
    public List<String> getIDList() throws Exception{
        return (List<String>) conn.do_job_get(Trafficlight.getIDList());
    }


    /**
     * Get number of traffic light in the simulation
     * @return a int number of traffic lights
     * @throws Exception
     * @Tested
    */  
    public int getCount() throws Exception{
        return (int) conn.do_job_get(Trafficlight.getIDCount());
    }


    /**
     * Get the current state of the traffic light ('r', 'y', 'g' sequence)
     * @return String of traffic light current state
     * @throws Exception
     * @Tested
    */ 
    public String getState(String tlId) throws Exception{
        return(String) conn.do_job_get(Trafficlight.getRedYellowGreenState(tlId));
    }


    /**
     * Get remaining time of the current phase of the traffic light (second)
     * @return double of remaining time in seconds
     * @throws Exception
     * @Tested
    */ 
    public double getNextSwitch(String tlId) throws Exception{
        double timeSeconds = (double)conn.do_job_get(Simulation.getTime());
        return (double) conn.do_job_get(Trafficlight.getNextSwitch(tlId)) - timeSeconds;
    }


    /**
     * Set the duration of the current phase of the traffic light (second)
     * @throws Exception
     * @Tested
    */ 
    public double getDuration(String tlId) throws Exception{
        return (double) conn.do_job_get(Trafficlight.getPhaseDuration(tlId));
    }

    /**
     * Set the duration of the current phase of the traffic light (second)
     * @throws Exception
     * @NotWorking
     * @UnknownReason
    */ 
    public void setDuration(String tlId, double duration) throws Exception{
        conn.do_job_set(Trafficlight.setPhaseDuration(tlId, duration));
    }


    /**
     * Get the current phase index of the traffic light
     * @return int of current phase index
     * @throws Exception
     * @Tested
    */ 
    public int getPhaseID(String tlId) throws Exception{
        return (int) conn.do_job_get(Trafficlight.getPhase(tlId));
    }


    /**
     * Toggle traffic light to next phase: get the current phase index
     * and then add 1 to convert to next phase
     * @throws Exception
     * @Tested
     */
    public void nextPhase(String tlId) throws Exception{
        int phase = (int) conn.do_job_get(Trafficlight.getPhase(tlId));
        SumoTLSController controller = (SumoTLSController) conn.do_job_get(Trafficlight.getCompleteRedYellowGreenDefinition(tlId));
        SumoTLSProgram program = controller.programs.values().iterator().next();
        if(phase == program.phases.size() - 1){
            conn.do_job_set(Trafficlight.setPhase(tlId, 0));
        } else {
            conn.do_job_set(Trafficlight.setPhase(tlId, phase+1));
        }
    }


    /**
     * Method to get lanes controlled by the traffic light
     * @param tlID
     * @return a list type String lane ids controlled by the traffic light
     * @throws Exception
     * @Tested
    */
    @SuppressWarnings("unchecked")
    public List<String> getLaneTraffic(String tlID) throws Exception{
        return (List<String>)conn.do_job_get(Trafficlight.getControlledLanes(tlID));
    }


    /**
     * Method to get links controlled by the traffic light
     * @param tlID
     * @return a list type SumoLink objects controlled by the traffic light
     * @throws Exception
     * @Tested
    */
    @SuppressWarnings("unchecked")
    public List<SumoLink> getLinksTraffic(String tlID) throws Exception{
        return (List<SumoLink>)conn.do_job_get(Trafficlight.getControlledLinks(tlID));
    }
    

    /**
     * Method to get junctions controlled by the traffic light
     * @param tlID
     * @return a list type String junction ids controlled by the traffic light
     * @throws Exception
     * @Tested
    */
    @SuppressWarnings("unchecked")
    public List<String> getJunctionTraffic(String tlID) throws Exception{
        return (List<String>)conn.do_job_get(Trafficlight.getControlledJunctions(tlID));
    }


    /**
     * Create and get a List of TrafficLightData for all traffic lights
     * @return a List of TrafficLightData for all traffic lights
     * @throws Exception
     * @Tested
    */
    public List<TrafficLightData> getTrafficLightDataList() throws Exception {
        List<String> IDs = this.getIDList();
        if(trafficLightDataList.isEmpty()) {
            for (String id : IDs) {
                // Get coordinates of start lanes by lane IDs
                List<SumoGeometry> LandGeometries = new java.util.ArrayList<>();
                for(SumoLink link : this.getLinksTraffic(id)){
                    SumoGeometry laneGeometry = (SumoGeometry) conn.do_job_get(de.tudresden.sumo.cmd.Lane.getShape(link.from));
                    LandGeometries.add(laneGeometry);
                }
                TrafficLightData trafficLightData = new TrafficLightData(
                    id,
                    LandGeometries,
                    this.getState(id)
                );
                trafficLightDataList.add(trafficLightData);
            }
        } else {
            // Update color list only
            for (TrafficLightData trafficLightData : trafficLightDataList) {
                trafficLightData.setColorList(this.getState(trafficLightData.getTlID()));
            }
        }
        return trafficLightDataList;
    }

}