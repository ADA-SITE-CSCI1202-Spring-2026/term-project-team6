package services;

import aircrafts.Aircraft;

public class CateringVan implements IGroundService {
    
    @Override
    public boolean canService(Aircraft aircraft) {
        return aircraft.getRequiredMeals() > 0;
    }
    
    @Override
    public void serviceFlight(Aircraft aircraft) {
        System.out.println("services.CateringVan: Loading " + aircraft.getRequiredMeals()
                          + " in-flight meals onto " + aircraft.getFlightNumber());
    }
}