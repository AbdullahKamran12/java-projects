public class CarParkMonitor extends Thread {
    // Array to store all car parks being monitored
    private carpark[] carParks;

    // Constructor to initialize the monitor with the list of car parks
    public CarParkMonitor(carpark[] carParks) {
        this.carParks = carParks;
    }

    // Thread execution method - monitors and reports car park status
    @Override
    public void run() {
        int reportCount = 0; // Counter to track the number of reports generated

        // Run the reporting loop 7 times (0 to 6), meaning it runs for 7 minutes to take into account any overflowing car entries from points
        while (reportCount <= 6) { 
            try {
                // Wait for 60 seconds before generating the next report
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace(); // Print error if thread sleep is interrupted
            }

            // Print the current simulated time in HH:MM format
            System.out.println("Time: " + formatTime(reportCount * 10) + "   ");

            // Iterate through all monitored car parks and print available spaces
            for (carpark cp : carParks) {
                System.out.println(cp.getDestination() + ": " + cp.getAvailableSpaces() + " Spaces");
            }

            // Increment report counter
            reportCount++;
        }
    }

    // Helper method to format time in HH:MM format based on total minutes
    private String formatTime(int minutes) {
        int hours = minutes / 60; // Convert minutes to hours
        int mins = minutes % 60; // Get remaining minutes
        return String.format("%02d:%02d", hours, mins); // Format as HH:MM
    }
}
