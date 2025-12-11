package real_time_traffic_simulation_with_java.wrapper;

import it.polito.appeal.traci.SumoTraciConnection;
import de.tudresden.sumo.cmd.Junction;
import de.tudresden.sumo.objects.SumoGeometry;

import java.util.ArrayList;
import java.util.List;
import real_time_traffic_simulation_with_java.cores.JunctionData;

/**
 * JunctionManager is a wrapper class for SumoTraciConnection to manage junctions in the simulation
 * @TestedCompleted
 */

public class JunctionManager {
    
    /**
     * private SumoTraciConnection conn
     * private List<JunctionData> junctionDataList
    */
    private final SumoTraciConnection conn;
    private List<JunctionData> junctionDataList = new java.util.ArrayList<>();

    /**
     * Connection to Sumo
     * @param connection
     * @throws Exception
    */
    public JunctionManager(SumoTraciConnection connection) throws Exception {
        this.conn = connection;
    }


    /**
     * Get list of junctions IDs
     * @return a List type String of junction IDs
     * @throws Exception
     * @Tested
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
     * @throws Exception
     * @Tested
    */
    public int getCount() throws Exception {
        return this.getIDList().size();
    }


    /**
     * Get shape of the junction
     * @return SumoGeometry shape of the junction
     * @throws Exception
     * @Tested
    */
    public SumoGeometry getShape(String junctionID) throws Exception {
        return (SumoGeometry) conn.do_job_get(Junction.getShape(junctionID));
    }


    /**
     * Create and get a List of JunctionData for all junctions
     * @return a List of JunctionData for all junctions
     * @throws Exception
     * @Tested
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
