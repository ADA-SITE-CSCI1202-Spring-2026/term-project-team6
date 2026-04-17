public class FuelingTruck implements IGroundService {

	@Override
	public boolean canService(Aircraft aircraft) {
		return true;
	}

	@Override
	public void serviceFlight(Aircraft aircraft) {
	}
}
