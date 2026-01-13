package real_time_traffic_simulation_with_java.tools;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportingFiles {
    private static final Logger LOGGER = Logger.getLogger(ExportingFiles.class.getName());
    
    private final ExecutorService exportingFiles;
    private final BlockingQueue<ReportData> queue;
    private volatile boolean running;

    private final CSVManager csvManager;


    public ExportingFiles(){
        
        this.csvManager = new CSVManager();

        this.queue = new LinkedBlockingQueue<>(1000);
        this.exportingFiles = Executors.newSingleThreadExecutor();
        this.running = true;

        startWorker();
        LOGGER.log(Level.INFO, "ExportingFiles service started. File: " + csvManager.getFilePath());
    }

    private void startWorker(){
        exportingFiles.submit(() ->{
            LOGGER.log(Level.INFO, "ExportingFiles worker thread started.");

            while(running || !queue.isEmpty()) {
                try {

                    ReportData data = queue.poll(500, TimeUnit.MILLISECONDS);

                    if (data!= null && data.getVehicleData() != null){
                        LOGGER.log(Level.FINE, "[WRITE] Thread: " + Thread.currentThread().getName());
                        if(data.shouldExportCSV()){
                            csvManager.updateCSV(data.getVehicleData());
                        }
                        // Will add pdf export here =))))))))
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING, "ExportingFiles worker thread interrupted.");
                    break;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error in ExportingFiles worker thread: " + e.getMessage(), e);
                }
            }
            LOGGER.log(Level.INFO, "ExportingFiles worker thread stopped.");
        });
    }

    public void queueExport(ReportData data){
        if (!queue.offer(data)){
            LOGGER.log(Level.WARNING, "ExportingFiles queue is full. Dropping export data.");
        }
    }

    public void queueCSV(List<String[]> vehicleData){
        queueExport(new ReportData(null, vehicleData, null, true, false));
    }

    public void shutdown(){
        LOGGER.log(Level.INFO, "Shutting down ExportingFiles service...");
        running = false;
        exportingFiles.shutdown();
        try{
            if (!exportingFiles.awaitTermination(60, TimeUnit.SECONDS)) {
                exportingFiles.shutdownNow();
                LOGGER.log(Level.WARNING, "ExportingFiles service did not terminate in the allotted time. Forced shutdown initiated.");
            }
            csvManager.closeCSV();
            LOGGER.log(Level.INFO, "ExportingFiles service shut down successfully. File: " + csvManager.getFilePath());

        } catch (InterruptedException e) {
            exportingFiles.shutdownNow();
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "ExportingFiles service shutdown interrupted.");
        }
    }
}
