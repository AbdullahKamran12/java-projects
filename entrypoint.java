import java.util.Random; // Importing the Random class for generating random numbers

// Class representing an entry point where vehicles enter the road network
public class entrypoint extends Thread {
    private String name;  // Name of the entry point (e.g., North, East, South)
    private roads road;   // The road connected to this entry point
    private Random rand = new Random(); // Random number generator for assigning destinations
    private int carsEnteredThisHour = 0; // Counter for cars entered within an hour
    private int carsPerHour; // Maximum number of cars that can enter per hour

    // Constructor to initialize an entry point with a name, connected road, and traffic flow rate
    public entrypoint(String name, roads road, int carsph) {
        this.name = name;
        this.road = road;
        this.carsPerHour = carsph;
    }
    
    // Method to randomly determine a vehicle's destination based on predefined probabilities
    private String getRandomDestination() {
        int randomValue = rand.nextInt(100); // Generate a random number between 0 and 99

        if (randomValue < 10) { 
            return "University";  // 10% chance to go to University
        } else if (randomValue < 30) {
            return "Station"; // 20% chance to go to the Station
        } else if (randomValue < 60) {
            return "Shopping Centre"; // 30% chance to go to the Shopping Centre
        } else {
            return "Industrial Park"; // 40% chance to go to the Industrial Park
        }
    }

    // Method to get the simulated time elapsed since the start of the simulation
    private String getSimulatedTime() {
        long elapsedMs = System.currentTimeMillis() - main.simulationStartTime;
        // Multiply elapsed milliseconds by 10 to scale simulation time (i.e., 6 minutes real = 60 minutes simulated)
        int simulatedSeconds = (int) ((elapsedMs * 10) / 1000);
        int minutes = simulatedSeconds / 60;
        int seconds = simulatedSeconds % 60;
        return String.format("%dm%02ds", minutes, seconds);
    }
    

    // Thread execution method - controls the entry of vehicles into the simulation
    @Override
    public void run() {
        long nextCarTime = System.currentTimeMillis();  // Timestamp for the next car arrival
        int interval = 360000 / carsPerHour; // Time interval between vehicle arrivals

        // Continue running while the simulation is within its total duration (360,000ms = 360 seconds = 6 minutes)
        while (System.currentTimeMillis() - main.simulationStartTime < 360000) {
            long currentTime = System.currentTimeMillis(); // Get current system time
            
            // Check if it's time to add a new vehicle to the road
            if (currentTime >= nextCarTime) { 
                synchronized (road) { // Synchronizing access to the road object

                    // Wait until there is space on the road or simulation time ends
                    while (!road.hasSpace() && System.currentTimeMillis() - main.simulationStartTime < 360000) {
                        if (System.currentTimeMillis() - main.simulationStartTime >= 360000) {
                            return; // Exit if simulation time is over
                        }
                        try {
                            road.wait(500); // Wait for 500ms before checking again
                        } catch (InterruptedException e) {
                            e.printStackTrace(); // Handle interruption exception
                        } 
                        if (System.currentTimeMillis() - main.simulationStartTime > 360000) {
                            return; // Exit if simulation time is over
                        }
                    }

                    // Retrieve the current simulated time
                    String entryTime = getSimulatedTime();
                    // Get a random destination for the new vehicle
                    String destination = getRandomDestination();

                    // Generate a unique ID for the vehicle
                    int vehicleId = vehicleidgenerator.getNextId();
                    // Create a new vehicle with the assigned destination and journey start time
                    vehicle newVehicle = new vehicle(destination, 
                                                     (currentTime - main.simulationStartTime) / 1000.0, 
                                                     vehicleId);

                    // Try to add the new vehicle to the road
                    if (road.addCar(newVehicle)) {
                        carsEnteredThisHour++; // Increment the count of cars entered
                    }

                    // Schedule the next vehicle arrival based on the interval
                    nextCarTime += interval;  
                }
            }
        }
    }
}
