package org.example.Visualizer;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class EnergyChart {
    private final int MAX_POINTS = 10000;
    private final LineChart<Number, Number> chart;
    private final String style = "-fx-stroke: #0d0c0c; -fx-stroke-width: 5px";
    private final XYChart.Series<Number, Number> energySeries = new XYChart.Series<>();

    public EnergyChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time");
        yAxis.setLabel("Total Energy");
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        yAxis.setForceZeroInRange(false);

        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setTitle("Total Energy");
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.getData().add(energySeries);
    }

    public void addPoint(double time, double energyTotal) {
        XYChart.Data<Number, Number> point = new XYChart.Data<>(time, energyTotal);

        energySeries.getData().add(point);
        if (energySeries.getData().size() > MAX_POINTS) {
            energySeries.getData().remove(0);
        }
    }

    public void appendStyle() {
        energySeries.getNode().setStyle(style);
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }
}
