public class Constants {
    public static final int ITEM_NUMBER = 30;
    public static final int CONSTRAINT_NUMBER = 10;
    public static final int SOLUTION_NUMBER = 20;
    public static final double MUTATION_RATE = 0.05;
    public static final int GENERATIONS = 500;
    public static final double ELITISM_RATE = 0.1;
    public static final int TOURNAMENT_SIZE = 4;
    public static final int MULTI = 100;
    public static final String[] MUTATIONS = {"mutation", "flipMutation", "swapMutation"};
    public static final String[] CROSSOVERS = {"crossover", "divideCrossover", "shuffleCrossover"};
    public static final String[] REPAIRS = {"repair", "repairWeightedUtility"};
    public static final String[] SELECTIONS = {"selectParents", "rouletteWheelSelection", "rankSelection", "tournamentSelection"};
    public static final int METHOD_NUMBER = MUTATIONS.length*CROSSOVERS.length*REPAIRS.length*SELECTIONS.length;



}
