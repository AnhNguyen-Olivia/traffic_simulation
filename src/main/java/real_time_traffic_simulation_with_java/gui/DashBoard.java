package real_time_traffic_simulation_with_java.gui;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;
import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
//import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class DashBoard extends Pane {
    public DashBoard(){
        TextField inputVnumber = new TextField();
        inputVnumber.setPromptText("1");

        ComboBox<String> startEdge = new ComboBox<>();
        Tooltip startEdgeTooltip = new Tooltip("Selec your start edge");
        startEdge.setTooltip(startEdgeTooltip);

        ComboBox<String> EndEdge = new ComboBox<>();
        Tooltip EndEdgeTooltip = new Tooltip("Selec your end edge");
        startEdge.setTooltip(EndEdgeTooltip);
        
        VBox vbox = new VBox();
        vbox.getChildren().addAll(inputVnumber, startEdge, EndEdge);
        //vbox.setSpacing(10);
        this.getChildren().add(vbox);
        this.setVisible(true);
    }
    
}
