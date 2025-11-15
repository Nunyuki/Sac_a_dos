import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        GeneticAlgorithm.solveMulti();
        SwingUtilities.invokeLater(() -> {
            Graph example = new Graph("Algorithme Genetique");
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
}
