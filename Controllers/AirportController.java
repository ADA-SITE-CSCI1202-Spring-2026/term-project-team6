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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
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
                int requiredFuelCJ = (int)(Math.random() * 50001 + 6000);
                int requiredMealsCJ = (int)(Math.random() * 201 + 800);
                int requiredCartsCJ = (int)(Math.random() * 63 + 8);

                newAircraft = new CommercialJet(
                        flightNumber,
                        requiredFuelCJ,
                        requiredMealsCJ,
                        requiredCartsCJ,
                        requiredFuelCJ * 2 + requiredMealsCJ *2 +
                                requiredCartsCJ * 40 + (int)(Math.random() * 25001 + 1000),
                        (int)(Math.random() * 3 + 1)
                );
                break;

            case 1:
                // we searched the internet for the ranges
                // also cargo freighters mainly need fuel and cargo/luggage carts, not passenger meals
                int requiredFuelCF = (int)(Math.random() * 45001 + 6000);
                int requiredCartsCF = (int)(Math.random() * 61 + 20);

                newAircraft = new CargoFreighter(
                        flightNumber,
                        requiredFuelCF,
                        10, // for workers
                        requiredCartsCF,
                        requiredFuelCF * 2 + requiredCartsCF * 40 +
                                40 + (int)(Math.random() * 20501 + 1500),
                        (int)(Math.random() * 6 + 1)
                );
                break;

            default:
                // we searched the internet for the range,
                // also PrivateCharters do not need cargo/luggage carts
                int requiredFuelPC = (int)(Math.random() * 10001 + 1000);
                int requiredMealsPC = (int)(Math.random() * 11 + 10);

                newAircraft = new PrivateCharter(
                        flightNumber,
                        requiredFuelPC,
                        requiredMealsPC,
                        0,
                        requiredFuelPC * 2 + requiredMealsPC*2 +
                                (int)(Math.random() * 11051 + 250),
                        (int)(Math.random() * 1 + 1)
                );
                break;
        }

        queueManager.enqueue(newAircraft);
        logger.log("WARNING: New Arrival - " + newAircraft.getAircraftType()
                + " Flight " + flightNumber
                + " | Queue size: " + queueManager.size());
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
                logger.log(
                        crew.getClass().getSimpleName()
                                + " serviced Flight "
                                + aircraft.getFlightNumber()
                );
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
                amount = 75000;
                cost = 150000;
                break;

            case MEALS:
                amount = 700;
                cost = 1400;
                break;

            case CART:
                amount = 80;
                cost = 3200;
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

    // Save current airport state to a human-readable text file
    public boolean saveState(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

            // Save budget
            writer.println("BUDGET=" + depotManager.getBudget());

            // Save flight counter so flight numbers continue correctly after loading
            writer.println("FLIGHT_COUNTER=" + flightCounter);

            // Save resources
            for (SupplyItem item : SupplyItem.values()) {
                writer.println("RESOURCE," + item.name() + "," + depotManager.getResourceAmount(item));
            }

            // Save queue in correct order
            writer.println("QUEUE_START");

            List<Aircraft> aircraftList = queueManager.toList();

            for (Aircraft aircraft : aircraftList) {
                writer.println(
                        "AIRCRAFT,"
                                + aircraft.getClass().getSimpleName() + ","
                                + aircraft.getFlightNumber() + ","
                                + aircraft.getRequiredFuel() + ","
                                + aircraft.getRequiredMeals() + ","
                                + aircraft.getRequiredCarts() + ","
                                + aircraft.getRewardAmount() + ","
                                + aircraft.getTurnaroundTime()
                );
            }

            writer.println("QUEUE_END");

            logger.log("State saved to " + fileName);
            return true;

        } catch (IOException ex) {
            logger.log("ERROR: Could not save state - " + ex.getMessage());
            return false;
        }
    }

    // Load airport state from a human-readable text file
    public boolean loadState(String fileName) {
        HashMap<SupplyItem, Integer> loadedResources = new HashMap<>();
        List<Aircraft> loadedQueue = new ArrayList<>();

        int loadedBudget = depotManager.getBudget();
        int loadedFlightCounter = flightCounter;

        // Initialize all resources with 0 before loading exact values
        for (SupplyItem item : SupplyItem.values()) {
            loadedResources.put(item, 0);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("BUDGET=")) {
                    loadedBudget = Integer.parseInt(line.substring("BUDGET=".length()));
                }

                else if (line.startsWith("FLIGHT_COUNTER=")) {
                    loadedFlightCounter = Integer.parseInt(line.substring("FLIGHT_COUNTER=".length()));
                }

                else if (line.startsWith("RESOURCE,")) {
                    String[] parts = line.split(",");

                    if (parts.length != 3) {
                        throw new IllegalArgumentException("Invalid resource line: " + line);
                    }

                    SupplyItem item = SupplyItem.valueOf(parts[1]);
                    int amount = Integer.parseInt(parts[2]);

                    loadedResources.put(item, amount);
                }

                else if (line.startsWith("AIRCRAFT,")) {
                    Aircraft aircraft = parseAircraftFromSaveLine(line);
                    loadedQueue.add(aircraft);
                }
            }

            // Apply loaded budget and resources only after file reading succeeds
            depotManager.setBudget(loadedBudget);
            depotManager.setResources(loadedResources);

            // Restore queue in the same order
            queueManager.clear();

            for (Aircraft aircraft : loadedQueue) {
                queueManager.enqueue(aircraft);
            }

            // Restore flight counter
            flightCounter = loadedFlightCounter;

            logger.log("State loaded from " + fileName);
            return true;

        } catch (IOException | IllegalArgumentException ex) {
            logger.log("ERROR: Could not load state - " + ex.getMessage());
            return false;
        }
    }

    // Rebuild aircraft objects from saved task data
    private Aircraft parseAircraftFromSaveLine(String line) {
        String[] parts = line.split(",");

        if (parts.length != 8) {
            throw new IllegalArgumentException("Invalid aircraft line: " + line);
        }

        String aircraftType = parts[1];
        String flightNumber = parts[2];

        int requiredFuel = Integer.parseInt(parts[3]);
        int requiredMeals = Integer.parseInt(parts[4]);
        int requiredCarts = Integer.parseInt(parts[5]);
        int rewardAmount = Integer.parseInt(parts[6]);
        int turnaroundTime = Integer.parseInt(parts[7]);

        switch (aircraftType) {
            case "CommercialJet":
                return new CommercialJet(
                        flightNumber,
                        requiredFuel,
                        requiredMeals,
                        requiredCarts,
                        rewardAmount,
                        turnaroundTime
                );

            case "CargoFreighter":
                return new CargoFreighter(
                        flightNumber,
                        requiredFuel,
                        requiredMeals,
                        requiredCarts,
                        rewardAmount,
                        turnaroundTime
                );

            case "PrivateCharter":
                return new PrivateCharter(
                        flightNumber,
                        requiredFuel,
                        requiredMeals,
                        requiredCarts,
                        rewardAmount,
                        turnaroundTime
                );

            default:
                throw new IllegalArgumentException("Unknown aircraft type: " + aircraftType);
        }
    }

    // Getters for GUI
    public FlightQueueManager getQueueManager() { return queueManager; }
    public DepotManager getDepotManager() { return depotManager; }
    public SystemLogger getLogger() { return logger; }
}