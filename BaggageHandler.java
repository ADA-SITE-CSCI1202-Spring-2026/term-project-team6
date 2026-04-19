public class BaggageHandler implements IGroundService {
    
    @Override
    public boolean canService(Aircraft aircraft) {
        return aircraft.getRequiredCarts() > 0;
    }
    
    @Override
    public void serviceFlight(Aircraft aircraft) {
        System.out.println("BaggageHandler: Loading " + aircraft.getRequiredCarts() 
                          + " luggage carts for " + aircraft.getFlightNumber());
    }
}