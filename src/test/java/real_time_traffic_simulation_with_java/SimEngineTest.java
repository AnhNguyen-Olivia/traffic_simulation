// package real_time_traffic_simulation_with_java;
// import java.util.List;

// import javafx.scene.Group;
// import real_time_traffic_simulation_with_java.cores.SimulationEngine;

// public class SimEngineTest {
//     public static void main(String[] args) {
//         try {
//             SimulationEngine engine = new SimulationEngine();

//             List<Group> mapTls = engine.getMapTls();

//             // Advance the simulation by a few steps to see the effect
//             for (int i = 0; i < 100; i++) {
//                 engine.stepSimulation();
//                 System.out.println("Step: " + i);

//                 engine.updateMapTls();

//                 Thread.sleep(100); // Pause for a short duration to simulate real-time progression
//             }

//             // Stop the simulation
//             engine.stopSimulation();


//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }
