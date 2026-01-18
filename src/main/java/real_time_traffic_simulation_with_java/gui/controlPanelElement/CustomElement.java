package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * Utility class for creating commonly used UI controls with tooltips
 */
public class CustomElement {
    
    /**
     * Create a text field with specified properties. So instead of repeating code, we can just call this method.
     * @param prompt The prompt text to display
     * @param width The width of the text field
     * @param tooltipText The tooltip text
     * @return Configured TextField
     */
    public static TextField createTextField(String prompt, int width, String tooltipText) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefWidth(width);
        textField.setMaxWidth(width);
        addTooltip(textField, tooltipText);
        return textField;
    }
    
    /**
     * Create a button with specified properties
     * @param text The button text
     * @param width The width of the button
     * @param tooltipText The tooltip text
     * @param backgroundColor The background color (e.g., "#6A6733")
     * @return Configured Button
     */
    public static Button createButton(String text, int width, String tooltipText, String backgroundColor) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setMaxWidth(width);
        button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-text-fill: white;");
        addTooltip(button, tooltipText);
        return button;
    }
    
    /**
     * Add tooltip to a control, because we use it multiple times
     * @param control The control to add tooltip to
     * @param text The tooltip text
     */
    public static void addTooltip(Control control, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(control, tooltip);
    }
}
