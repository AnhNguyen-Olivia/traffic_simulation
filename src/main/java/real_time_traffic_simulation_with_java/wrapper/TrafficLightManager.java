package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import de.tudresden.sumo.cmd.Trafficlight;

public class TrafficLightManager{
    private final SumoTraciConnection conn;

    public TrafficLightManager(SumoTraciConnection connection){
        this.conn = connection;
    }
    /**
     * get all traffic light id 
     */
    @SuppressWarnings("Unchecked")
    public List<String> getIDList() throws Exception{
        return (List<String>) conn.do_job_get(Trafficlight.getIDList());
    }
    /**
     * get current state 
     */
    public String getState(String tlId) throws Exception{
        return(String) conn.do_job_get(Trafficlight.getRedYellowGreenState(tlId));
    }
    /**
     * set the state string
     */
    public void setState(String tlId, String state) throws Exception{
        conn.do_job_set(Trafficlight.setRedYellowGreenState(tlId, state));
    }
    /**
     * set the duration of the current phase
     */
    public void setDuration(String tlId, double duration) throws Exception{
        conn.do_job_set(Trafficlight.setPhaseDuration(tlId, duration));
    }
    /**
     * set and get Phase of the current phase
     */
    public int getPhase(String tlId) throws Exception{
        return(int) conn.do_job_get(Trafficlight.getPhase(tlId));
    }
    public void setPhase(String tlId, int phase) throws Exception{
        conn.do_job_set(Trafficlight.setPhase(tlId, phase));
    }
    /**
    * Toggle traffic light to next phase
    */
   public void nextPhase(String tlId) throws Exception{
    int phase = getPhase(tlId);
    setPhase(tlId,phase+1);
   }
}
