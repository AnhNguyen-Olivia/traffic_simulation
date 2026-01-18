package real_time_traffic_simulation_with_java.tools.PDFmethod;

import tech.tablesaw.api.Table;
import java.util.Arrays;
import java.util.List;


// ----------------------------------------------------
// RETRIEVE PDF CONTENT FROM CSV FILE
// ----------------------------------------------------
/**
 * Retrieve overall data from CSV file for summary
 * @param csv_table
 * @return List<String[]> {totalVehicleCount, [color], [count by color], [congested edges]}
 */
public final class OverallFromCSV {
    private OverallFromCSV() {
        // private constructor to prevent instantiation
    }

    /**
     * Retrieve overall data from CSV file for summary
     * @param csv_table
     * @return List<String[]> {totalVehicleCount, [color], [count by color], [congested edges]}
     */
    public static final List<String[]> retrieveOverallDataFromCSV(Table csv_table) {
        // If no vehicle was injected (empty CSV)
        if(csv_table.rowCount() == 0) {
            return List.of(
                new String[]{"0"}, // totalVehicleCount
                new String[]{},    // [color]
                new String[]{},    // [count by color]
                new String[]{}     // [congested edges]
            );
        }

        List<String[]> data = new java.util.ArrayList<>();

        // Total number of vehicles injected
        Table uniqueVehicles = csv_table.selectColumns("vehicle id", "vehicle color").dropDuplicateRows();  // keep one row per vehicle
        int uniqueVehicleCount = uniqueVehicles.rowCount();
        data.add(new String[]{String.valueOf(uniqueVehicleCount)});

        // Total number of vehicles injected by color
        Table countByColor = uniqueVehicles.countBy("vehicle color");
        String[] colors = countByColor.stringColumn("vehicle color").asObjectArray();
        Integer[] counts = countByColor.intColumn("Count").asObjectArray();
        String[] counts_as_str = Arrays.stream(counts)
                        .map(d -> String.format("%d", d))
                        .toArray(String[]::new);
        data.add(colors);
        data.add(counts_as_str);

        // Edge id of edges congested at least once
        Table congestedEdgesTable = csv_table.where(csv_table.booleanColumn("edge congestion status").isTrue());
        String[] congestedEdges = congestedEdgesTable.stringColumn("vehicle is on edge").unique().asObjectArray();
        Arrays.sort(congestedEdges);
        data.add(congestedEdges);
        
        return data;
    }
}
