package aircrafts;

public class CommercialJet extends Aircraft {

	public CommercialJet(String flightNumber, int requiredFuel, int requiredMeals, int requiredCarts, int rewardAmount, int turnaroundTime) {
		super(flightNumber, requiredFuel, requiredMeals, requiredCarts, rewardAmount, turnaroundTime);
	}

	@Override
	public String getAircraftType() {
		return "Commercial Jet";
	}
	
}
