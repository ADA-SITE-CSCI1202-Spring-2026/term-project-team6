package services;

import aircrafts.Aircraft;

public class FuelingTruck implements IGroundService {
    
    @Override
    public boolean canService(Aircraft aircraft) {
        return true;
    }
    
    @Override
    public void serviceFlight(Aircraft aircraft) {
        System.out.println("services.FuelingTruck: Refueling " + aircraft.getFlightNumber()
                          + " with " + aircraft.getRequiredFuel() + "L of jet fuel");
    }
}