public class carpark extends Thread {
    // The name of the destination this car park serves
    private String destination;           
    
    // Array to store parked vehicles, limited by the car park capacity
    private vehicle[] parkedVehicles;  
    
    // The total number of parking spaces in the car park
    private int capacity;                 
    
    // Counter for the number of vehicles currently parked
    private int count;                   
    
    // The road leading into this car park
    private roads incomingRoad; 

    // Total accumulated journey time for all parked vehicles
    private double totalJourneyTime = 0;

    // Constructor to initialize the car park with a destination, capacity, and the incoming road
    public carpark(String destination, int capacity, roads incomingRoad) {
        this.destination = destination;
        this.capacity = capacity;
        this.parkedVehicles = new vehicle[capacity]; // Array size is set to the car park capacity
        this.count = 0;  // Initially, no vehicles are parked
        this.incomingRoad = incomingRoad;
    }

    // Helper method to calculate the elapsed simulated time in minutes and seconds
    private String getSimulatedTime() {
        long elapsedMs = System.currentTimeMillis() - main.simulationStartTime;
        // Multiply elapsed milliseconds by 10 to scale simulation time (i.e., 6 minutes real = 60 minutes simulated)
        int simulatedSeconds = (int) ((elapsedMs * 10) / 1000);
        int minutes = simulatedSeconds / 60;
        int seconds = simulatedSeconds % 60;
        return String.format("%dm%02ds", minutes, seconds);
    }
    

    // Thread execution method - runs the car park operations
    @Override
    public void run() {
        // Runs while the simulation time is less than 6 minutes (360000 ms)
        while (System.currentTimeMillis() - main.simulationStartTime < 360000) {
            
            // If the car park is full, stop admitting vehicles and exit the loop
            if (count >= capacity) {
                break;
            }

            // If there is a vehicle waiting on the incoming road
            if (incomingRoad.hasCar()) {
                // Remove the vehicle from the queue
                vehicle v = incomingRoad.removeCar();

                // If the vehicle's destination matches this car park, admit it
                if (v != null && v.getDestination().equals(destination)) {
                    try {
                        // Simulate parking delay of 1.2 seconds (1200 ms)
                        Thread.sleep(1200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Calculate how long the vehicle has taken to reach the car park
                    double parkedTime = (System.currentTimeMillis() - main.simulationStartTime) / 1000.0;
                    v.setParkedTime(parkedTime);

                    // Update total journey time for averaging later
                    totalJourneyTime += v.getJourneyTime();

                    // Synchronizing to avoid race conditions when updating count and array
                    synchronized(this) {
                        parkedVehicles[count] = v; // Store the vehicle in the parked list
                        count++; // Increase parked vehicle count
                        
                        
                    }
                }
            }
        }
    }

    // Getter method to return the destination of this car park
    public String getDestination() {
        return destination;
    }

    // Method to calculate and return the number of available parking spaces
    public int getAvailableSpaces() {
        return capacity - count;
    }

    // Method to return the number of vehicles currently parked
    public int getCount(){
        return count;
    }

    // Method to compute and return the average journey time for parked vehicles
    public double getAverageJourneyTime() {
        return count > 0 ? totalJourneyTime / count : 0;
    }
}
