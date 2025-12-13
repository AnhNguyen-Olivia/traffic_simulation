// package real_time_traffic_simulation_with_java.gui;
// import real_time_traffic_simulation_with_java.cores.TrafficLightData;
// import real_time_traffic_simulation_with_java.cores.SimulationEngine;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import javafx.scene.Group;
// import javafx.scene.control.Tooltip;
// import javafx.scene.layout.Pane;
// import javafx.scene.shape.Polygon;
// import javafx.scene.transform.Scale;
// import javafx.util.Duration;

// public class trafficLightPane extends Pane {

//     private SimulationEngine simulationEngine;
    
//     private List<TrafficLightData> TrafficLightDataList;

//     //using Hashmap to store traffic light ID and its corresponding list of Polygons
//     private Map<String, Group> trafficLightGroups = new HashMap<>();


//     public trafficLightPane(SimulationEngine engine, List<TrafficLightData> tlDataList){
//         this.simulationEngine = engine;
//         this.TrafficLightDataList = tlDataList;
//         addTrafficLight();
//         this.setStyle("-fx-background-color: rgba(94, 40, 155, 0); -fx-background-radius: 10;");
//     }

//     //method to get current color of traffic light           
//     private javafx.scene.paint.Color getTlCurrentColor(String colorState){
//         switch(colorState){
//             case "r":
//                 return javafx.scene.paint.Color.RED; 
//             case "y":
//                 return javafx.scene.paint.Color.YELLOW; 
//             case "g":
//                 return javafx.scene.paint.Color.GREEN;
//             default:
//                 return javafx.scene.paint.Color.GRAY;
//         }
//     }

//     // Create a method to add the traffic light polygons to the pane
//     public void addTrafficLight(){
//         for (TrafficLightData tlData : TrafficLightDataList){
//             Group tlGroup = new Group();
            
//             List<Polygon> shapes = tlData.getShape();
//             List<String> colors = tlData.getColorList();
            
//             // Loop to add traffic light and its color
//             for(int i = 0; i < shapes.size(); i++){
//                 Polygon shape = shapes.get(i);
//                 String colorState = colors.get(i);
//                 shape.setFill(getTlCurrentColor(colorState));
//                 tlGroup.getChildren().add(shape);

//                 // Scale up the traffic light for visibility (will adjust later)
//                 // shape.setScaleX(10);
//                 // shape.setScaleY(10);

//                 Scale scale = new Scale (1, 1, 0, 0);
//                 shape.getTransforms().add(scale);

//                 scale.setX(2);
//                 scale.setY(2);
                

//                 Tooltip tooltip = new Tooltip("Traffic Light: " + tlData.getTlID());
//                 Tooltip.install(tlGroup, tooltip);

//                 final String tlID = tlData.getTlID(); 
                
//                 // Add mouse entered/exited for debugging
//                 shape.setOnMouseEntered(e -> {
//                     shape.setStroke(javafx.scene.paint.Color.BLUE);
//                     shape.setStrokeWidth(0.1);
//                 });
                
//                 shape.setOnMouseExited(e -> {
//                     shape.setStroke(javafx.scene.paint.Color.BLACK);
//                     shape.setStrokeWidth(0.1);
//                 });
                
//                 shape.setOnMouseClicked(event -> {
//                     System.out.println("Mouse clicked on traffic light: " + tlID + ", click count: " + event.getClickCount());
                    
//                     if(event.getClickCount() == 2){
//                         try{
//                             System.out.println("=== Double-clicked traffic light: " + tlID + " ===");
//                             // Visual feedback - flash white briefly
//                             shape.setStroke(javafx.scene.paint.Color.WHITE);
//                             shape.setStrokeWidth(0.1);
                            
//                             simulationEngine.toggleSingleTl(tlID);
//                             System.out.println("Toggle command sent");
                            
//                             // Immediately update visual after toggle
//                             List<TrafficLightData> lights = simulationEngine.getMapTrafficLights();
//                             for (TrafficLightData tl : lights) {
//                                 if(tl.getTlID().equals(tlID)){
//                                     String newPhase = String.join("", tl.getColorList());
//                                     updatePhase(tlID, newPhase);
//                                     System.out.println("Updated to phase: " + newPhase);
//                                     break;
//                                 }
//                             }
                            
//                             // Reset stroke after brief delay
//                             javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
//                             pause.setOnFinished(e -> {
//                                 shape.setStroke(javafx.scene.paint.Color.BLACK);
//                                 shape.setStrokeWidth(0.5);
//                             });
//                             pause.play();
//                         } catch(Exception e){
//                             System.err.println("Error toggling traffic light: " + e.getMessage());
//                             e.printStackTrace();
//                         }
//                         event.consume();
//                     }
//                 });
                
//                 // Enable mouse interaction
//                 shape.setMouseTransparent(false);
//             }
//             trafficLightGroups.put(tlData.getTlID(), tlGroup);
//             this.getChildren().add(tlGroup);
//         }
//     }

//     public void updatePhase(String tlID, String newPhase){
//         Group tlGroup = trafficLightGroups.get(tlID);
//         if(tlGroup != null && newPhase != null){
//             List<javafx.scene.Node> children = tlGroup.getChildren();
//             for(int i = 0; i < Math.min(children.size(), newPhase.length()); i++){
//                 String colorState = String.valueOf(newPhase.charAt(i));
//                 ((Polygon) children.get(i)).setFill(getTlCurrentColor(colorState));
//             }
//             // Update the TrafficLightData as well
//             for(TrafficLightData tlData: TrafficLightDataList){
//                 if(tlData.getTlID().equals(tlID)){
//                     tlData.setColor(newPhase);
//                     break;
//                 }
//             }
//         }
//     }

// }