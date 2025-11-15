import java.lang.reflect.Array;
import java.util.*;

/**
 * The GeneticAlgorithm class represents the genetic algorithm for optimizing solutions.
 */
public class GeneticAlgorithm {
    private Population population;
    private final double mutationRate;
    private final int generations;
    private final double elitismRate;
    private static Solution[][] solution;
    public static double[][] meanValue;
    public static double[][] standardDeviationValue;

    /**
     * Constructs a GeneticAlgorithm with the specified population, mutation rate, and number of generations.
     * @param population    the initial population
     */
    public GeneticAlgorithm(Population population) {
        this.population = population;
        this.mutationRate = Constants.MUTATION_RATE;
        this.generations = Constants.GENERATIONS;
        this.elitismRate = Constants.ELITISM_RATE;
    }

    /**
     * Selects two parent solutions randomly from the population.
     * @return an array containing two parent solutions
     */
    private Solution[] selectParents() {

        Random rand = new Random();
        Solution mother = population.getSolutions()[rand.nextInt(population.getSolutions().length)];
        Solution father = mother;

        while (father == mother) {
            father = population.getSolutions()[rand.nextInt(population.getSolutions().length)];
        }
        return new Solution[]{father, mother};
    }

    /**
     * Selects two parents using the roulette wheel selection method.
     * @return an array containing two selected parent solutions
     */
    private Solution[] rouletteWheelSelection() {
        // Calculate the total utility sum of all solutions in the population
        double utilities = Arrays.stream(population.getSolutions())
                .mapToDouble(Solution::getUtilities)
                .sum();

        Solution mother = selectSolutionByRoulette(utilities);
        Solution father = mother;

        while (father == mother) {
            father = selectSolutionByRoulette(utilities);
        }

        return new Solution[]{father, mother};
    }

    /**
     * Selects a solution using roulette wheel selection based on total utility sum.
     * @param utilities the sum of fitness of all solutions in the population
     * @return the selected solution
     */
    private Solution selectSolutionByRoulette(double utilities) {
        double randomPoint = Math.random() * utilities;
        double currentUtility = 0.0;

        for (Solution solution : population.getSolutions()) {
            currentUtility += solution.getUtilities();

            // If cumulative utility surpasses or equals the random point, return this solution
            if (currentUtility >= randomPoint) {
                return solution;
            }
        }
        return population.getSolutions()[population.getSolutions().length - 1];
    }

    /**
     * Selects two parents using the rank selection method.
     * @return an array containing two selected parent solutions
     */
    private Solution[] rankSelection() {
        // Sort solutions by utility in ascending order
        Solution[] sortedSolutions = Arrays.stream(population.getSolutions())
                .sorted(Comparator.comparingDouble(Solution::getUtilities))
                .toArray(Solution[]::new);

        // Calculate the total rank sum of sorted solutions
        double totalRank = (sortedSolutions.length * (sortedSolutions.length + 1)) / 2.0;

        Solution mother = selectSolutionByRank(sortedSolutions, totalRank);
        Solution father = mother;

        while (father == mother) {
            father = selectSolutionByRank(sortedSolutions, totalRank);
        }

        return new Solution[]{father, mother};
    }

    /**
     * Selects a solution using rank-based selection with the total rank sum.
     * @param sortedSolutions   solutions sorted by utility
     * @param ranks             the sum of ranks of all solutions in the population
     * @return the selected solution
     */
    private Solution selectSolutionByRank(Solution[] sortedSolutions, double ranks) {
        double randomPoint = Math.random() * ranks;
        double cumulativeRank = 0.0;

        for (int i = 0; i < sortedSolutions.length; i++) {
            cumulativeRank += (i + 1);

            // If cumulative rank surpasses or equals the random point, return this solution
            if (cumulativeRank >= randomPoint) {
                return sortedSolutions[i];
            }
        }
        return sortedSolutions[sortedSolutions.length - 1];
    }

    /**
     * Selects two parents using the tournament selection method.
     * @param tournamentSize    the size of the tournament
     * @return an array containing two selected parent solutions
     */
    private Solution[] tournamentSelection(int tournamentSize) {
        Solution mother = selectSolutionByTournament(tournamentSize);
        Solution father = mother;

        while (father == mother) {
            father = selectSolutionByTournament(tournamentSize);
        }

        return new Solution[]{father, mother};
    }

    /**
     * Selects a solution using tournament selection with a specified tournament size.
     * @param tournamentSize    the size of the tournament
     * @return the selected solution
     */
    private Solution selectSolutionByTournament(int tournamentSize) {
        Random rand = new Random();
        Solution[] tournament = new Solution[tournamentSize];

        // Randomly select solutions for the tournament
        for (int i = 0; i < tournamentSize; i++) {
            tournament[i] = population.getSolutions()[rand.nextInt(population.getSolutions().length)];
        }

        // Return the solution with the highest utility among those selected
        return Arrays.stream(tournament).max(Comparator.comparingDouble(Solution::getUtilities)).orElse(null);
    }

    /**
     * Applies elitism by preserving the best solutions in the new generation.
     * @param newPopulation the newly generated population
     * @param elitists   the number of solutions to preserve
     */
    private void applyElitism(Population newPopulation, int elitists) {
        // Sort solutions by utility in descending order
        Solution[] sortedSolutions = Arrays.stream(population.getSolutions())
                .sorted(Comparator.comparingDouble(Solution::getUtilities).reversed())
                .toArray(Solution[]::new);

        // Copy the best solutions into the new population
        for (int i = 0; i < elitists; i++) {
            newPopulation.getSolutions()[i] = sortedSolutions[i];
        }
    }


    /**
     * Runs the genetic algorithm and returns the best solution found.
     * @return the best solution found
     */
    public Solution solve(String mutationMethod, String crossoverMethod, String repairMethod, String selectionMethod,int tour) {
        Solution[] parents;
        Solution newSolution;
        Population newPopulation;
        for (int gen = 0; gen < generations; gen++) {
            newPopulation = population;
            int elitists = (int)(newPopulation.getSolutions().length*elitismRate);
            applyElitism(newPopulation,elitists);
            for (int i = elitists; i < population.getSolutions().length; i++) {
                parents = switch (selectionMethod) {
                    case "selectParents" -> selectParents();
                    case "rouletteWheelSelection" -> rouletteWheelSelection();
                    case "rankSelection" -> rankSelection();
                    case "tournamentSelection" -> tournamentSelection(Constants.TOURNAMENT_SIZE);
                    default -> throw new IllegalStateException("Selection Not Found !!");
                };
                newSolution = new Solution(population.getItems(), population.getBudgets(),repairMethod);
                newSolution.setRepairMethod(repairMethod);
                newSolution = switch (crossoverMethod) {
                    case "crossover" -> Solution.crossover(parents[0],parents[1]);
                    case "divideCrossover" -> Solution.divideCrossover(parents[0],parents[1]);
                    case "shuffleCrossover" -> Solution.shuffleCrossover(parents[0],parents[1]);
                    default -> throw new IllegalStateException("Crossover Not Found !!");

                };
                if (Math.random() < mutationRate) {
                    switch (mutationMethod) {
                        case "mutation" : newSolution.mutation(); break;
                        case "flipMutation" : newSolution.flipMutation(mutationRate); break;
                        case "swapMutation" : newSolution.swapMutation(); break;
                        default : throw new IllegalStateException("Mutation Not Found !!");
                    }
                }
                newPopulation.getSolutions()[i] = newSolution;
            }
            population = newPopulation;
            solution[tour][gen] = newPopulation.getBestSolution();
        }
        return population.getBestSolution();
    }

    public static void solveMulti(){
        solution = new Solution[Constants.MULTI][Constants.GENERATIONS];
        meanValue = new double[Constants.METHOD_NUMBER][Constants.GENERATIONS];
        standardDeviationValue = new double[Constants.METHOD_NUMBER][Constants.GENERATIONS];
        double[] budgets = new double[Constants.CONSTRAINT_NUMBER];
        double[][] computationTimes = new double[Constants.METHOD_NUMBER][Constants.GENERATIONS];

        for (int i = 0; i < Constants.CONSTRAINT_NUMBER; i++) {
            budgets[i] = new Random().nextDouble()*Constants.ITEM_NUMBER*2+(Constants.ITEM_NUMBER/2.0);
        }

        System.out.println(STR."Budgets : \{Arrays.toString(budgets)}");
        Item[] items = Item.generateRandomItems(Constants.ITEM_NUMBER,Constants.CONSTRAINT_NUMBER,budgets);

        //String[] repairs = {"repair"}; Use for the report
        //String[] crossovers = {"crossover"}; Use for the report


        int method = 0;

        for (String mutation : Constants.MUTATIONS) {
            for (String selection : Constants.SELECTIONS) {
                for (String repair : Constants.REPAIRS) {
                    for (String crossover : Constants.CROSSOVERS) {
                        System.out.println("--------------------------Méthode-----------------------------------------------");
                        System.out.println(STR."Mutation: \{mutation}, Crossover: \{crossover}, Repair: \{repair}, Selection: \{selection}");
                        for(int i = 0; i<Constants.MULTI; i++) {
                            Population population = new Population(Constants.SOLUTION_NUMBER, items, budgets, repair);
                            GeneticAlgorithm ga = new GeneticAlgorithm(population);
                            long startTime = System.nanoTime();
                            Solution bestSolution = ga.solve(mutation, crossover, repair, selection, i);
                            long endTime = System.nanoTime();
                            double elapsedTime = (endTime - startTime) / 1_000_000.0;
                            computationTimes[method][i] = elapsedTime; // Stocker le temps de calcul
                        }

                        for(int gen = 0; gen < Constants.GENERATIONS; gen++) {
                            double[] number = new double[Constants.MULTI];

                            for(int tour = 0; tour < Constants.MULTI; tour++) {
                                number[tour] = solution[tour][gen].getUtilities();
                            }

                            meanValue[method][gen] = mean(number);
                            standardDeviationValue[method][gen] = standardDeviation(number);
                            if(gen%50 == 0){
                                System.out.print(STR."------ Generation: \{gen} ------");
                                System.out.println(STR."Moyenne : \{meanValue[method][gen]} Ecart-type : \{standardDeviationValue[method][gen]}");
                            }
                        }
                        double meanTime = mean(computationTimes[method]);
                        System.out.println(STR."Temps moyen de calcul : \{meanTime} ms");
                        method ++;
                    }
                }
            }
        }
    }

    /**
     * Calculate the mean
     * @param numbers contains the number to use for calculating mean
     * @return the mean
     */
    public static double mean(double[] numbers) {
        double sum = 0.0;
        for (double number : numbers) {
            sum += number;
        }
        return sum / numbers.length;
    }

    /**
     * Calculate the standard deviation
     * @param numbers contains the number to use for calculating standard deviation
     * @return the standard deviation
     */
    public static double standardDeviation(double[] numbers) {
        double mean = mean(numbers);
        double sumSquaredDiff = 0.0;
        for (double number : numbers) {
            sumSquaredDiff += Math.pow(number - mean, 2);
        }
        double variance = sumSquaredDiff / numbers.length;
        return Math.sqrt(variance);
    }
}
