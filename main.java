public class main {
    // Stores the start time of the simulation
    public static final long simulationStartTime = System.currentTimeMillis();

    public static void main(String[] args) {
        System.out.println("Scenario 1\n");
        // Creating roads that lead to different destinations with a specified capacity
        roads JuncDtoUni = new roads(15, "University");
        roads JuncDtoStation = new roads(15, "Station");
        roads JuncCtoShoping = new roads(7, "Shopping Centre");
        roads JuncAtoIndustrial = new roads(15, "Industrial Park");

        // Defining valid destinations for different junction routes
        String[] allDest = {"University", "Station", "Shopping Centre", "Industrial Park"};
        String[] destAtoB = {"University", "Station", "Shopping Centre"};
        String[] destCtoD = {"University", "Station"};

        // Creating major roads leading to junctions
        roads northToJuncC = new roads(50, allDest);
        roads eastToJuncB = new roads(30, allDest);
        roads southToJuncA = new roads(60, allDest);

        // Defining inter-junction roads
        roads juncAtoB = new roads(7, destAtoB);
        roads juncBtoA = new roads(7, "Industrial Park");

        roads juncBtoC = new roads(10, destAtoB);
        roads juncCtoB = new roads(10, "Industrial Park");

        roads juncCtoD = new roads(10, destCtoD);

        // Defining road connections for each junction
        roads[] juncAEntry = {southToJuncA, juncBtoA};
        roads[] juncAExit = {JuncAtoIndustrial, juncAtoB};

        roads[] juncBEntry = {eastToJuncB, juncAtoB, juncCtoB};
        roads[] juncBExit = {juncBtoC, juncBtoA};

        roads[] juncCEntry = {northToJuncC, juncBtoC};
        roads[] juncCExit = {juncCtoB, juncCtoD, JuncCtoShoping};

        roads[] juncDEntry = {juncCtoD};
        roads[] juncDExit = {JuncDtoStation, JuncDtoUni};

        // Collecting all roads for later reference
        roads[] allRoads = {
            JuncDtoUni,
            JuncDtoStation,
            JuncCtoShoping,
            JuncAtoIndustrial,
            northToJuncC,
            eastToJuncB,
            southToJuncA,
            juncAtoB,
            juncBtoA,
            juncBtoC,
            juncCtoB,
            juncCtoD
        };

        // Creating junctions with entry and exit roads, and setting their processing speed
        junction juncA = new junction("A", juncAEntry, juncAExit, 60);
        junction juncB = new junction("B", juncBEntry, juncBExit, 60);
        junction juncC = new junction("C", juncCEntry, juncCExit, 30);
        junction juncD = new junction("D", juncDEntry, juncDExit, 30);

        // Creating entry points where cars enter the simulation
        entrypoint North = new entrypoint("North", northToJuncC, 550);
        entrypoint East = new entrypoint("East", eastToJuncB, 300);
        entrypoint South = new entrypoint("South", southToJuncA, 550);

        // Creating car parks for different destinations
        carpark universityCarPark = new carpark("University", 100, JuncDtoUni);  
        carpark stationCarPark = new carpark("Station", 150, JuncDtoStation);       
        carpark shoppingCentreCarPark = new carpark("Shopping Centre", 400, JuncCtoShoping); 
        carpark industrialParkCarPark = new carpark("Industrial Park", 1000, JuncAtoIndustrial); 

        // Grouping car parks, entry points, and junctions for easier management
        carpark[] carParks = {universityCarPark, stationCarPark, shoppingCentreCarPark, industrialParkCarPark};
        entrypoint[] entrypoints = {North, South, East};
        junction[] junctions = {juncA, juncB, juncC, juncD};
        
        // Starting all car parks as threads
        for (carpark cp : carParks) {
            cp.start();
        }

        // Starting all entry points as threads
        for (entrypoint ep : entrypoints){
            ep.start();
        }

        // Starting all junctions as threads
        for (junction j : junctions){
            j.start();
        }

        // Initializing and starting the car park monitor
        CarParkMonitor monitor = new CarParkMonitor(carParks);
        monitor.start();

        // Allowing the simulation to run for a set duration
        try {
            Thread.sleep(380000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Calculating the number of vehicles still in the queue on roads
        int totalQueued = 0;
        for (roads r : allRoads) {
            totalQueued += r.getSize();
        }

        // Calculating the number of vehicles successfully parked
        int totalParked = 0;
        for (carpark cp : carParks) {
            totalParked += cp.getCount();
        }

        // Retrieving the total number of vehicles created
        int totalCreated = vehicleidgenerator.getCount();

        // Displaying simulation results
        System.out.println("Cars parked in University: " + universityCarPark.getCount() + 
                           " with average journey time: " + universityCarPark.getAverageJourneyTime());
        System.out.println("Cars parked in Station: " + stationCarPark.getCount() + 
                           " with average journey time: " + stationCarPark.getAverageJourneyTime());
        System.out.println("Cars parked in Shopping Centre: " + shoppingCentreCarPark.getCount() + 
                           " with average journey time: " + shoppingCentreCarPark.getAvailableSpaces());
        System.out.println("Cars parked in Industrial Park: " + industrialParkCarPark.getCount() + 
                           " with average journey time: " + industrialParkCarPark.getAvailableSpaces());
        System.out.println("Total cars created: " + totalCreated);
        System.out.println("Total cars parked: " + totalParked);
        System.out.println("Total cars queued on roads: " + totalQueued);

        // Checking data integrity to ensure no vehicles are lost
        if (totalCreated == (totalParked + totalQueued)) {
            System.out.println("No data lost: all vehicles accounted for.");
        } else {
            System.out.println("Data mismatch: some vehicles are missing.");
        }
    }
}
