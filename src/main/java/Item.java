import java.util.Random;

/**
 * The Item class represents an object with associated utility and costs.
 */
public class Item {
    private final double utility;
    private final double[] costs;
    /*private static final long SEED = 12345L;
    private static final Random RANDOM = new Random(SEED); */// Use for the report
    private static final Random RANDOM = new Random();

    /**
     * Constructs an Item with the specified utility and costs.
     * @param utility   the utility of the item
     * @param costs     an array of costs for different constraints
     */
    public Item(double utility, double[] costs) {
        this.utility = utility;
        this.costs = costs;
    }

    /**
     * Generate randomly a list of items
     * @param itemsNumber           Number of items generated
     * @param constraintsNumber     Number of constraints for each item
     * @return  A list which contains "numberItems" items
     */
    public static Item[] generateRandomItems(int itemsNumber, int constraintsNumber, double[] budgets) {
        Item[] items = new Item[itemsNumber];

        for (int i = 0; i < itemsNumber; i++) {
            double utility = RANDOM.nextDouble() * itemsNumber*10;
            double[] costs = new double[constraintsNumber];
            for (int j = 0; j < constraintsNumber; j++) {
                costs[j] = RANDOM.nextDouble() * (budgets[j]-1)/4;
            }
            items[i] = new Item(utility, costs);
        }
        return items;
    }

    public double getUtility() {
        return utility;
    }

    public double[] getCosts() {
        return costs;
    }

    public double getCost(int index) {
        return costs[index];
    }
}
