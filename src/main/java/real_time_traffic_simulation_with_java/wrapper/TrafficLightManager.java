package real_time_traffic_simulation_with_java.wrapper;

import real_time_traffic_simulation_with_java.cores.TrafficLight;
import de.tudresden.sumo.cmd.Trafficlight;
import it.polito.appeal.traci.SumoTraciConnection;

public class TrafficLightManager {
    private SumoTraciConnection sumoConn;
    private TrafficLight trafficLight;

    public TrafficLightManager(SumoTraciConnection sumoConn, TrafficLight trafficLight) {
        this.sumoConn = sumoConn;
        this.trafficLight = trafficLight;
    }

    public TrafficLight getModel() {
        return trafficLight;
    }
 
    public void readFromSUMO() throws Exception {		// Read the real traffic-light state from SUMO and synchronizes core feature
        String sumoState = (String) sumoConn.do_job_get(
                Trafficlight.getRedYellowGreenState(trafficLight.getId())
        );

        char c = sumoState.charAt(0); 				// First signal controls the logic

        switch (c) {
            case 'g': case 'G':
                trafficLight.setGreenDuration(trafficLight.getState() == TrafficLight.State.GREEN ? 0 : trafficLight.getState().ordinal());
                break;

            case 'y': case 'Y':
                trafficLight.setYellowDuration(trafficLight.getState() == TrafficLight.State.YELLOW ? 0 : trafficLight.getState().ordinal());
                break;

            default:
                trafficLight.setRedDuration(trafficLight.getState() == TrafficLight.State.RED ? 0 : trafficLight.getState().ordinal());
        }
    }

    public void writeToSUMO() throws Exception {
        String phase;

        switch (trafficLight.getState()) {
            case GREEN:
                phase = "g";
                break;
            case YELLOW:
                phase = "y";
                break;
            default:
                phase = "r";
        }

        String current = (String) sumoConn.do_job_get(
                Trafficlight.getRedYellowGreenState(trafficLight.getId())
        );

        // Create a full state string based on the current length
        StringBuilder fullState = new StringBuilder();
        for (int i = 0; i < current.length(); i++)
            fullState.append(phase);

        sumoConn.do_job_set(
                Trafficlight.setRedYellowGreenState(trafficLight.getId(), fullState.toString())
        );
    }

    public void toggle() throws Exception {					// toggle light state in GUI
        trafficLight.toggleState();
        writeToSUMO();
    }

    public void updateAutomatic() throws Exception {		// looped in simulation engine
        trafficLight.update();
        writeToSUMO();
    }
}