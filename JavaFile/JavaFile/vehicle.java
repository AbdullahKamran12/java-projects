// Class representing a vehicle that moves through the road system
public class vehicle {
    private String destination; // Destination where the vehicle is headed
    private double entryTime; // Time when the vehicle entered the system
    private double parkedTime; // Time when the vehicle was parked (initialized as -1 if not parked)
    private int position; // Position of the vehicle in a queue (road or parking area)
    private int id; // Unique identifier for the vehicle

    // Constructor to initialize a vehicle with its destination, entry time, and ID
    public vehicle(String destination, double entryTime, int id) {
        this.destination = destination; // Assign destination
        this.entryTime = entryTime; // Assign entry time
        this.parkedTime = -1; // Default parked time (not yet parked)
        this.position = 0; // Default position
        this.id = id; // Assign unique vehicle ID
    }

    // Returns the position of the vehicle in a queue
    public int getPosition() {
        return position;
    }

    // Sets the position of the vehicle in a queue
    public void setPosition(int position) {
        this.position = position;
    }

    // Returns the destination of the vehicle
    public String getDestination() {
        return destination;
    }

    // Returns the time when the vehicle entered the system
    public double getEntryTime() {
        return entryTime;
    }

    // Returns the journey time (equal to parked time once the vehicle reaches its destination)
    public double getJourneyTime() {
        return parkedTime;
    }

    // Sets the parked time of the vehicle (when it reaches its destination)
    public void setParkedTime(double parkedTime) {
        this.parkedTime = parkedTime;
    }

    // Returns the parked time of the vehicle
    public double getParkedTime() {
        return parkedTime;
    }

    // Returns the unique ID of the vehicle
    public int getId() {
        return id;
    }
}
