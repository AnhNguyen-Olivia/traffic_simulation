package real_time_traffic_simulation_with_java.tools;

import real_time_traffic_simulation_with_java.alias.Path;
import real_time_traffic_simulation_with_java.alias.Metrics;

import com.opencsv.CSVWriter;

import java.util.List;
import java.util.logging.Level;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * Manages CSV log files for the traffic simulation.
 * It creates a new CSV file with a timestamped name upon instantiation (format: YYYY-MM-DD HH_MM_SS).
 */
public class CSVManager {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(CSVManager.class.getName());

    private String timeStamp;
    private String filePath;
    private CSVWriter writer;
    private static final String[] headers = Metrics.HEADERS;

    /**
     * Manages CSV log files for the traffic simulation.
     * It creates a new CSV file with a timestamped name upon instantiation (format: YYYY-MM-DD HH_MM_SS).
     */
    public CSVManager() {
        this.timeStamp = LocalDateTime.now().toString().substring(0, 19).replace("T", " ");
        this.timeStamp = this.timeStamp.replaceAll(":", "_");
        this.filePath = Path.CsvLogFolder + this.timeStamp + ".csv";
        try {
            // Make sure the directory exists
            Files.createDirectories(Paths.get(Path.CsvLogFolder));
            this.writer = new CSVWriter(new FileWriter(this.filePath, true), CSVWriter.DEFAULT_SEPARATOR,
                                        CSVWriter.NO_QUOTE_CHARACTER,
                                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                                        CSVWriter.DEFAULT_LINE_END);
            this.writer.writeNext(headers);
            this.writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to write headers to CSV file: " + e.getMessage());
        }
    }

    /** Getter to retrieve the file path of the CSV log */
    public String getFilePath() {
        return this.filePath;
    }
    /** Getter to retrieve the timestamp used in the CSV file name */
    public String getTimeStamp() {
        return this.timeStamp;
    }

    /** 
     * Appends new rows of data to the CSV file.
     * @param data A list of string arrays representing the data rows to be added.
     * @exception IllegalArgumentException if the length of any data row does not match the number of headers.
     */
    public void updateCSV(List<String[]> data) {
        for (String[] row : data) {
            if (row.length != headers.length) {
                throw new IllegalArgumentException("Data length does not match headers length.");
            }
        }
        try {
            this.writer.writeAll(data);
            this.writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to write data to CSV file: " + e.getMessage(), e);
        }
    }

    /** Closes the CSV writer to release file resources. Currently Beta testing*/
    public void closeCSV() {
        try {
            if (writer != null) {
                writer.close();
                LOGGER.log(Level.INFO, "CSV file closed successfully.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close CSV file: " + e.getMessage(), e);
        }
    }
}
