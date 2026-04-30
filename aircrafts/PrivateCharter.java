package aircrafts;

public class PrivateCharter extends Aircraft {

	public PrivateCharter(String flightNumber, int requiredFuel, int requiredMeals, int requiredCarts, int rewardAmount, int turnaroundTime) {
		super(flightNumber, requiredFuel, requiredMeals, requiredCarts, rewardAmount, turnaroundTime);
	}

	@Override
	public String getAircraftType() {
		return "Private Charter";
	}
	
}