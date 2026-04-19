public class CommercialJet extends Aircraft {

	public CommercialJet(String flightNumber, int requiredFuel, int requiredMeals, int requiredCarts, int rewardAmount) {
		super(flightNumber, requiredFuel, requiredMeals, requiredCarts, rewardAmount);
	}

	@Override
	public String getAircraftType() {
		return "Commercial Jet";
	}
	
}
