public interface IGroundCrew {

	// Returns true if task is processable
	boolean canProcess(Aircraft task);
	void processTask(Aircraft task);
}