package org.example.Visualizer;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

public class EnergyChart {
    private final int MAX_POINTS = 10000;

    private final VBox container;
    private final LineChart<Number, Number> kinChart;
    private final LineChart<Number, Number> potTotalChart;

    private final String nameTitel1 = "Energy (eV): K:(red)";
    private final String nameTitel2 = "Energy (eV): T:(black), V:(green)";
    private final String styleEK = "-fx-stroke: #ff0505; -fx-stroke-width: 5px;";
    private final String styleEP = "-fx-stroke: #00fa25; -fx-stroke-width: 5px";
    private final String styleET = "-fx-stroke: #0d0c0c; -fx-stroke-width: 5px";

    private final XYChart.Series<Number, Number> eKSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> ePSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> eTSeries = new XYChart.Series<>();

    public EnergyChart() {
        // ============= GRAPH 1 ==================
        NumberAxis xKin = new NumberAxis();
        NumberAxis yKin = new NumberAxis();

        xKin.setLabel("Step");
        yKin.setLabel("K Energy(eV)");
        xKin.setAnimated(false);
        yKin.setAnimated(false);
        yKin.setForceZeroInRange(false);

        kinChart = new LineChart<>(xKin, yKin);
        kinChart.setAnimated(false);
        kinChart.setTitle(nameTitel1);
        kinChart.setCreateSymbols(false);
        kinChart.setLegendVisible(false);
        kinChart.getData().add(eKSeries);

        // ============= GRAPH 2 ==================
        NumberAxis xPot = new NumberAxis();
        NumberAxis yPot = new NumberAxis();

        xPot.setLabel("Step");
        yPot.setLabel("P, T Energy(eV)");
        xPot.setAnimated(false);
        yPot.setAnimated(false);
        yPot.setForceZeroInRange(false);

        potTotalChart = new LineChart<>(xPot, yPot);
        potTotalChart.setAnimated(false);
        potTotalChart.setTitle(nameTitel2);
        potTotalChart.setCreateSymbols(false);
        potTotalChart.setLegendVisible(false);
        potTotalChart.getData().add(ePSeries);
        potTotalChart.getData().add(eTSeries);

        // CONTAINER
        container = new VBox(10, kinChart, potTotalChart);
    }

    public void addPointEK(double time, double energyKinetick) {
        /*
        * +---------------------------+
        * |Add points enegry kinetick.|
        * +---------------------------+
        */
        XYChart.Data<Number, Number> point = new XYChart.Data<>(
            time, energyKinetick);

        eKSeries.getData().add(point);
        if (eKSeries.getData().size() > MAX_POINTS) {
            eKSeries.getData().remove(0);
        }
    }

    public void addPointEP(double time, double energyPotential) {
        /*
        * +----------------------------+
        * |Add points enegry potential.|
        * +----------------------------+
        */
        XYChart.Data<Number, Number> point = new XYChart.Data<>(
            time, energyPotential);

        ePSeries.getData().add(point);
        if (ePSeries.getData().size() > MAX_POINTS) {
            ePSeries.getData().remove(0);
        }
    }

    public void addPointET(double time, double energyTotal) {
        /*
        * +------------------------+
        * |Add points enegry total.|
        * +------------------------+
        */
        XYChart.Data<Number, Number> point = new XYChart.Data<>(
            time, energyTotal);

        eTSeries.getData().add(point);
        if (eTSeries.getData().size() > MAX_POINTS) {
            eTSeries.getData().remove(0);
        }
    }

    public void appendStyleEK() {
        eKSeries.getNode().setStyle(styleEK);
    }

    public void appendStyleEP() {
        ePSeries.getNode().setStyle(styleEP);
    }

    public void appendStyleET() {
        eTSeries.getNode().setStyle(styleET);
    }

    public VBox getContainer() {
        return container;
    }
}
