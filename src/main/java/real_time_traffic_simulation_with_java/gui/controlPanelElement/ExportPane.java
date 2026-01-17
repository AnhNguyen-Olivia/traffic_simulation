package real_time_traffic_simulation_with_java.gui.controlPanelElement;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import real_time_traffic_simulation_with_java.alias.Color;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import real_time_traffic_simulation_with_java.tools.ExportingFiles;
import real_time_traffic_simulation_with_java.tools.PDFExporter;

/**
 * Export component with CSV export button
 */
public class ExportPane extends VBox {
    private static final Logger LOGGER = Logger.getLogger(ExportPane.class.getName());
    
    private final ComboBox<String> colorFilter;
    private final CheckBox congestedOnlyCheckBox;
    private final Button exportButton;
    private final SimulationEngine simulationEngine;
    private final ExportingFiles exportingFiles;
    
    public ExportPane(SimulationEngine simulationEngine, ExportingFiles exportingFiles) {
        this.simulationEngine = simulationEngine;
        this.exportingFiles = exportingFiles;
        this.setSpacing(10);
        
        // Color filter
        colorFilter = new ComboBox<>();
        List<String> colorOptions = new java.util.ArrayList<>(Color.ListofAllColor);
        colorOptions.add(0, ""); // Empty option for no filter
        colorFilter.getItems().addAll(colorOptions);
        colorFilter.setPromptText("Color");
        colorFilter.setPrefWidth(105);
        colorFilter.setMaxWidth(105);
        CustomElement.addTooltip(colorFilter, "Select color to filter vehicles");
        
        // Congested edge checkbox
        congestedOnlyCheckBox = new CheckBox("Only include congested edge");
        congestedOnlyCheckBox.setStyle("-fx-text-fill: white;");
        CustomElement.addTooltip(congestedOnlyCheckBox, "Check to export only congested edges");

        exportButton = CustomElement.createButton("Export PDF", 220, 
            "Press to export current PDF log file.", "#6A6733");
        exportButton.setOnAction(e -> handleExport());
        
        this.getChildren().addAll(colorFilter, congestedOnlyCheckBox, exportButton);
    }
    
    /**
     * Returns whether the "Only include congested edge" checkbox is selected
     * @return true if checked, false otherwise
     */
    public boolean isCongestedOnlySelected() {
        return congestedOnlyCheckBox.isSelected();
    }

    private void handleExport() {

        /**
         * We want to disable the export button and change its text to "Exporting..."
         * Thus the user knows that the export process has started and cannot click the button again
        */
        exportButton.setDisable(true);
        exportButton.setText("Exporting...");


        /**
         * Retrieve filter options
         * If no color is selected or the user select the empty option, we set the color filter to an empty string
         * If the congested only checkbox is selected, we set the congestedOnly variable to true
        */
        String selectedColor = colorFilter.getValue();
        if(selectedColor == null) {
            selectedColor = "";
        }
        boolean congestedOnly = congestedOnlyCheckBox.isSelected();

        /**
         * Retrieve CSV file path and timestamp from ExportingFiles instance
         * Also retrieve the simulation data for PDF export
         * If an exception occurs during data retrieval, we re-enable the export button and reset its text
        */
        String csvPath = exportingFiles.getCSVFilePath();
        String csvTimeStamp = exportingFiles.getCSVTimerstamp();

        List<String[]> simulationData;
        try{
            simulationData = simulationEngine.dataForPDF();
        }catch(Exception ex){
            exportButton.setDisable(false);
            exportButton.setText("Export PDF");
            LOGGER.log(Level.WARNING, "Failed to retrieve simulation data for PDF export: " + ex.getMessage(), ex);
            return;
        }
        
        /**
         * Make a local copy of the filter options for use in the background thread
         * This is necessary because the variables need to be effectively final to be accessed from within the thread
        */
        final String finalSelectedColor = selectedColor;
        final boolean finalCongestedOnly = congestedOnly;

        new Thread(() -> {
           try{
            PDFExporter.exportSummary(csvPath, csvTimeStamp, finalSelectedColor, finalCongestedOnly, simulationData);
            
            /**
             * Update button state back on the JavaFX Application Thread after export completion
             * javafx.application.Platform.runLater runs the specified Runnable on the JavaFX Application Thread at some unspecified time in the future. 
             * This method, which may be called from any thread, will post the Runnable to an event queue and then return immediately to the caller. 
             * The Runnables are executed in the order they are posted. 
             * A runnable passed into the runLater method will be executed before any Runnable passed into a subsequent call to runLater. 
             * If this method is called after the JavaFX runtime has been shutdown, the call will be ignored: the Runnable will not be executed and no exception will be thrown.
             * -Official JavaFX Documentation-
            */
            
            javafx.application.Platform.runLater(() -> {
                exportButton.setDisable(false);
                exportButton.setText("Export PDF");
                LOGGER.log(Level.INFO, "PDF export completed successfully.");

            });
           } catch (Exception ex){
            // Update button state back on the JavaFX Application Thread in case of error
            javafx.application.Platform.runLater(() -> {
                exportButton.setDisable(false);
                exportButton.setText("Export PDF");
                LOGGER.log(Level.WARNING, "Failed to export PDF: " + ex.getMessage(), ex);
            });
           }
        }).start();
    }
}