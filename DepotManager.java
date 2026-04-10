import java.util.HashMap;

enum SupplyItem {

	JET_FUEL,
	MEALS,
	CART

}

public class DepotManager {
	
	private HashMap<SupplyItem, Integer> resources;
	private int budget;
}