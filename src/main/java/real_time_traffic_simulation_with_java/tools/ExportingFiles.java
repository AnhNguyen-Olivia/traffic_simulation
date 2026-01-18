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
 *
 * <p>
 * Main thread queues data, worker thread writes to file.
 * Thread components for exporting CSV data in the background.
 * </p>
 *
 * <p>
 * ExecutionService is an Executor that provides methods to manage termination
 * and methods that can produce a Future for tracking progress of one or more
 * asynchronous tasks.
 * </p>
 *
 * <p>
 * BlockingQueue is a Queue that additionally supports operations that wait for
 * the queue to become non-empty when retrieving an element, and wait for space
 * to become available in the queue when storing an element.
 * </p>
 *
 * <p>
 * We choose BlockingQueue because it is thread-safe (i.e. multiple threads can
 * access it concurrently) and it handles synchronization internally.
 * Normal queues like LinkedList are not thread-safe and would require additional
 * synchronization mechanisms (like synchronized blocks) to ensure safe concurrent
 * access, which can be error-prone and less efficient.
 * </p>
 *
 * <p>
 * The combination of ExecutorService and BlockingQueue allows us to efficiently
 * manage background tasks and safely share data between threads.
 * </p>
 *
 * <p>
 * private volatile boolean is used to ensure that changes to the running flag
 * are immediately visible to all threads.
 * This is important for safely stopping the background worker thread when
 * shutting down the export service.
 * If we did not use volatile, there is a risk that the worker thread might not
 * see the updated value of running in a timely manner, or might cache old values,
 * leading to potential issues during shutdown.
 * </p>
 *
 * <p>
 * <i>More note about BlockingQueue:</i>
 * A BlockingQueue is typically used to have one thread produce objects, which
 * another thread consumes.
 * The producing thread will keep producing new objects and insert them into the
 * BlockingQueue, until the queue reaches some upper bound on what it can contain.
 * Its limit, in other words.
 * </p>
 *
 * <p>
 * If the blocking queue reaches its upper limit, the producing thread is blocked
 * while trying to insert the new object.
 * It remains blocked until a consuming thread takes an object out of the queue.
 * </p>
 *
 * <p>
 * The consuming thread keeps taking objects out of the BlockingQueue to process
 * them.
 * If the consuming thread tries to take an object out of an empty queue, the
 * consuming thread is blocked until a producing thread puts an object into the queue.
 * </p>
 * 
 * @see <a href="https://jenkov.com/tutorials/java-util-concurrent/blockingqueue.html">
 *  Jenkov – Java BlockingQueue Tutorial
 *    </a>
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html"> 
 *  Java BlockingQueue Documentation
 *    </a>
 */


public class ExportingFiles {
    private static final Logger LOGGER = Logger.getLogger(ExportingFiles.class.getName());

    private final ExecutorService executor;
    private final BlockingQueue<ReportData> queue;
    private volatile boolean running;

    // The CSV manager to handle CSV file operations
    private final CSVManager csvManager; 

    /**
     * <p>
     * Create ExportingFiles with background worker thread.
     * First it initializes the CSV manager and thread components, then starts the worker thread.
     * </p>
     * 
     * <p>
     * We use a LinkedBlockingQueue with a capacity of 1000 to buffer data between the main thread and the worker thread.
     * This allows the main thread to queue data quickly without blocking, while the worker thread processes the data at its own pace.
     * LinkedBlockingQueue is an optionally-bounded blocking queue based on linked nodes. This queue orders elements FIFO. 
     * </p>
     * 
     * <p>
     * newSingleThreadExecutor is Executor method that Creates an Executor that uses a single worker thread operating off an unbounded queue, 
     * and uses the provided ThreadFactory to create a new thread when needed. 
     * </p>
     * 
     * <p>
     * We use a single-threaded executor to ensure that CSV writes are performed sequentially, preventing potential data corruption from concurrent writes.
     * For example, if multiple threads tried to write to the CSV file at the same time, it could lead to interleaved data and an invalid file format.
     * Furthermore, because this is a background thread, it won't block the main simulation thread, ensuring smooth performance and take less system resources.
     * </p>
     * 
     * <p>
     * We then set the running flag to true to indicate that the worker thread should continue running.
     * Then start the background worker thread by calling startWorker(), 
     * which submits a task to the executor that continuously polls the queue for new data and writes it to the CSV file.
     * </p>
     * 
     * 
     * @see <a href="https://stackoverflow.com/questions/16814569/examples-of-when-it-is-convenient-to-use-executors-newsinglethreadexecutor">
     *  StackOverflow – When is it convenient to use Executors.newSingleThreadExecutor()?
     *  </a>
     * 
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/LinkedBlockingQueue.html">
     *  Java LinkedBlockingQueue Documentation
     *  </a>
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
     * Method to start the background worker
     * 
     * <p>
     * First the startWorker method calls executor.submit() to submit a new task to the executor. 
     * This task is defined as a lambda expression that implements the Runnable interface.
     * </p>
     * 
     * <p>
     * Inside the taskm, a while loop checks the running flag and whether the queue is empty.
     * This ensures that the worker thread continues running as long as it is supposed to (running is true) or there is still data in the queue to process.
     * Meaning if running is set to false (indicating shutdown), the loop will continue processing any remaining data in the queue before exiting.
     * Or if the queue is empty but running is still true, it will keep waiting for new data to arrive.
     * If the queue is empty and running is false, the loop will exit, allowing the thread to terminate gracefully.
     * </p>
     * 
     * <p>
     * Within the loop, the worker thread apttempts to poll the queue for new data using queue.poll().
     * We choose poll() and not take() because we want to wait for new data, but only for a limited time (in this case, 500 milliseconds or 0.5 seconds).
     * If the poll times out becuase no new data is available within that time frame, it returns null (data = null).
     * The loop then continues to the next iteration, allowing it to check the running flag again.
     * </p>
     * 
     * <p>
     * If there is new data, the worker thread process it by calling csvManager.updateCSV() to write the data to the CSV file.
     * It also logs the number of rows exported for debugging purposes.
     * </p>
     * 
     * <p>
     * We put data.getVehicleData() as parameter to updateCSV().
     * GetVehicleData() is a method from ReportData that returns a List<String[]> representing the vehicle data to be exported.
     * 
     * 
     * </p>
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
     * <p>
     * Queue vehicle data for CSV export (non-blocking).
     * The data is from the simulation engine (called in the main window), then put into the queue for background export
     * ReportData object is created and added to the queue. 
     * </p>
     * 
     * <p>
     * If the queue is full, a warning is logged and the data is dropped.
     * If not we use offer() method to add data to the queue because it is non-blocking.
     * This means that if the queue is full, the method will return false immediately instead of blocking the calling thread until space becomes available.
     * </p>
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