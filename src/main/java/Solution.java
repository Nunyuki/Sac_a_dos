import java.util.*;

/**
 * The Solution class represents a potential solution to the problem.
 * A solution is represented by a binary vector: 1 if the item is selected, 0 otherwise.
 * The class calculates total utilities, total costs.
 * The class can do operations like mutation, crossover and repair the solution to make it valid
 */
public class Solution {
    private final Item[] items;
    private final int[] selection;
    private final double[] budgets;
    private final double[] costs;
    private double utilities;
    private static String repairMethod;

    /**
     * Constructs a Solution with the specified items and budgets.
     * @param items         an array of items
     * @param budgets       an array of budget constraints
     * @param repairMethod  repair method to use
     */
    public Solution(Item[] items, double[] budgets, String repairMethod){
        this.items = items;
        this.selection = new int[items.length];
        this.budgets = budgets;
        this.costs = new double[budgets.length];
        this.utilities = 0.0;
        Solution.repairMethod = repairMethod;
    }

    /**
     * Initializes the solution randomly and repairs it to be valid.
     */
    public void initializeRandomly(){
        for(int i = 0; i < selection.length; i++){
            selection[i] =(Math.random()<0.5) ? 0 : 1;
        }
        chooseRepair();
    }

    /**
     * Calculates the total utilities based on the current selection.
     */
    public void calculateUtilities(){
        this.utilities = 0.0;
        for(int i=0; i<selection.length; i++){
            if(selection[i] == 1){
                this.utilities += this.items[i].getUtility();
            }
        }
    }

    /**
     * Calculates the total costs for each constraint based on the current selection.
     */
    public void calculateCosts(){
        Arrays.fill(this.costs, 0);
        for(int i=0; i<selection.length; i++){
            if(selection[i] == 1){
                for(int j=0; j<costs.length; j++){
                    costs[j] += items[i].getCost(j);
                }
            }
        }
    }

    /**
     * Modifies the costs in the total costs.
     * @param costsArray    the costs to apply
     * @param factor        the factor to apply (1 to add, -1 to remove)
     */
    public void modifyCosts(double[] costsArray, int factor){
        for(int i=0; i<this.costs.length; i++){
            this.costs[i] += factor * costsArray[i];
        }
    }

    /**
     * Checks if adding costs exceeds the budget.
     * @param costsArray the costs to test
     * @return true if the resulting costs are within budget, else false
     */
    private boolean checkCosts(double[] costsArray) {
        for (int i = 0; i < costsArray.length; i++) {
            if (this.costs[i] + costsArray[i] > this.budgets[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Modifies items in the selection based on given indexes.
     * @param indexes   the indexes of items to modify
     * @param factor    the factor to apply (1 to add, -1 to remove)
     */
    private void modifyItems(Integer[] indexes, int factor) {
        for (int i : indexes) {
            //Remove item i and his cost from solution's cost
            if (factor == -1 && selection[i] == 1) {
                selection[i] = 0;
                modifyCosts(items[i].getCosts(), factor);
                if(checkCosts(new double[costs.length])){
                    return;
                }
            }
            //Add item i and his cost from solution's cost
            else if (factor == 1 && selection[i] == 0 && checkCosts(items[i].getCosts())) {
                selection[i] = 1;
                modifyCosts(items[i].getCosts(), factor);
            }
        }
    }

    private void chooseRepair() {
        switch(repairMethod){
            case "repair" :  repair(); break;
            case "repairWeightedUtility" : repairWeightedUtility(); break;
            default: throw new IllegalStateException("Repair Not Found !!");
        }
        calculateUtilities();
        calculateCosts();
    }

    private static Solution chooseRepairNewSolution(Solution newSolution) {
        switch (repairMethod) {
            case "repair" -> newSolution.repair();
            case "repairWeightedUtility" -> newSolution.repairWeightedUtility();
            default -> throw new IllegalStateException("Repair Not Found !!");
        }

        newSolution.calculateUtilities();
        newSolution.calculateCosts();
        return newSolution;
    }

    /**
     * Applies a mutation by changing a random coordinate from 1 to 0, or from 0 to 1, and repair it to be valid.
     */
    public void mutation() {
        int index = (int) (Math.random() * selection.length);
        selection[index] = 1 - selection[index];
        chooseRepair();
    }

    /**
     * Applies a mutation by changing two value between two coordinates.
     */
    public void swapMutation() {
        int index1 = (int) (Math.random() * selection.length);
        int index2 = index1;

        while(selection[index1] == selection[index2]){
            index2 = (int) (Math.random()*selection.length);
        }

        //Exchange items in the selection at position index1 and index2 value
        selection[index1] = 1 - selection[index1];
        selection[index2] = 1 - selection[index2];
        chooseRepair();
    }

    /**
     * Applies a mutation by changing coordinates from 1 to 0, or from 0 to 1, and repair it to be valid.
     */
    public void flipMutation(double mutationRate) {

        // for each item, a small probabilities to mutate
        for (int i = 0; i < selection.length; i++) {
            if (Math.random() < mutationRate) {
                selection[i] = 1 - selection[i];
            }
        }
        chooseRepair();
    }

    /**
     * Creates a new solution by using two parent solutions, and repair it to be valid.
     * It will choose randomly the selection[i] between two parents
     * @param father    one parent solution
     * @param mother    another parent solution
     * @return the new solution created from parents
     */
    public static Solution crossover(Solution father, Solution mother) {
        Solution newSolution = new Solution(mother.items, mother.budgets, mother.repairMethod);

        // The new solution has the same probability to have the item i from the father or from the mother for all the selection
        for (int i = 0; i < newSolution.getSelection().length; i++) {
            newSolution.selection[i] = (Math.random() < 0.5) ? father.selection[i] : mother.selection[i];
        }

        return chooseRepairNewSolution(newSolution);
    }

    /**
     * Creates a new solution by using two parent solutions, and repair it to be valid.
     * It will choose an index randomly, each segment will be the mother or the father
     * @param father    one parent solution
     * @param mother    another parent solution
     * @return the new solution created from parents
     */
    public static Solution divideCrossover(Solution father, Solution mother) {
        Solution newSolution = new Solution(mother.getItems(), mother.getBudgets(), mother.repairMethod);
        // The point which cut the selection by 2
        int crossoverIndex = (int) (Math.random() * mother.getSelection().length);

        // Mother and father has the same probability to be the head or the tail of the selection
        if(Math.random()<0.5){
            for (int i = 0; i < crossoverIndex; i++) {
                newSolution.getSelection()[i] = father.getSelection()[i];
            }
            for (int i = crossoverIndex; i < mother.getSelection().length; i++) {
                newSolution.getSelection()[i] = mother.getSelection()[i];
            }
        }
        else{
            for (int i = 0; i < crossoverIndex; i++) {
                newSolution.getSelection()[i] = mother.getSelection()[i];
            }
            for (int i = crossoverIndex; i < mother.getSelection().length; i++) {
                newSolution.getSelection()[i] = father.getSelection()[i];
            }
        }

        return chooseRepairNewSolution(newSolution);
    }

    /**
     * Creates a new solution by using two parent solutions, and repair it to be valid.
     * It will shuffle a table of index, and choosing if the items is from father of mother by the parity of the index
     * @param father    one parent solution
     * @param mother    another parent solution
     * @return the new solution created from parents
     */
    public static Solution shuffleCrossover(Solution father, Solution mother) {
        Solution newSolution = new Solution(mother.items, mother.budgets,repairMethod);
        Random random = new Random();
        int length = newSolution.getSelection().length;

        // Create a list of index and shuffle it value
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);

        // Attributes father's value if the index is even, the mother's value if the index is odd
        for (int i = 0; i < length; i++) {
            newSolution.selection[indices.get(i)] = (i % 2 == 0) ? father.selection[indices.get(i)] : mother.selection[indices.get(i)];
        }

        return chooseRepairNewSolution(newSolution);
    }

    /**
     * Repairs the solution to ensure it meets the budget constraints.
     * First, it removes items with the lowest utility until the solution is valid.
     * Then, it adds items with the highest utility while staying within the budget.
     */
    public void repair() {
        calculateUtilities();
        calculateCosts();
        if(checkCosts(new double[this.costs.length])){
            return;
        }

        Integer[] indexes = new Integer[selection.length];
        for(int i = 0; i < selection.length; i++){
            indexes[i] = i;
        }

        // Remove items with lowest utility first
        Arrays.sort(indexes,(j,k) -> Double.compare(items[j].getUtility(),items[k].getUtility()));
        modifyItems(indexes,-1);

        //Add items with highest utility first
        Arrays.sort(indexes,(j,k) -> Double.compare(items[k].getUtility(),items[j].getUtility()));
        modifyItems(indexes,1);

        calculateUtilities();
        calculateCosts();
    }

    /**
     * Repairs the solution by considering the weighted utility ratio.
     * Removes items based on the lowest ratio of utility to cost sum,
     * and adds items based on the highest ratio while ensuring budget constraints are met.
     */
    public void repairWeightedUtility() {
        calculateCosts();
        calculateUtilities();
        if (checkCosts(new double[this.costs.length])) {
            return;
        }

        // Calculate utility to cost ratios for each item
        double[] ratios = new double[selection.length];
        for (int i = 0; i < selection.length; i++) {
            double costSum = Arrays.stream(items[i].getCosts()).sum();
            ratios[i] = items[i].getUtility() / costSum;
        }

        Integer[] indexes = new Integer[selection.length];
        for (int i = 0; i < selection.length; i++) {
            indexes[i] = i;
        }

        // Sort item indexes by ratio ascending for removal
        Arrays.sort(indexes, Comparator.comparingDouble(i -> ratios[i]));
        modifyItems(indexes, -1);

        // Sort item indexes by ratio descending for addition
        Arrays.sort(indexes, (i1, i2) -> Double.compare(ratios[i2], ratios[i1]));
        modifyItems(indexes, 1);

        calculateUtilities();
        calculateCosts();
    }

    public int[] getSelection() {
        return selection;
    }

    public double getUtilities() {
        return utilities;
    }

    public Item[] getItems(){
        return items;
    }

    public double[] getBudgets() {
        return budgets;
    }

    public void setRepairMethod(String repairMethod) {
        Solution.repairMethod = repairMethod;
    }

    public double[] getCosts() {
        return costs;
    }
}
