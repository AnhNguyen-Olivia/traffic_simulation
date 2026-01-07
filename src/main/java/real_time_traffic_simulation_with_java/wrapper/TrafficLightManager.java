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


/** Wrapper class for TraaS to manage traffic lights in the simulation */
public class TrafficLightManager{

    /** Connection to Sumo */
    private final SumoTraciConnection conn;
    /** Store List of visualization objects for traffic lights */
    private List<TrafficLightData> trafficLightDataList = new java.util.ArrayList<>();

    /**
     * Wrapper class for TraaS to manage traffic lights in the simulation
     * @param connection connection to Sumo
     * @throws Exception
    */
    public TrafficLightManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    /**
     * Get list of traffic light IDs
     * @return a List type String of traffic light IDs
     * @throws Exception
    */ 
    @SuppressWarnings("unchecked")    
    public List<String> getIDList() throws Exception{
        return (List<String>) conn.do_job_get(Trafficlight.getIDList());
    }


    /**
     * Get number of traffic light in the simulation
     * @return a int number of traffic lights
     * @throws Exception
    */  
    public int getCount() throws Exception{
        return (int) conn.do_job_get(Trafficlight.getIDCount());
    }


    /**
     * Get the current state of the traffic light ('r', 'y', 'g' sequence)
     * @param tlId ID of the traffic light
     * @return String of traffic light current state
     * @throws Exception
    */ 
    public String getState(String tlId) throws Exception{
        return(String) conn.do_job_get(Trafficlight.getRedYellowGreenState(tlId));
    }


    /**
     * Get remaining time of the current phase of the traffic light (second)
     * @param tlId ID of the traffic light
     * @return double of remaining time in seconds
     * @throws Exception
    */ 
    public double getNextSwitch(String tlId) throws Exception{
        double timeSeconds = (double)conn.do_job_get(Simulation.getTime());
        return (double) conn.do_job_get(Trafficlight.getNextSwitch(tlId)) - timeSeconds;
    }


    /**
     * Get the duration of the current phase of the traffic light (second)
     * @param tlId ID of the traffic light
     * @return double of duration of the current phase in seconds
     * @throws Exception
    */ 
    public double getDuration(String tlId) throws Exception{
        return (double) conn.do_job_get(Trafficlight.getPhaseDuration(tlId));
    }

    /**
     * Set the duration of the current phase of the traffic light (second)
     * Currently not working for unknown reason
     * @param tlId ID of the traffic light
     * @param duration double of duration of the current phase in seconds
     * @throws Exception
    */ 
    public void setDuration(String tlId, double duration) throws Exception{
        conn.do_job_set(Trafficlight.setPhaseDuration(tlId, duration));
    }


    /**
     * Get the current phase index of the traffic light
     * @param tlId ID of the traffic light
     * @return int of current phase index
     * @throws Exception
    */ 
    public int getPhaseID(String tlId) throws Exception{
        return (int) conn.do_job_get(Trafficlight.getPhase(tlId));
    }


    /**
     * Get number of phases of the traffic light
     * @param tlId ID of the traffic light
     * @return int of number of phases
     * @throws Exception
     */
    public int getPhaseCount(String tlId) throws Exception{
        SumoTLSController controller = (SumoTLSController) conn.do_job_get(Trafficlight.getCompleteRedYellowGreenDefinition(tlId));
        SumoTLSProgram program = controller.programs.values().iterator().next();
        return program.phases.size();
    }


    /**
     * Toggle traffic light to next phase: get the current phase index
     * and then add 1 to convert to next phase
     * @param tlId ID of the traffic light
     * @throws Exception
     */
    public void nextPhase(String tlId) throws Exception{
        int phase = (int) conn.do_job_get(Trafficlight.getPhase(tlId));
        if(phase == this.getPhaseCount(tlId) - 1){
            conn.do_job_set(Trafficlight.setPhase(tlId, 0));
        } else {
            conn.do_job_set(Trafficlight.setPhase(tlId, phase+1));
        }
    }


    /**
     * Method to get lanes controlled by the traffic light
     * @param tlID ID of the traffic light
     * @return a list type String lane ids controlled by the traffic light
     * @throws Exception
    */
    @SuppressWarnings("unchecked")
    public List<String> getLaneTraffic(String tlID) throws Exception{
        return (List<String>)conn.do_job_get(Trafficlight.getControlledLanes(tlID));
    }


    /**
     * Method to get links controlled by the traffic light
     * @param tlID ID of the traffic light
     * @return a list type SumoLink objects controlled by the traffic light
     * @throws Exception
    */
    @SuppressWarnings("unchecked")
    public List<SumoLink> getLinksTraffic(String tlID) throws Exception{
        return (List<SumoLink>)conn.do_job_get(Trafficlight.getControlledLinks(tlID));
    }
    

    /**
     * Method to get junctions controlled by the traffic light
     * @param tlID ID of the traffic light
     * @return a list type String junction ids controlled by the traffic light
     * @throws Exception
    */
    @SuppressWarnings("unchecked")
    public List<String> getJunctionTraffic(String tlID) throws Exception{
        return (List<String>)conn.do_job_get(Trafficlight.getControlledJunctions(tlID));
    }


    /**
     * Initiate a List of TrafficLightData for all traffic lights
     * @return a List of TrafficLightData for all traffic lights
     * @throws Exception
    */
    public List<TrafficLightData> getTrafficLightDataList() throws Exception {
        List<String> IDs = this.getIDList();
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
        return trafficLightDataList;
    }


    /**
     * Update the TrafficLightData List with current states from simulation
     * @throws Exception
    */
    public void updateTrafficLightDataList() throws Exception {
        for (TrafficLightData trafficLightData : this.trafficLightDataList) {
            String colorString = this.getState(trafficLightData.getId());
            trafficLightData.setColor(colorString);
        }
    }
}