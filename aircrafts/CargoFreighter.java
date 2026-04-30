package aircrafts;

public class CargoFreighter extends Aircraft {

	public CargoFreighter(String flightNumber, int requiredFuel, int requiredMeals, int requiredCarts, int rewardAmount, int turnaroundTime) {
		super(flightNumber, requiredFuel, requiredMeals, requiredCarts, rewardAmount, turnaroundTime);
	}

	@Override
	public String getAircraftType() {
		return "Cargo Freighter";
	}
	
}