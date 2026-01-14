package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.objects.SumoGeometry;
import de.tudresden.sumo.objects.SumoLink;
import de.tudresden.sumo.objects.SumoTLSController;
import de.tudresden.sumo.objects.SumoTLSProgram;
import de.tudresden.sumo.objects.SumoTLSPhase;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import real_time_traffic_simulation_with_java.cores.TrafficLightData;


/** Wrapper class for TraaS to manage traffic lights in the simulation */
public class TrafficLightManager{
    private static final Logger LOGGER = Logger.getLogger(TrafficLightManager.class.getName());

    /** Connection to Sumo */
    private final SumoTraciConnection conn;
    /** Store List of visualization objects for traffic lights */
    private List<TrafficLightData> trafficLightDataList = new java.util.ArrayList<>();

    /**
     * Wrapper class for TraaS to manage traffic lights in the simulation
     * @param connection connection to Sumo
     */
    public TrafficLightManager(SumoTraciConnection connection) {
        this.conn = connection;
    }


    /**
     * Get list of traffic light IDs
     * @return a List type String of traffic light IDs
    */ 
    @SuppressWarnings("unchecked")    
    public List<String> getIDList() throws Exception{
        return (List<String>) conn.do_job_get(Trafficlight.getIDList());
    }


    /**
     * Get number of traffic light in the simulation
     * @return a int number of traffic lights
    */  
    public int getCount() {
        try{
            return (int) conn.do_job_get(Trafficlight.getIDCount());
        } catch (Exception e){
            LOGGER.log(Level.WARNING, "Error getting traffic light count.");
            return -1;
        }
    }


    /**
     * Get the current state of the traffic light ('r', 'y', 'g' sequence)
     * @param tlId ID of the traffic light
     * @return String of traffic light current state
    */ 
    public String getState(String tlId) throws Exception{
        return (String) conn.do_job_get(Trafficlight.getRedYellowGreenState(tlId));
    }

    /**
     * Get remaining time of the current phase of the traffic light (second)
     * @param tlId ID of the traffic light
     * @return double of remaining time in seconds
    */ 
    public double getNextSwitch(String tlId) {
        try{
            double timeSeconds = (double)conn.do_job_get(Simulation.getTime());
            return (double) conn.do_job_get(Trafficlight.getNextSwitch(tlId)) - timeSeconds;
        } catch (Exception e){
            LOGGER.log(Level.WARNING, "Error getting traffic light remaining time until next switch.");
            return -1;
        }
    }


    /**
     * Get the duration of the current phase of the traffic light (second)
     * @param tlId ID of the traffic light
     * @return double of duration of the current phase in seconds
    */ 
    public double getDuration(String tlId) {
        try{
            return (double) conn.do_job_get(Trafficlight.getPhaseDuration(tlId));
        } catch (Exception e){
            LOGGER.log(Level.WARNING, "Error getting traffic light current phase duration.");
            return -1;
        }
    }


    /**
     * Get the current phase index of the traffic light
     * @param tlId ID of the traffic light
     * @return int of current phase index
    */ 
    public int getPhaseID(String tlId) {
        try{
            return (int) conn.do_job_get(Trafficlight.getPhase(tlId));
        } catch (Exception e){
            LOGGER.log(Level.WARNING, "Error getting traffic light current phase index.");
            return -1;
        }
    }


    /**
     * Get number of phases of the traffic light
     * @param tlId ID of the traffic light
     * @return int of number of phases
     */
    public int getPhaseCount(String tlId) throws Exception{
        SumoTLSController controller = (SumoTLSController) conn.do_job_get(Trafficlight.getCompleteRedYellowGreenDefinition(tlId));
        SumoTLSProgram program = controller.programs.values().iterator().next();
        return program.phases.size();
    }


    /**
     * Get initial list of durations for each phase of the traffic light set in net file
     * @param tlId ID of the traffic light
     * @return a List of Double type durations for each phase
    */
    public List<Integer> getPhasesDuration(String tlId) throws Exception{
        SumoTLSController controller = (SumoTLSController) conn.do_job_get(Trafficlight.getCompleteRedYellowGreenDefinition(tlId));
        ArrayList<SumoTLSPhase> phases = (ArrayList<SumoTLSPhase>) controller.programs.get("0").phases;
        List<Integer> durations = new ArrayList<>();
        for (SumoTLSPhase phase : phases) {
            durations.add((int) phase.maxDur);
        }
        return durations;
    }


    /**
     * Set new duration for each phase of the traffic light and update TrafficLightData phasesDuration
     * @param tlId ID of the traffic light
     * @param durations List of durations for each phase
     */
    public void setPhaseDuration(String tlId, List<Integer> durations) {
        try{
            // Validate input durations
            if(durations.size() != this.getPhaseCount(tlId)){
                LOGGER.log(Level.WARNING, "Number of durations does not match number of phases. Cannot adjust phase durations for traffic light " + tlId + ".");
                return;
            }
            SumoTLSController controller = (SumoTLSController) conn.do_job_get(Trafficlight.getCompleteRedYellowGreenDefinition(tlId));
            ArrayList<SumoTLSPhase> phases = (ArrayList<SumoTLSPhase>) controller.programs.get("0").phases;
            List<Integer> newDurations = new ArrayList<>();
            // Create new SumoTLSProgram with updated durations
            SumoTLSProgram newProgram = new SumoTLSProgram();
            for (int i = 0; i < phases.size(); i++) {
                newProgram.add(new SumoTLSPhase(durations.get(i), phases.get(i).phasedef));
                newDurations.add(durations.get(i));
            }
            // Set the new program in the simulation
            conn.do_job_set(Trafficlight.setCompleteRedYellowGreenDefinition(tlId, newProgram));
            // Update the phases in TrafficLightData
            for(TrafficLightData trafficLightData : this.trafficLightDataList) {
                if(trafficLightData.getId().equals(tlId)) {
                    trafficLightData.setPhasesDuration(newDurations);
                    break;
                }
            }
        } catch (Exception e){
            LOGGER.log(Level.WARNING, "Error adjusting phase durations for traffic light " + tlId + ".");
        }
    }



    /**
     * Toggle traffic light to next phase: get the current phase index
     *      and then add 1 to convert to next phase
     * @param tlId ID of the traffic light
     */
    public void nextPhase(String tlId) {
        try{
            int phase = (int) conn.do_job_get(Trafficlight.getPhase(tlId));
            if(phase == this.getPhaseCount(tlId) - 1){
                conn.do_job_set(Trafficlight.setPhase(tlId, 0));
            } else {
                conn.do_job_set(Trafficlight.setPhase(tlId, phase+1));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error toggling to next phase for traffic light " + tlId + ".");
        }
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
                this.getState(id),
                this.getPhasesDuration(id)
            );
            trafficLightDataList.add(trafficLightData);
        }
        return trafficLightDataList;
    }


    /**
     * Update mapping data of the TrafficLightData List with current states from simulation
     * @throws Exception
    */
    public void updateTrafficLightDataList() throws Exception {
        for (TrafficLightData trafficLightData : this.trafficLightDataList) {
            String colorString = this.getState(trafficLightData.getId());
            trafficLightData.setColor(colorString);
        }
    }
}