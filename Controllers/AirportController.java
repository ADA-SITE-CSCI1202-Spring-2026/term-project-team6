package Controllers;

import Managers.DepotManager;
import Managers.FlightQueueManager;
import Managers.SupplyItem;
import Managers.SystemLogger;
import aircrafts.Aircraft;
import aircrafts.CargoFreighter;
import aircrafts.CommercialJet;
import aircrafts.PrivateCharter;
import services.CateringVan;
import services.FuelingTruck;
import services.IGroundService;
import services.BaggageHandler;

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
        this.depotManager = new DepotManager(500000); // Starting budget $500,000
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
                // we searched the internet for the ranges
                newAircraft = new CommercialJet(flightNumber, (int)(Math.random()*74001+6000),
                        (int)(Math.random()*201+800), (int)(Math.random()*63+8),
                        (int)(Math.random()*19001+1000), (int)(Math.random()*3+1));
                break;
            case 1:
                // we searched the internet for the ranges
                newAircraft = new CargoFreighter(flightNumber, (int)(Math.random()*69001+6000),
                        (int)(Math.random()*5+8), 0,
                        (int)(Math.random()*13501+1500), (int)(Math.random()*6+1));
                break;
            default:
                // we searched the internet for the range
                newAircraft = new PrivateCharter(flightNumber, (int)(Math.random()*17001+1000),
                        (int)(Math.random()*11+10), 0,
                        (int)(Math.random()*7751+250), (int)(Math.random()*1+1));
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
                amount = 10000;
                cost = 20000;
                break;
            case MEALS:
                amount = 200;
                cost = 400;
                break;
            case CART:
                amount = 10;
                cost = 500;
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