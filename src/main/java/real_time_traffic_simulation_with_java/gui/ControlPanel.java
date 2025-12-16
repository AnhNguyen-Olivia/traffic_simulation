package real_time_traffic_simulation_with_java.gui;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;


/**
 * Control panel pane including vehicle injection tool, stress test tool, toggle all traffic light tool. <br>
 * Group all tools in Hboxes then add them to the Vbox and add the VBox to control panel pane. <br>
 * Add a cat cover as background image <br>
 * (Happy Christmas!) ᓚ₍⑅^- .-^₎ -ᶻ 𝗓 𐰁
*/
public class ControlPanel extends Pane {

    /**
     * Create instance of SimulationEngine
    */
    private SimulationEngine simulationEngine;

    /**
     * Control panel pane including vehicle injection tool, stress test tool, toggle all traffic light tool. <br>
     * Group all tools in Hboxes then add them to the Vbox and add the VBox to control panel pane. <br>
     * Add a cat cover as background image <br>
     * (Happy Christmas!) ᓚ₍⑅^- .-^₎ -ᶻ 𝗓 𐰁
     * @param simulationEngine
    */
    public ControlPanel(SimulationEngine simulationEngine){

        /**
         * Initialize simulationEngine
        */
        this.simulationEngine = simulationEngine;

        /**
         * Create vehicle text field, set perfered width and max width, add tooltip
        */
        TextField inputVnumber = new TextField();
        inputVnumber.setPromptText("1");
        inputVnumber.setPrefWidth(100);
        inputVnumber.setMaxWidth(100);
        Tooltip inputToolTip = new Tooltip("Enter the number of vehicle you want to inject");
        inputToolTip.setShowDelay(Duration.ZERO);
        Tooltip.install(inputVnumber, inputToolTip);

        /**
         * Create vehicle color combobox, add all color options, add tooltip.
         * Use Color alias class to get all color options.
        */
        ComboBox<String> vehicleColor = new ComboBox<>();
        vehicleColor.getItems().addAll(Color.ListofAllColor);
        Tooltip vehicleColorTooltip = new Tooltip("Select color for your vehicle(s)");
        vehicleColorTooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(vehicleColor, vehicleColorTooltip);

        /**
         * Group vehicle number input and color selection in an HBox
        */
        HBox inputAndColor = new HBox(10, inputVnumber, vehicleColor);

        /**
         * Create start edge combobox, add all edge IDs from simulationEngine, add tooltip
        */
        ComboBox<String> startEdge = new ComboBox<>();
        try {
            startEdge.getItems().addAll(simulationEngine.getAllEdgeIDs());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Tooltip startEdgeTooltip = new Tooltip("Select your start edge");
        startEdgeTooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(startEdge, startEdgeTooltip);

        /**
         * Create end edge combobox, add all edge IDs from simulationEngine, add tooltip
        */
        ComboBox<String> EndEdge = new ComboBox<>();
        try {
            EndEdge.getItems().addAll(simulationEngine.getAllEdgeIDs());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Tooltip EndEdgeTooltip = new Tooltip("Select your end edge");
        EndEdgeTooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(EndEdge, EndEdgeTooltip);

        /**
         * Group start edge and target edge in an HBox
        */
        HBox startAndEnd = new HBox(10, startEdge, EndEdge);

        /**
         * Create vehicle injection button, add tooltip, add event handler for injection
        */
        Button vehicleInjection = new Button("Vehicle Injection");;
        Tooltip vehicleInjectionTooltip = new Tooltip("Press to inject vehicle");
        vehicleInjectionTooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(vehicleInjection, vehicleInjectionTooltip);

        /**
         * Event handler for vehicle injection button
        */
        EventHandler<ActionEvent> injectEvent = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                injectVehicle(inputVnumber, vehicleColor, startEdge, EndEdge);
            }
        };

        /**
         * add event handler to vehicle injection button
        */
        vehicleInjection.setOnAction(injectEvent);
    
        /**
         * Create stress test tool components: input field for number of vehicle, start edge combobox, 
         * stress test button, add tooltips.
        */
        TextField inputStressVnumber = new TextField();
        inputStressVnumber.setPromptText("100");
        inputStressVnumber.setPrefWidth(100);
        inputStressVnumber.setMaxWidth(100);
        Tooltip inputStressTooltip = new Tooltip("Enter the number of vehicle you want to inject. Default is 100");
        inputStressTooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(inputStressVnumber, inputStressTooltip);

        ComboBox<String> startStressEdge = new ComboBox<>();
        try {
            startStressEdge.getItems().addAll(simulationEngine.getAllEdgeIDs());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Tooltip startStressEdgeTooltip = new Tooltip("Select your start edge");
        startStressEdgeTooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(startStressEdge, startStressEdgeTooltip);

        HBox VandStart = new HBox(10, inputStressVnumber, startStressEdge);
        
        Button StressTest = new Button("Stress Test Tool");
        Tooltip StressTestTooltip = new Tooltip("Stress test tool that allow user to enter stress test mode. Default number is 100, but you can change to any number you want.");
        StressTestTooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(StressTest, StressTestTooltip);

        /**
         * Event handler for stress test button
        */
        EventHandler<ActionEvent> stressEvent = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                stressVehicle(inputStressVnumber, startStressEdge);
            }
        };

        /**
         * add event handler to stress test button
        */
        StressTest.setOnAction(stressEvent);

        /**
         * Create toggle all traffic light button, add tooltip, add event handler for toggling all traffic light
        */
        Button toggleAllTl = new Button("Toggle All Traffic Light");
        Tooltip toggleAllTlTooltip = new Tooltip("Press to toggle all traffic light.");
        toggleAllTlTooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(toggleAllTl, toggleAllTlTooltip);

        EventHandler<ActionEvent> toggleAllEvent = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                try {
                    simulationEngine.toggleAllTls();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        };

        toggleAllTl.setOnAction(toggleAllEvent);

        /**
         * Group all tools in a VBox and add the VBox to control panel pane
        */
        VBox vbox = new VBox();
        vbox.getChildren().addAll(inputAndColor, startAndEnd, vehicleInjection, VandStart, StressTest, toggleAllTl);
        vbox.setSpacing(20);
        this.getChildren().add(vbox);

        /**
         * Set background image for control panel pane, add a cat cover /ᐠ - ˕ -マ ᶻ 𝗓 𐰁
        */
        this.setStyle("-fx-background-image: url('" + getClass().getResource(Path.ConTrolPanelImage).toExternalForm() + "'); " +
                      "-fx-background-size: cover;" + 
                      "-fx-background-repeated: no-repeat;"
                    );


        this.setVisible(true);
    }

    /***
     * Method to inject vehicle into the simulation.
     * @param inputVnumber
     * @param vehicleColor
     * @param startEdge
     * @param EndEdge
     * 
    */
    public void injectVehicle(TextField inputVnumber, ComboBox<String> vehicleColor, ComboBox<String> startEdge, ComboBox<String> EndEdge){
        try{
            String vehicleNumber = inputVnumber.getText().trim(); // Trim whitespace
            String vColor = vehicleColor.getValue();
            String startE = startEdge.getValue();
            String endE = EndEdge.getValue();

            if(vehicleNumber.isEmpty()){
                vehicleNumber = "1"; // Default to 1
            }
            int vNumber = Integer.parseInt(vehicleNumber);

            if(vColor == null || vColor.isEmpty()){
                vColor = "WHITE"; // Default to WHITE
                return;
            }

            if(startE == null || startE.isEmpty()){
                System.out.println("Error, start edge is emtpy");
                return;
            }

            if(endE == null || endE.isEmpty()){
                System.out.println("Error, end edge is emtpy");
                return;
            }

            try {
                // Inject vehicle(s) into the simulation, send the data to simulation engine method of injectVehicle
                this.simulationEngine.injectVehicle(vNumber, startE, endE, vColor);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch(NumberFormatException ex){
            System.out.println("Error: Please enter a positive interger number. \n(Will change this to logging later)");
        }
    }
    
    /**
     * Method to start stress test mode.
     * @param inputStressVnumber
     * @param startStressEdge
    */
    public void stressVehicle(TextField inputStressVnumber, ComboBox<String> startStressEdge){
        try{
            String vehicleNumber = inputStressVnumber.getText().trim();
            String stressEdge = startStressEdge.getValue();

            if(vehicleNumber.isEmpty()){
                vehicleNumber = "100";
            }
            int vNumber = Integer.parseInt(vehicleNumber);

            if(stressEdge == null || stressEdge.isEmpty()){
                System.out.println("Error, start edge is emtpy");
                return;
            }

            try {
                this.simulationEngine.stressTest(vNumber, stressEdge);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch(NumberFormatException ex){
            System.out.println("Error: Please enter a positive interger number. \n(Will change this to logging later)");
        }
    }
}
