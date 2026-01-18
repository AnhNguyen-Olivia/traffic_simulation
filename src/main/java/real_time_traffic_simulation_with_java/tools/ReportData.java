package real_time_traffic_simulation_with_java.tools;

import java.util.List;

/**
 * This class represents the data structure for report data used in exporting vehicle information.
 * It encapsulates a list of string arrays, where each array contains details about a vehicle.
 * Techically speaking, this is a wrapper class for passing vehicle data to the ExportingFiles class.
 */
public class ReportData {
    private final List<String[]> vehicleData;

    /**
     * ReportData constructor, use to initialize vehicle data.
     * We are assigning the parameter vehicleData to the class variable vehicleData. 
     * We are using this constructor to create an instance of ReportData with the provided vehicle data.
     * @param vehicleData the list of string arrays containing vehicle information
     */
    public ReportData(List<String[]> vehicleData) {
        this.vehicleData = vehicleData;
    }

    /**
     * Getter for vehicle data
     * @return the list of string arrays containing vehicle information
    */
    public List<String[]> getVehicleData() {
        return vehicleData;
    }
}