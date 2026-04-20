
public abstract class Aircraft {

	private String flightNumber;
	private int requiredFuel;
	private int requiredMeals;
	private int requiredCarts;
	private int rewardAmount;
	private int turnaroundTime;

	public Aircraft(
			String flightNumber,
			int requiredFuel,
			int requiredMeals,
			int requiredCarts,
			int rewardAmount,
			int turnaroundTime) {
		this.flightNumber = flightNumber;
		this.requiredFuel = requiredFuel;
		this.requiredMeals = requiredMeals;
		this.requiredCarts = requiredCarts;
		this.rewardAmount = rewardAmount;
		this.turnaroundTime = turnaroundTime;
	}

	// Getters
	public String getFlightNumber() {
		return flightNumber;
	}

	public int getRequiredFuel() {
		return requiredFuel;
	}

	public int getRequiredMeals() {
		return requiredMeals;
	}

	public int getRequiredCarts() {
		return requiredCarts;
	}

	public int getRewardAmount() {
		return rewardAmount;
	}

	public int getTurnaroundTime() {
		return turnaroundTime;
	}

	// Setters
	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public void setRequiredFuel(int requiredFuel) {
		this.requiredFuel = requiredFuel;
	}

	public void setRequiredMeals(int requiredMeals) {
		this.requiredMeals = requiredMeals;
	}

	public void setRequiredCarts(int requiredCarts) {
		this.requiredCarts = requiredCarts;
	}

	public void setRewardAmount(int rewardAmount) {
		this.rewardAmount = rewardAmount;
	}

	public void setTurnaroundTime(int turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	// Abstract method for polymorphism
	public abstract String getAircraftType();

	@Override
	public String toString() {
		return getAircraftType() + " - Flight " + flightNumber;
	}

}