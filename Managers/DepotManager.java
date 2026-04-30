package Managers;

import aircrafts.Aircraft;

import java.util.HashMap;

public class DepotManager {
    private HashMap<SupplyItem, Integer> resources;
    private int budget;
    
    public DepotManager(int initialBudget) {
        this.budget = initialBudget;
        this.resources = new HashMap<>();
        
        // Initialize starting resources
        resources.put(SupplyItem.JET_FUEL, 500000);
        resources.put(SupplyItem.MEALS, 5000);
        resources.put(SupplyItem.CART, 200);
    }
    
    // Check if we have enough resources for an aircraft
    public boolean hasEnoughResources(Aircraft aircraft) {
        return resources.get(SupplyItem.JET_FUEL) >= aircraft.getRequiredFuel() &&
               resources.get(SupplyItem.MEALS) >= aircraft.getRequiredMeals() &&
               resources.get(SupplyItem.CART) >= aircraft.getRequiredCarts();
    }
    
    // Consume resources - MUST prevent negative values!
    public boolean consumeResources(Aircraft aircraft) {
        if (!hasEnoughResources(aircraft)) {
            return false;
        }
        
        resources.put(SupplyItem.JET_FUEL, 
                     resources.get(SupplyItem.JET_FUEL) - aircraft.getRequiredFuel());
        resources.put(SupplyItem.MEALS,
                     resources.get(SupplyItem.MEALS) - aircraft.getRequiredMeals());
        resources.put(SupplyItem.CART,
                     resources.get(SupplyItem.CART) - aircraft.getRequiredCarts());
        
        return true;
    }
    
    // Add reward money (budget revenue)
    public void addRevenue(int amount) {
        budget += amount;
    }
    
    // Purchase supplies
    public boolean purchaseSupply(SupplyItem item, int amount, int cost) {
        if (budget >= cost) {
            resources.put(item, resources.get(item) + amount);
            budget -= cost;
            return true;
        }
        return false;
    }
    
    // Getters - NO direct Map access (ENCAPSULATION!)
    public int getResourceAmount(SupplyItem item) {
        return resources.get(item);
    }
    
    public int getBudget() {
        return budget;
    }
    
    // For save/load functionality
    public HashMap<SupplyItem, Integer> getResourcesCopy() {
        return new HashMap<>(resources);
    }
    
    // For save/load functionality
    public void setResources(HashMap<SupplyItem, Integer> resources) {
        this.resources = new HashMap<>(resources);
    }
    
    public void setBudget(int budget) {
        this.budget = budget;
    }

}