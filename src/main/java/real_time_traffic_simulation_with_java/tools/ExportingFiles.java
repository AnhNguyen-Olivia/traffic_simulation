package real_time_traffic_simulation_with_java.tools;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles CSV export on a background thread.
 * Main thread queues data, worker thread writes to file.
 */
public class ExportingFiles {
    private static final Logger LOGGER = Logger.getLogger(ExportingFiles.class.getName());
    
    /**
     * Thread components for exporting CSV data in the background
    */
    private final ExecutorService executor;
    private final BlockingQueue<ReportData> queue;
    private volatile boolean running;

    // The CSV manager to handle CSV file operations
    private final CSVManager csvManager; 

    /**
     * Create ExportingFiles with background worker thread.
    */
    public ExportingFiles() {
        // Initialize CSV manager and thread components
        this.csvManager = new CSVManager();
        this.queue = new LinkedBlockingQueue<>(1000);
        this.executor = Executors.newSingleThreadExecutor();
        this.running = true;

        // Start background worker thread
        startWorker();
        LOGGER.log(Level.INFO, "ExportingFiles started. CSV: " + csvManager.getFilePath());
    }

    /**
     * Background worker thread to process queued CSV data.
     * This will run on a seperate thread.
     * Polls the queue for data and writes to CSV.
    */
    private void startWorker() {
        executor.submit(() -> {
            LOGGER.log(Level.INFO, "Export worker thread started.");

            while (running || !queue.isEmpty()) {
                try {
                    ReportData data = queue.poll(500, TimeUnit.MILLISECONDS);

                    if (data != null && data.getVehicleData() != null) {
                        csvManager.updateCSV(data.getVehicleData());
                        LOGGER.log(Level.FINE, "Thread: " + Thread.currentThread().getName() + " exported " + data.getVehicleData().size() + " rows.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING, "Export worker interrupted.");
                    break;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Export error: " + e.getMessage(), e);
                }
            }
            LOGGER.log(Level.INFO, "Export worker thread stopped.");
        });
    }

    /**
     * Queue vehicle data for CSV export (non-blocking).
     * @param vehicleData Data to write to CSV
     */
    public void queueCSV(List<String[]> vehicleData) {
        if (!queue.offer(new ReportData(vehicleData))) {
            LOGGER.log(Level.WARNING, "Export queue full. Data dropped.");
        }
    }

    /**
     * Get the file path of the current CSV file.
     * @return CSV file path
     */
    public String getCSVFilePath() {
        return csvManager.getFilePath();
    }

    /**
     * Get the timestamp of the current CSV file for PDF export.
     * @return CSV timestamp
     */
    public String getCSVTimerstamp() {
        return csvManager.getTimeStamp();
    }

    /**
     * Shutdown the export service. Waits for queued data to be written.
     */
    public void shutdown() {
        LOGGER.log(Level.INFO, "Shutting down ExportingFiles...");
        running = false;
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                LOGGER.log(Level.WARNING, "Forced shutdown.");
            }
            csvManager.closeCSV();
            LOGGER.log(Level.INFO, "ExportingFiles shut down. File: " + csvManager.getFilePath());
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}