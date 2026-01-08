package real_time_traffic_simulation_with_java.gui.mapLayer;

import java.util.List;

import javafx.application.Platform;

import javafx.scene.control.Dialog;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


/**
 * A popup window designed to adjust traffic light phases' durations <br>
 * Returns a List of Integer representing the new durations for each phase upon confirmation <br>
 * If no changes were made or invalid input was provided, returns null <br>
 * Will display appropriate notification alerts upon closing
 */
public class trafficlightPopupWindow extends Dialog<List<Integer>> {
    // ButtonType.OK = ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    /** Confirm button type of the popup window, for easy reference */
    private ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
    /** Stores TextFields for user input, for easy reference */
    private List<TextField> textFields = new java.util.ArrayList<>();

    /**
     * A popup window designed to adjust traffic light phases' durations <br>
     * Returns a List of Integer representing the new durations for each phase upon confirmation <br>
     * If no changes were made or invalid input was provided, returns null <br>
     * Will display appropriate notification alerts upon closing
     * @param tlID The ID of the traffic light
     * @param phasesDuration The current phases durations of the traffic light
     */
    public trafficlightPopupWindow(String tlID, List<Integer> phasesDuration) {
        setupLayout(tlID, phasesDuration);
        setReturn(tlID, phasesDuration);
    }


    /** 
     * Private helper method: Set up the layout of the popup window
     */
    private void setupLayout(String tlID, List<Integer> phasesDuration) {
        this.setTitle("Adjust Traffic Light Phases' Durations");
        // Add confirm button to the dialog pane
        this.getDialogPane().getButtonTypes().add(confirmButtonType);
        // Create grid layout for phase index, current duration, and new duration input
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.add(new Label("Phase Index"), 0, 0);
        grid.add(new Label("Duration (s)"), 1, 0);
        grid.add(new Label("New Duration (s)"), 2, 0);
        for(int i = 0; i < phasesDuration.size(); i++) {
            grid.add(new Label(String.valueOf(i)), 0, i + 1);
            grid.add(new Label(String.valueOf(phasesDuration.get(i))), 1, i + 1);
            TextField tf = new TextField();
            tf.setPrefWidth(75); tf.setMinWidth(75); tf.setMaxWidth(75);
            textFields.add(tf);
            grid.add(tf, 2, i + 1);
        }
        // Create content layout with traffic light ID, instruction, and grid
        Label IDLabel = new Label("Traffic Light ID: " + tlID);
        IDLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: darkcyan; -fx-font-weight: bold;");
        Label instructionLabel = new Label("Enter new duration for each phase and click Confirm to apply changes.\nLeave a field empty to keep the current duration.");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: darkcyan; -fx-font-weight: italic;");
        VBox content = new VBox(10, IDLabel, instructionLabel, grid);
        // Set content to the dialog pane
        this.getDialogPane().setContent(content);
    }

    /**
     * Private helper method: Setup the return value when confirm button is clicked
     * @param tlID
     * @param phasesDuration
     */
    private void setReturn(String TlID, List<Integer> originalPhaseDurations) {
        this.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                List<Integer> newDurations = new java.util.ArrayList<>();
                for(int i = 0; i < textFields.size(); i++) {
                    try {
                        String input = textFields.get(i).getText();
                        // If input is empty, keep original duration
                        if(input == null || input.isEmpty()) {
                            newDurations.add(originalPhaseDurations.get(i));
                        } else {
                            int duration = Integer.parseInt(input);
                            // If input is negative, exit and display alert
                            if(duration <= 0) {
                                setNotiWhenClose("Duration must be positive!", false);
                                return null;
                            // If input is valid positive integer, append to new durations
                            } else {
                                newDurations.add(duration);
                            }
                        }
                    // If input is not integer format, exit and display alert
                    } catch (NumberFormatException e) {
                        setNotiWhenClose("Should enter integer values only!", false);
                        return null;
                    }
                }
                // If no changes were made, exit and display alert
                if (newDurations.equals(originalPhaseDurations)) {
                    setNotiWhenClose("No changes were made to the phase durations.", false);
                    return null;
                }
                // If all inputs are valid and changes were made, return new durations and display success alert
                setNotiWhenClose(String.format("Traffic Light %s phases durations updated successfully.", TlID), true);
                return newDurations;
            }
            return null;
        });
    }
    /** 
     * Private helper method: Set notification alert when popup window is closed
     * @param message The message to be displayed in the alert
     */
    private void setNotiWhenClose(String message, boolean successfully) {
        Alert alert_upon_close = new Alert(Alert.AlertType.NONE,message,ButtonType.OK);
        alert_upon_close.setTitle("Status");
        // Set text color based on success or failure
        Label contentLabel = (Label) alert_upon_close.getDialogPane().lookup(".content.label");
        if(successfully) {
            contentLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        } else {
            contentLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        }
        // Show alert upon closing the popup window, use Platform.runLater to ensure alert is initiated properly
        this.setOnHidden(e -> {
            Platform.runLater(() -> {
                alert_upon_close.show();
            });
        });
    }
    
}
