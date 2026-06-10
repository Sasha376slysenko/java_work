package org.example.Visualizer;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

public class EnergyChart {
    private final int MAX_POINTS = 2000;
    private final VBox container;

    private final XYChart.Series<Number, Number> eKSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> ePSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> eTSeries = new XYChart.Series<>();

    public EnergyChart() {
        final LineChart<Number, Number> kinChart;
        final LineChart<Number, Number> potTotalChart;
        final String nameTitle1 = "Energy (eV): K:(red)";
        final String nameTitle2 = "Energy (eV): T:(black), V:(green)";

        // ============= GRAPH 1 ==================
        NumberAxis xKin = new NumberAxis();
        NumberAxis yKin = new NumberAxis();

        xKin.setLabel("MD: dt=0.2(fs) , MC: Step");
        yKin.setLabel("K Energy(eV)");
        xKin.setAnimated(false);
        yKin.setAnimated(false);
        yKin.setForceZeroInRange(false);

        kinChart = new LineChart<>(xKin, yKin);
        kinChart.setAnimated(false);
        kinChart.setTitle(nameTitle1);
        kinChart.setCreateSymbols(false);
        kinChart.setLegendVisible(false);
        kinChart.getData().add(eKSeries);

        // ============= GRAPH 2 ==================
        NumberAxis xPot = new NumberAxis();
        NumberAxis yPot = new NumberAxis();

        xPot.setLabel("MD: dt=0.2(fs) , MC: Step");
        yPot.setLabel("P, T Energy(eV)");
        xPot.setAnimated(false);
        yPot.setAnimated(false);
        yPot.setForceZeroInRange(false);

        potTotalChart = new LineChart<>(xPot, yPot);
        potTotalChart.setAnimated(false);
        potTotalChart.setTitle(nameTitle2);
        potTotalChart.setCreateSymbols(false);
        potTotalChart.setLegendVisible(false);
        potTotalChart.getData().add(ePSeries);
        potTotalChart.getData().add(eTSeries);

        // CONTAINER
        container = new VBox(10, kinChart, potTotalChart);
    }

    public void addPointEK(double time, double energyKinetick) {
        /*
        * +--------------------------+
        * |Add points energy kinetic.|
        * +--------------------------+
        */
        XYChart.Data<Number, Number> point = new XYChart.Data<>(
            time, energyKinetick);

        eKSeries.getData().add(point);
        if (eKSeries.getData().size() > MAX_POINTS) {
            eKSeries.getData().removeFirst();
        }
    }

    public void addPointEP(double time, double energyPotential) {
        /*
        * +----------------------------+
        * |Add points energy potential.|
        * +----------------------------+
        */
        XYChart.Data<Number, Number> point = new XYChart.Data<>(
            time, energyPotential);

        ePSeries.getData().add(point);
        if (ePSeries.getData().size() > MAX_POINTS) {
            ePSeries.getData().removeFirst();
        }
    }

    public void addPointET(double time, double energyTotal) {
        /*
        * +------------------------+
        * |Add points energy total.|
        * +------------------------+
        */
        XYChart.Data<Number, Number> point = new XYChart.Data<>(
            time, energyTotal);

        eTSeries.getData().add(point);
        if (eTSeries.getData().size() > MAX_POINTS) {
            eTSeries.getData().removeFirst();
        }
    }

    public void appendStyleEK() {
        final String styleEK = "-fx-stroke: #ff0505; -fx-stroke-width: 5px;";
        eKSeries.getNode().setStyle(styleEK);
    }

    public void appendStyleEP() {
        final String styleEP = "-fx-stroke: #00fa25; -fx-stroke-width: 5px";
        ePSeries.getNode().setStyle(styleEP);
    }

    public void appendStyleET() {
        final String styleET = "-fx-stroke: #0d0c0c; -fx-stroke-width: 5px";
        eTSeries.getNode().setStyle(styleET);
    }

    public VBox getContainer() {
        return container;
    }
}
