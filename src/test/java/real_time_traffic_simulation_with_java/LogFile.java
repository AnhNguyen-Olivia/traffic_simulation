// package real_time_traffic_simulation_with_java;

// import real_time_traffic_simulation_with_java.alias.Path;
// import real_time_traffic_simulation_with_java.tools.PDFExporter;
// import real_time_traffic_simulation_with_java.cores.SimulationEngine;

// import tech.tablesaw.api.Table;


// public class LogFile {

//         public static void main(String[] args) {
//             String timeStamp = "2026-01-17 23_30_55";
//             String csvLogFilePath = Path.CsvLogFolder + timeStamp + ".csv";
//             Table csvTable = Table.read().csv(csvLogFilePath);

//             Table congestedEdgesTable = csvTable.where(csvTable.booleanColumn("edge congestion status").isTrue());
//             String[] congestedEdges = congestedEdgesTable.stringColumn("vehicle is on edge").unique().asObjectArray();

//             System.out.println(congestedEdges.getClass().getName());


//             // try{
//             //     SimulationEngine engine = new SimulationEngine();
//             //     PDFExporter.exportSummary(Path.CsvLogFolder + timeStamp + ".csv", 
//             //                                     timeStamp, 
//             //                                     "", 
//             //                                     false,
//             //                                     engine.dataForPDF());
//             //     engine.stopSimulation();
//             // } catch (Exception e) {
//             //     e.printStackTrace();
//             // }



//         }
// }