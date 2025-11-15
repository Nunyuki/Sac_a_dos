import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import javax.swing.*;
import java.awt.*;

public class Graph extends JFrame {

    public Graph(String title) {
        super(title);

        XYSeriesCollection dataset = createDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "Génération",
                "Utilité",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        chart.getPlot().setBackgroundPaint(Color.WHITE);
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800, 600));
        setContentPane(panel);

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        //Use for the report
        /*Color[] baseColors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};

        for (int i = 0; i < 12; i++) {
            int colorIndex = i % 4;
            float[] hsb = Color.RGBtoHSB(baseColors[colorIndex].getRed(), baseColors[colorIndex].getGreen(), baseColors[colorIndex].getBlue(), null);
            float brightness = 1.0f - (0.2f * (i / 4)); // Ajuste la luminosité
            Color variantColor = Color.getHSBColor(hsb[0], hsb[1], brightness);

            renderer.setSeriesPaint(i, variantColor);
            renderer.setSeriesShapesVisible(i, false);
        }*/
    }

    private XYSeriesCollection createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series;

        for(int i=0; i<Constants.METHOD_NUMBER;i++){
            series = new XYSeries("Courbe" + (i + 1));
            for(int j=0; j<Constants.GENERATIONS;j++){
                series.add(j, GeneticAlgorithm.meanValue[i][j]);
            }


        //Use for write the report
        /*for (int i = 0; i < 12; i++) {
            XYSeries series;

            if (i < 4) {
                series = new XYSeries("Courbe mutation " + (i + 1));
            } else if (i < 8) {
                series = new XYSeries("Courbe flipMutation " + (i - 3));
            } else {
                series = new XYSeries("Courbe swapMutation " + (i - 7));
            }

            for (int j = 0; j < 500; j++) {
                series.add(j, GeneticAlgorithm.meanValue[i][j] - 1700);
            }*/

            dataset.addSeries(series);
        }
        return dataset;
    }
}
