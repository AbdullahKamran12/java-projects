import java.io.FileWriter; // Importing FileWriter for logging events to a file
import java.io.IOException; // Importing IOException to handle file writing exceptions

// Class representing a junction that controls the flow of vehicles from entry roads to exit roads
public class junction extends Thread {
    private String name;                          // Name of the junction
    private roads[] entryRoads;                   // Array of roads leading into the junction
    private roads[] exitRoads;                    // Array of roads leading out of the junction
    private int greenTime;                        // Duration (in seconds) for which an entry road has a green signal
    private int currentEntry;                     // Index tracking the currently active entry road
    private FileWriter logWriter;                 // File writer for logging junction activity

    // Constructor to initialize a junction with its name, entry roads, exit roads, and green light duration
    public junction(String name, roads[] entryRoads, roads[] exitRoads, int greenTimes) {
        this.name = name;
        this.entryRoads = entryRoads;
        this.exitRoads = exitRoads;
        this.greenTime = greenTimes;
        this.currentEntry = 0;  // Start with the first entry road

        try {
            logWriter = new FileWriter(name + "_log.txt", true); // Open log file for writing (append mode)
        } catch (IOException e) {
            e.printStackTrace(); // Handle exceptions if file cannot be opened
        }
    }

    // Method to calculate and return the simulated time since the start of the simulation
    private String getSimulatedTime() {
        long elapsedMs = System.currentTimeMillis() - main.simulationStartTime;
        // Multiply elapsed milliseconds by 10 to scale simulation time (i.e., 6 minutes real = 60 minutes simulated)
        int simulatedSeconds = (int) ((elapsedMs * 10) / 1000);
        int minutes = simulatedSeconds / 60;
        int seconds = simulatedSeconds % 60;
        return String.format("%dm%02ds", minutes, seconds);
    }
    
    
    // Thread execution method - manages traffic flow through the junction
    @Override
    public void run() {
        // Continue running while the simulation time is within the limit (360,000ms = 360 seconds = 6 minutes)
        while (System.currentTimeMillis() - main.simulationStartTime < 360000) {
            
            roads currentEntryRoad = entryRoads[currentEntry]; // Get the currently active entry road
            int carsPassed = 0; // Counter for the number of cars passing through
            int maxCars = (int) ((12.0 / 60) * greenTime); // Calculate max number of cars allowed per green light
            int carsWaiting = currentEntryRoad.getSize(); // Get the number of waiting cars in the current entry road
            boolean gridlocked = false; // Flag to check if the junction is gridlocked

            // Process vehicles until the maxCars limit is reached or no more cars are available
            while (carsPassed < maxCars && currentEntryRoad.hasCar()) {
                vehicle v = currentEntryRoad.peekCar(); // Peek at the next vehicle in line
                boolean redirected = false; // Flag to track if the vehicle is successfully redirected
                
                // Try to find an exit road that matches the vehicle's destination
                for (int i = 0; i < exitRoads.length; i++) {
                    if (exitRoads[i].getDestination() != null &&
                        v.getDestination().equals(exitRoads[i].getDestination())) { // Direct match found
                        if (exitRoads[i].hasSpace()) { // Check if the exit road has space
                            synchronized(currentEntryRoad) {
                                currentEntryRoad.removeCar(); // Remove the car from the entry road
                            }
                            synchronized(exitRoads[i]) {
                                exitRoads[i].addCar(v); // Add the car to the exit road
                            }
                            redirected = true;
                            break;
                        }
                    } else if (exitRoads[i].getDestinations() != null) { // Check if multiple destinations are allowed
                        for (String dest : exitRoads[i].getDestinations()) {
                            if (v.getDestination().equals(dest)) {
                                if (exitRoads[i].hasSpace()) { // Check for available space
                                    synchronized(currentEntryRoad) {
                                        currentEntryRoad.removeCar(); // Remove car from the entry road
                                    }
                                    synchronized(exitRoads[i]) {
                                        exitRoads[i].addCar(v); // Add car to the exit road
                                    }
                                    redirected = true;
                                    break;
                                }
                            }
                        }
                        if (redirected) break; // Exit loop if redirection was successful
                    }
                }
                
                if (!redirected) { // If vehicle couldn't be redirected, it remains in place
                    break;
                }
                carsPassed++; // Increment the count of successfully redirected cars
            }
            
            // Check if there were cars waiting but none could move, indicating potential gridlock
            if (carsWaiting > 0 && carsPassed == 0) {
                gridlocked = true; // Set gridlock flag
            }

            // Prepare log message containing simulation time, junction name, and traffic flow details
            String logMessage = "Time: " + getSimulatedTime() + " - Junction " + name + ": ";
            if (currentEntryRoad != null) {
                logMessage += carsPassed + " cars through from " + currentEntryRoad.getDestination();
            } else if (currentEntryRoad == null) {
                logMessage += carsPassed + " cars through from multiple destination road";
            }
            
            logMessage += ", " + carsWaiting + " cars waiting.";
            if (gridlocked) {
                logMessage += " GRIDLOCK"; // Append gridlock warning if applicable
            }
            logMessage += "\n"; // Add a newline for log formatting
            
            // Write the log message to the file
            try {
                synchronized(logWriter) { // Ensure thread safety while writing to the file
                    logWriter.write(logMessage);
                    logWriter.flush(); // Ensure data is written immediately
                }
            } catch (IOException e) {
                e.printStackTrace(); // Handle file writing errors
            }

            // Pause the thread for a duration based on the green light time before switching roads
            try {
                Thread.sleep(greenTime * 100); // Convert greenTime to milliseconds
            } catch (InterruptedException e) {
                e.printStackTrace(); // Handle thread interruption exceptions
            }

            // Move to the next entry road in a circular manner
            currentEntry = (currentEntry + 1) % entryRoads.length;
        }
    }
}
