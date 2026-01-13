package real_time_traffic_simulation_with_java.tools;

import java.util.List;

public class ReportData {
    private final String simulationStep;
    private final List<String[]> vehicleData;
    private final String filterDesciption;
    private final boolean exportCSV;
    private final boolean exportPDF;

    public ReportData(String simulationStep, List<String[]> vehicleData, String filterDesciption, boolean exportCSV, boolean exportPDF) {
        this.simulationStep = simulationStep;
        this.vehicleData = vehicleData;
        this.filterDesciption = filterDesciption;
        this.exportCSV = exportCSV;
        this.exportPDF = exportPDF;
    }

    public String getSimulationStep() {
        return simulationStep;
    }

    public List<String[]> getVehicleData() {
        return vehicleData;
    }

    public String getFilterDesciption() {
        return filterDesciption;
    }

    public boolean shouldExportCSV() {
        return exportCSV;
    }

    public boolean shouldExportPDF() {
        return exportPDF;
    }
}