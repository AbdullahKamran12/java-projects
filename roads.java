// Class representing a road that holds vehicles in a circular buffer queue
public class roads {
    private vehicle[] buffer; // Array to store vehicles
    private int capacity; // Maximum number of vehicles the road can hold
    private int front; // Index of the first vehicle in the queue
    private int rear; // Index where the next vehicle will be added
    private int size; // Current number of vehicles on the road
    private String destination; // Single destination associated with the road
    private String[] destinations; // Multiple possible destinations (for multi-destination roads)

    // Constructor for a road with a single destination
    public roads(int capacity, String destination) {
        this.capacity = capacity;
        this.buffer = new vehicle[capacity]; // Initialize the vehicle array
        this.front = 0; // Initialize front index
        this.rear = 0; // Initialize rear index
        this.size = 0; // Initialize size to 0 (empty road)
        this.destination = destination; // Assign the destination
    }

    // Constructor for a road that supports multiple destinations
    public roads(int capacity, String[] destinations) {
        this.capacity = capacity;
        this.buffer = new vehicle[capacity]; // Initialize the vehicle array
        this.front = 0; // Initialize front index
        this.rear = 0; // Initialize rear index
        this.size = 0; // Initialize size to 0 (empty road)
        this.destinations = destinations; // Assign multiple destinations
    }

    // Checks if the road has available space for more vehicles
    public synchronized boolean hasSpace() {
        return size < capacity;
    }

    // Checks if there are any vehicles currently on the road
    public synchronized boolean hasCar() {
        return size > 0;
    }

    // Adds a vehicle to the road queue if there is space
    public synchronized boolean addCar(vehicle v) {
        if (hasSpace()) {
            buffer[rear] = v; // Place the vehicle at the rear index
            v.setPosition(rear); // Update the vehicle's position
            rear = (rear + 1) % capacity; // Move rear index circularly
            size++; // Increase vehicle count
            return true; // Indicate success
        }
        return false; // Indicate failure (road full)
    }

    // Removes and returns the vehicle at the front of the queue
    public synchronized vehicle removeCar() {
        if (!hasCar()) return null; // Return null if no vehicle is available
        vehicle v = buffer[front]; // Get the vehicle at the front
        buffer[front] = null; // Clear the slot
        front = (front + 1) % capacity; // Move front index circularly
        size--; // Decrease vehicle count
        notifyAll(); // Notify waiting threads that space is available
        return v; // Return the removed vehicle
    }

    // Returns the current number of vehicles on the road
    public int getSize() {
        return size;
    }

    // Returns the primary destination of the road
    public String getDestination() {
        return destination;
    }

    // Returns the list of possible destinations (for multi-destination roads)
    public String[] getDestinations() {
        return destinations;
    }

    // Returns the vehicle at the front of the queue without removing it
    public synchronized vehicle peekCar() {
        return size > 0 ? buffer[front] : null; // Return the front vehicle if available, otherwise null
    }
}
