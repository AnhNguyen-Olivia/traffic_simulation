package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * Vehicle injection component with input fields and controls
 */
public class VehicleInjectionPane extends VBox {
    
    private final TextField inputVnumber;
    private final TextField inputSpeed;
    private final ComboBox<String> vehicleColor;
    private final ComboBox<String> startEdge;
    private final ComboBox<String> endEdge;
    private final Button injectButton;
    private final SimulationEngine simulationEngine;
    
    public VehicleInjectionPane(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
        this.setSpacing(10);
        
        // Number and speed inputs
        inputVnumber = createTextField("1", 105, "Enter the number of vehicle you want to inject");
        inputSpeed = createTextField("max", 105, "Enter the speed of vehicle you want to inject (in km/h)");
        
        // Color selection
        vehicleColor = new ComboBox<>();
        vehicleColor.getItems().addAll(Color.ListofAllColor);
        vehicleColor.setPromptText("Color");
        vehicleColor.setPrefWidth(105);
        vehicleColor.setMaxWidth(105);
        addTooltip(vehicleColor, "Select color for your vehicle(s)");
        
        HBox inputRow = new HBox(10, inputVnumber, inputSpeed);
        
        // Edge selection
        startEdge = new ComboBox<>();
        startEdge.setPromptText("Start edge");
        startEdge.setPrefWidth(105);
        startEdge.setMaxWidth(105);
        
        endEdge = new ComboBox<>();
        endEdge.setPromptText("End edge");
        endEdge.setPrefWidth(105);
        endEdge.setMaxWidth(105);
        
        populateEdges();
        addTooltip(startEdge, "Select your start edge");
        addTooltip(endEdge, "Select your end edge");
        
        HBox edgeRow = new HBox(10, startEdge, endEdge);
        
        // Inject button
        injectButton = new Button("Inject vehicle");
        injectButton.setPrefWidth(105);
        injectButton.setMaxWidth(105);
        injectButton.setStyle("-fx-background-color: #6A6733; -fx-text-fill: white;");
        addTooltip(injectButton, "Press to inject vehicle");
        injectButton.setOnAction(e -> handleInject());
        
        HBox colorButtonRow = new HBox(10, vehicleColor, injectButton);
        
        this.getChildren().addAll(inputRow, edgeRow, colorButtonRow);
    }

    /**
     * Create a text field with specified properties. So instead of repeating code, we can just call this method.
     * @param prompt
     * @param width
     * @param tooltipText
     * @return
    */
    private TextField createTextField(String prompt, int width, String tooltipText) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefWidth(width);
        textField.setMaxWidth(width);
        addTooltip(textField, tooltipText);
        return textField;
    }
    
    /**
     * Add tooltip to a control, because we use it multiple times
     * @param control
     * @param text
    */
    private void addTooltip(Control control, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(control, tooltip);
    }
    
    /**
     * Populate start and end edge combo boxes
    */
    private void populateEdges() {
        try {
            startEdge.getItems().addAll(simulationEngine.getAllEdgeIDs());
            endEdge.getItems().addAll(simulationEngine.getAllEdgeIDs());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handle inject action
    */
    private void handleInject() {
        try {
            String vehicleNumber = inputVnumber.getText().trim();
            String speed = inputSpeed.getText().trim();
            String vColor = vehicleColor.getValue();
            String startE = startEdge.getValue();
            String endE = endEdge.getValue();
            
            // Defaults
            if (vehicleNumber.isEmpty()) vehicleNumber = "1";
            int vNumber = Integer.parseInt(vehicleNumber);
            
            String fSpeed;
            if (speed.isEmpty()) {
                fSpeed = "max";
            } else {
                fSpeed = Float.toString(Float.parseFloat(speed) / 3.6f);
            }
            
            if (vColor == null || vColor.isEmpty()) {
                vColor = "WHITE";
                return;
            }
            
            if (startE == null || startE.isEmpty()) {
                System.out.println("Error, start edge is empty");
                return;
            }
            
            if (endE == null || endE.isEmpty()) {
                System.out.println("Error, end edge is empty");
                return;
            }
            
            simulationEngine.injectVehicle(vNumber, startE, endE, vColor, fSpeed);
            
        } catch (NumberFormatException ex) {
            System.out.println("Error: Please enter a positive integer number.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}