import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AirportController {
    private FlightQueueManager queueManager;
    private DepotManager depotManager;
    private SystemLogger logger;
    private List<IGroundService> groundCrews;
    private Random random;
    private int flightCounter;
    
    public AirportController() {
        this.queueManager = new FlightQueueManager();
        this.depotManager = new DepotManager(50000); // Starting budget $50,000
        this.logger = new SystemLogger();
        this.random = new Random();
        this.flightCounter = 100;
        
        // Initialize ground crews (polymorphic list!)
        this.groundCrews = new ArrayList<>();
        groundCrews.add(new FuelingTruck());
        groundCrews.add(new CateringVan());
        groundCrews.add(new BaggageHandler());
        
        logger.log("Airport Operations System Initialized");
    }
    
    // Generate random flight (called by timer)
    public void generateRandomFlight() {
        String flightNumber = "SK" + flightCounter++;

        Aircraft newAircraft;
        
        int type = random.nextInt(3);
        // flightNumber, requiredFuel, requiredMeals, requiredCarts, rewardAmount, turnaroundTime
        switch (type) {
            case 0:
                newAircraft = new CommercialJet(flightNumber, 1, 2, 3, 4, 5); // Placeholder values, can be randomized as needed
                break;
            case 1:
                newAircraft = new CargoFreighter(flightNumber, 5, 4, 3, 2, 1); // Placeholder values, can be randomized as needed
                break;
            default:
                newAircraft = new PrivateCharter(flightNumber, 3, 3, 3, 3, 3); // Placeholder values, can be randomized as needed
                break;
        }
        
        queueManager.enqueue(newAircraft);
        logger.log("WARNING: New Arrival - " + newAircraft.getAircraftType() 
                  + " Flight " + flightNumber);
    }
    
    // Process next flight (called when "Clear Next Flight" button clicked)
    public boolean processNextFlight() {
        Aircraft aircraft = queueManager.peekNextFlight();
        
        if (aircraft == null) {
            logger.log("ERROR: No flights in queue");
            return false;
        }
        
        // Check resources BEFORE processing
        if (!depotManager.hasEnoughResources(aircraft)) {
            logger.log("ERROR: Cannot clear Flight " + aircraft.getFlightNumber() 
                      + " - Insufficient supplies!");
            return false;
        }
        
        // Consume resources
        if (!depotManager.consumeResources(aircraft)) {
            logger.log("ERROR: Resource consumption failed for " + aircraft.getFlightNumber());
            return false;
        }
        
        // Process with ground crews (POLYMORPHISM!)
        for (IGroundService crew : groundCrews) {
            // Each crew checks if they can service the flight, and if so, they do it.
            if (crew.canService(aircraft)) {
                crew.serviceFlight(aircraft);
            }
        }
        
        // Add revenue
        depotManager.addRevenue(aircraft.getRewardAmount());
        
        // Remove from queue
        queueManager.dequeue();

        // Log success
        logger.log("SUCCESS: Flight " + aircraft.getFlightNumber() 
                  + " cleared. Revenue: $" + aircraft.getRewardAmount());
        
        return true;
    }
    
    // Purchase supplies
    public boolean purchaseSupply(SupplyItem item) {
        int amount, cost;
        
        // Define purchase amounts and costs for each item
        switch (item) {
            case JET_FUEL:
                amount = 1000;
                cost = 500;
                break;
            case MEALS:
                amount = 200;
                cost = 300;
                break;
            case CART:
                amount = 5;
                cost = 100;
                break;
            default:
                return false;
        }
        
        // Attempt purchase
        if (depotManager.purchaseSupply(item, amount, cost)) {
            // Log purchase
            logger.log("Purchased " + amount + " units of " + item + " for $" + cost);
            return true;
        } else {
            // Log insufficient budget
            logger.log("ERROR: Insufficient budget to purchase " + item);
            return false;
        }
    }
    
    // Getters for GUI
    public FlightQueueManager getQueueManager() { return queueManager; }
    public DepotManager getDepotManager() { return depotManager; }
    public SystemLogger getLogger() { return logger; }
}