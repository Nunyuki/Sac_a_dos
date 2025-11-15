import java.util.*;

/**
 * The Population class represents a population of solutions for the genetic algorithm.
 */
public class Population {
    private final Solution[] solutions;  // Tableau de solutions
    private final Item[] items;
    private final double[] budgets;
    private final String repairMethod;

    /**
     * Constructs a Population with the specified size, items, and budgets.
     * @param size          the number of solutions in the population
     * @param items         an array of items
     * @param budgets       an array of budget constraints
     * @param repairMethod  repair method to use in solutions
     */
    public Population(int size, Item[] items, double[] budgets, String repairMethod) {
        this.items = items;
        this.budgets = budgets;
        this.solutions = new Solution[size];
        this.repairMethod = repairMethod;
        initialize();
    }

    /**
     * Initializes the population with random solutions.
     */
    private void initialize() {
        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = new Solution(items, budgets,repairMethod);
            solutions[i].initializeRandomly();
        }
    }

    /**
     * Gets the best solution in the population based on utility.
     * @return the best solution
     */
    public Solution getBestSolution() {
        return Arrays.stream(solutions).max(Comparator.comparingDouble(Solution::getUtilities)).orElse(null);
    }

    /**
     * Gets the worst solution in the population based on utility.
     * @return the worst solution
     */
    public Solution getWorstSolution() {
        return Arrays.stream(solutions).min(Comparator.comparingDouble(Solution::getUtilities)).orElse(null);
    }

    public Solution[] getSolutions() {
        return solutions;
    }

    public int solutionLength(){
        return solutions.length;
    }

    public Item[] getItems(){
        return items;
    }

    public double[] getBudgets(){
        return budgets;
    }

    public String getRepairMethod() {
        return repairMethod;
    }
}
