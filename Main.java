
public class Main {
    public static void main(String[] args) {
        AirportController controller = new AirportController();
        controller.generateRandomFlight();
        controller.generateRandomFlight();
        controller.generateRandomFlight();

        // Simulate servicing the first flight in the queue
        controller.processNextFlight();
        // Simulate purchasing supplies
        controller.purchaseSupply(SupplyItem.JET_FUEL);

        // Simulate servicing the next flight in the queue
        controller.processNextFlight();
        // Simulate purchasing supplies
        controller.purchaseSupply(SupplyItem.MEALS);

        System.out.println(controller.getQueueManager() + " flights in queue.");
        System.out.println("Simulation complete. Check logs for details.");
    }
}