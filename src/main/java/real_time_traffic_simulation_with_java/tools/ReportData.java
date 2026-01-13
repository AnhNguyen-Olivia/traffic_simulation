package real_time_traffic_simulation_with_java.tools;

import java.util.List;

/**
 * Simple container for vehicle data to be exported to CSV.
 */
public class ReportData {
    private final List<String[]> vehicleData;

    public ReportData(List<String[]> vehicleData) {
        this.vehicleData = vehicleData;
    }

    public List<String[]> getVehicleData() {
        return vehicleData;
    }
}