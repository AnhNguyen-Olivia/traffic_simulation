package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Junction;
import de.tudresden.sumo.objects.SumoGeometry;

import java.util.ArrayList;
import java.util.List;
import real_time_traffic_simulation_with_java.cores.JunctionData;


/**
 * Wrapper class for TraaS to manage junctions in the simulation
 */
public class JunctionManager {
    
    /** Connection to Sumo */
    private final SumoTraciConnection conn;
    /** Stores List of visualization objects for junctions */
    private List<JunctionData> junctionDataList = new java.util.ArrayList<>();

    /**
     * Wrapper class for TraaS to manage junctions in the simulation
     * @param connection connection to Sumo
    */
    public JunctionManager(SumoTraciConnection connection) {
        this.conn = connection;
    }


    /**
     * Get list of junctions IDs
     * @return a List type String of junction IDs
    */ 
    public List<String> getIDList() throws Exception {
        List<String> IDs = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<String> junctionIDList = (List<String>) conn.do_job_get(Junction.getIDList());
        for (String ID : junctionIDList) {
            if (ID.startsWith("J")) {
                IDs.add(ID);
            }
        }
        return IDs;
    }

    
    /**
     * Get number of junctions
     * @return an int number of junctions
    */
    public int getCount() throws Exception {
        return this.getIDList().size();
    }


    /**
     * Get shape of the junction
     * @param junctionID the ID of the junction
     * @return SumoGeometry shape of the junction
    */
    public SumoGeometry getShape(String junctionID) throws Exception {
        return (SumoGeometry) conn.do_job_get(Junction.getShape(junctionID));
    }


    /**
     * Create and get a List of JunctionData for all junctions
     * @return a List of JunctionData for all junctions
    */
    public List<JunctionData> getJunctionDataList() throws Exception {
        if(junctionDataList.isEmpty()){
            List<String> IDs = this.getIDList();
            for (String id : IDs) {
                JunctionData junctiondata = new JunctionData(
                        id,
                        this.getShape(id)
                );
                junctionDataList.add(junctiondata);
            }
        }
        return junctionDataList;
    }
}
