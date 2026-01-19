package real_time_traffic_simulation_with_java.gui;

// Import JavaFX components for building GUI
import javafx.animation.AnimationTimer;  // For continuous animation loop (like a game loop)
import javafx.application.Application;   // Base class for JavaFX applications
import javafx.scene.Scene;               // Window content (what users see)
import javafx.scene.control.ScrollPane;  // Scrollable container for panels
import javafx.scene.layout.BorderPane;   // Layout manager (divides window into 5 regions)
import javafx.scene.paint.Color;         // For setting colors
import javafx.stage.Stage;               // Window frame (the actual window)
import real_time_traffic_simulation_with_java.cores.SimulationEngine;

/**
 * MainWindow - Main application window
 * This is the entry point of the GUI application
 * Layout: Left (ControlPanel), Center (MapPanel), Right (Dashboard)
 */
public class MainWindow extends Application {
    
    // Store reference to the backend simulation engine
    // This engine connects to SUMO and manages all simulation logic
    private SimulationEngine simulationEngine;
    
    /**
     * start() method is called automatically by JavaFX when application launches
     * This is like the "main" method for GUI applications
     * @param stage - The main window provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            // Step 1: Create and initialize the backend engine
            // SimulationEngine connects to SUMO and starts the simulation
            simulationEngine = new SimulationEngine();
            
            // Step 2: Build the user interface (GUI)
            // This will create all panels and connect them together
            initializeGui(stage);
            
        } catch (Exception e) {
            // If something goes wrong, print error details
            e.printStackTrace();
        }
    }
    
    /**
     * Build the complete GUI with 3 panels
     * This method does all the heavy lifting to create the interface
     */
    private void initializeGui(Stage stage) throws Exception {
        
        // ========== STEP 1: CREATE THE 3 MAIN PANELS ==========
        // Think of these as 3 separate screens that will be combined
        
        ControlPanel leftPanel = new ControlPanel();  // Left: control buttons, vehicle injection, etc.
        MapPanel centerPanel = new MapPanel(simulationEngine); // Center: the map showing roads and vehicles
        Dashboard rightPanel = new Dashboard();       // Right: statistics and export options
        
        
        // ========== STEP 2: CONNECT PANELS TO BACKEND ENGINE ==========
        // Give panels access to SimulationEngine so they can:
        // - Get data (vehicle positions, traffic lights, etc.)
        // - Send commands (inject vehicles, toggle lights, etc.)
        
        leftPanel.setSimulationEngine(simulationEngine);    // ControlPanel needs engine to control simulation
        rightPanel.setSimulationEngine(simulationEngine);   // Dashboard needs engine to fetch real-time statistics
        
        
        // ========== STEP 3: WRAP PANELS IN SCROLLPANES ==========
        // ScrollPane allows vertical scrolling when content is too tall
        
        // Wrap left panel
        ScrollPane leftScroll = new ScrollPane(leftPanel);
        leftScroll.setFitToWidth(true);  // Make panel stretch to fit scrollpane width
        leftScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);  // Hide horizontal scrollbar
        leftScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);  // Show vertical scrollbar only when needed
        
        // Wrap right panel (same settings)
        ScrollPane rightScroll = new ScrollPane(rightPanel);
        leftScroll.setFitToWidth(true);
        rightScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        
        // ========== STEP 4: ARRANGE PANELS IN LAYOUT ==========
        // BorderPane divides window into 5 regions: TOP, LEFT, CENTER, RIGHT, BOTTOM
        // We only use LEFT, CENTER, RIGHT
        
        BorderPane root = new BorderPane();  // Create the layout manager
        root.setLeft(leftScroll);            // Put ControlPanel on left side
        root.setCenter(centerPanel);         // Put MapPanel in center (takes most space)
        root.setRight(rightScroll);          // Put Dashboard on right side
        root.setStyle("-fx-background-color: #F5F5F7;");  // Set light gray background color
        
        
        // ========== STEP 5: CREATE THE SCENE (WINDOW CONTENT) ==========
        // Scene = Everything inside the window
        // Think: Stage is the window frame, Scene is what's inside
        
        Scene scene = new Scene(root, 1200, 700);  // Create scene with root layout, 1200px wide, 700px tall
        scene.setFill(Color.web("#F5F5F7"));       // Set scene background color (same as root)
        
        
        // ========== STEP 6: CONFIGURE THE WINDOW (STAGE) ==========
        // Set window title, add scene to window, set size limits
        
        stage.setTitle("Real Time SUMO Traffic Simulation");  // Window title shown in title bar
        stage.setScene(scene);      // Put our scene inside the window
        stage.setMinWidth(1000);    // User can't make window narrower than 1000px
        stage.setMinHeight(600);    // User can't make window shorter than 600px
        
        
        // ========== STEP 7: HANDLE WINDOW CLOSE EVENT ==========
        
        stage.setOnCloseRequest(event -> {  // Lambda function (runs when window closes)
            try {
                // Check if engine exists before trying to stop it
                if (simulationEngine != null) {
                    simulationEngine.stopSimulation();  // Tell SUMO to stop and disconnect
                }
            } catch (Exception e) {
                // If error during shutdown, print it (but still continue to exit)
                System.err.println("Error closing simulation: " + e.getMessage());
            }
            System.exit(0);  // Exit the program completely
        });
        
        
        // Show the window! Everything is ready, make it visible to user
        stage.show();
        
        
        // ========== STEP 8: START ANIMATION LOOP (THE HEART OF THE APP) ==========
        // AnimationTimer is like a heartbeat that runs continuously
        // It updates the simulation and refreshes the screen many times per second
        // Think of it like a video game loop: update → draw → update → draw → ...
        
        AnimationTimer timer = new AnimationTimer() {
            
            // Track time of last update (in nanoseconds)
            // We use this to control update frequency
            private long lastUpdate = 0;
            
            // Update every 200 milliseconds (5 times per second)
            // 1 second = 1,000,000,000 nanoseconds
            // So 200ms = 200,000,000 nanoseconds
            private static final long UPDATE_INTERVAL = 200_000_000;
            
            // Remember previous state to detect changes
            private boolean wasRunning = false;
            
            /**
             * handle() is called automatically ~60 times per second by JavaFX
             * But we only want to update every 200ms, so we check time
             */
            @Override
            public void handle(long now) {
                
                // ===== CHECK IF ENOUGH TIME HAS PASSED =====
                // "now" is current time in nanoseconds
                // If less than 200ms since last update, skip this frame
                if (now - lastUpdate < UPDATE_INTERVAL) {
                    return;  // Exit early, don't update yet
                }
                
                // Enough time passed, update lastUpdate time
                lastUpdate = now;
                
                
                // ===== CHECK IF SIMULATION IS RUNNING =====
                // Ask ControlPanel if user pressed Start or Stop
                boolean isRunning = leftPanel.isSimulationRunning();
                
                // Track state changes (optional)
                // This helps us know when simulation starts/stops
                if (isRunning != wasRunning) {
                    wasRunning = isRunning;  // Update previous state
                }
                
                
                // ===== UPDATE SIMULATION AND DISPLAY =====
                try {
                    
                    // If simulation is running, step it forward
                    if (isRunning) {
                        // Tell SUMO to advance 1 step (simulate traffic for next time step)
                        simulationEngine.stepSimulation();
                        
                        // Update time display in ControlPanel
                        // Show current simulation time in seconds
                        leftPanel.updateTime((int) simulationEngine.getCurrentTime());
                    }
                    
                    // Always update map display (even when paused)
                    // This ensures map shows current state
                    // Users can still pan/zoom when paused
                    centerPanel.refresh();
                    // Update Dashboard statistics with real-time data
                    rightPanel.updateStatistics();
                    
                } catch (Exception e) {
                    // If any error occurs during update, print it
                    // But don't crash - keep running
                    System.err.println("Error in animation loop: " + e.getMessage());
                }
            }
        };
        
        // Start the timer! Animation loop begins running
        // From now on, handle() will be called continuously
        timer.start();
    }
    

    public static void main(String[] args) {
        launch(args);  // Start JavaFX application
    }
}
