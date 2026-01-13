package real_time_traffic_simulation_with_java.gui.dashboardSection;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import javafx.scene.chart.CategoryAxis;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.BarChart;
import real_time_traffic_simulation_with_java.alias.Metrics;
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * ChartSection class represents a bar chart displaying live statistics of edges in the traffic simulation. <br>
 * It shows average speed, density, and halting number for each edge, updating periodically.
 */
public class ChartSection extends BarChart<Number, String> {
    private static final Logger logger = Logger.getLogger(ChartSection.class.getName());

    private NumberAxis xAxis;
    private CategoryAxis yAxis;
    private SimulationEngine simulationEngine;
    private List<String> edgeIds;

    private XYChart.Series<Number, String> avgSpeed;
    private XYChart.Series<Number, String> density;
    private XYChart.Series<Number, String> haltingNumber;

    private Timeline updateChart;

    /** 
     * Constructor for ChartSection.
     * @param simulationEngine The simulation engine to fetch edge statistics from.
     */
    public ChartSection(SimulationEngine simulationEngine) {
        // Initialize self and fields
        super(new NumberAxis(), new CategoryAxis());
        initializeCoreAttributes(simulationEngine);
        setupChart();
        setupUpdateChart();
    }


    /**
     * Private helper method: initialize attributes.
     */
    private void initializeCoreAttributes(SimulationEngine simulationEngine) {
        this.xAxis = (NumberAxis) this.getXAxis();
        this.yAxis = (CategoryAxis) this.getYAxis();
        this.simulationEngine = simulationEngine;
        try{
            this.edgeIds = simulationEngine.getAllEdgeIDs();
        } catch (IllegalStateException e) {
            logger.log(Level.SEVERE, "Failed to fetch edge IDs from simulation engine.", e);
            logger.log(Level.SEVERE, "ChartSection was not fully initialized.");
            return;
        }
        this.edgeIds.sort(String::compareTo);
        // Chart series
        this.avgSpeed = new XYChart.Series<>();
        this.avgSpeed.setName("Average Speed (km/h)");
        this.density = new XYChart.Series<>();
        this.density.setName("Density (veh/km)");
        this.haltingNumber = new XYChart.Series<>();
        this.haltingNumber.setName("Travel Time (s)");
    }

    /**
     * Private helper method: set up chart.
     */
    private void setupChart() {
        // Setup chart properties
        this.setPrefHeight(edgeIds.size() * 30 + 50);
        this.setPrefWidth(Metrics.DASHBOARD_WIDTH + 50);
        this.setAnimated(false);
        // Setup axes
        this.setTitle("Live Statistics");
        xAxis.setLabel("Value");
        xAxis.setTickLabelRotation(90);
        yAxis.setLabel("Edge ID");
        // Add series to chart
        this.getData().add(avgSpeed);
        this.getData().add(density);
        this.getData().add(haltingNumber);
    }

    /**
     * Private helper method: setup updating chart periodically.
     */
    public void setupUpdateChart() {
        this.updateChart = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            try{
                setValue();
            } catch(IllegalStateException ex) {
                this.updateChart.stop();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }), new KeyFrame(Duration.millis(Metrics.CONNECT_SPEED_MS))); // Time line stop after this duration (or loop if setCycleCount)
        // Ensure the timeline runs indefinitely
        this.updateChart.setCycleCount(Animation.INDEFINITE);
        this.updateChart.play();
    }

    /**
     * Private helper method: update chart values.
     */
    private void setValue() {
        avgSpeed.getData().clear();
        density.getData().clear();
        haltingNumber.getData().clear();
        for (String edgeId : edgeIds) {
            double[] edgeStats = simulationEngine.getEdgeStats(edgeId);
            avgSpeed.getData().add(new XYChart.Data<>(edgeStats[0], edgeId));
            density.getData().add(new XYChart.Data<>(edgeStats[1], edgeId));
            haltingNumber.getData().add(new XYChart.Data<>(edgeStats[2], edgeId));
        }
    }


}
