public class PrivateCharter extends Aircraft {

	public PrivateCharter(String flightNumber, int requiredFuel, int requiredMeals, int requiredCarts, int rewardAmount) {
		super(flightNumber, requiredFuel, requiredMeals, requiredCarts, rewardAmount);
	}

	@Override
	public String getAircraftType() {
		return "Private Charter";
	}
	
}