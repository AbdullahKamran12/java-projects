import java.util.concurrent.atomic.AtomicInteger; // Importing AtomicInteger for thread-safe operations

// Class responsible for generating unique vehicle IDs in a thread-safe manner
public class vehicleidgenerator {
   private static final AtomicInteger idCounter = new AtomicInteger(0); // Atomic counter to ensure unique IDs

   // Method to generate the next unique vehicle ID
   public static int getNextId() {
       return idCounter.incrementAndGet(); // Increments and returns the new ID
   }

   // Method to get the current count of generated IDs
   public static int getCount() {
       return idCounter.get(); // Returns the current value of the ID counter
   }
}
