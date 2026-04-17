public class CargoFreighter extends Aircraft {

	public CargoFreighter(String flightNumber, int requiredFuel, int requiredMeals, int requiredCarts, int rewardAmount) {
		super(flightNumber, requiredFuel, requiredMeals, requiredCarts, rewardAmount);
	}

	@Override
	public String getAircraftType() {
		return "Cargo Freighter";
	}
	
}