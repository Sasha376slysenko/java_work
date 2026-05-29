package org.example.Visualizer;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class PotentialEnergyChart {
    private final int MAX_POINTS = 10000;
    private final LineChart<Number, Number> chart;
    private final String style = "-fx-stroke: #00fa25; -fx-stroke-width: 5px";
    private final XYChart.Series<Number, Number> penergySeries = new XYChart.Series<>();

    public PotentialEnergyChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time");
        yAxis.setLabel("Potential energy");
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        yAxis.setForceZeroInRange(false);

        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setTitle("Potential energy");
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.getData().add(penergySeries);
    }

    public void addPoint(double time, double energyTotal) {
        XYChart.Data<Number, Number> point = new XYChart.Data<>(time, energyTotal);

        penergySeries.getData().add(point);
        if (penergySeries.getData().size() > MAX_POINTS) {
            penergySeries.getData().remove(0);
        }
    }

    public void appendStyle() {
        penergySeries.getNode().setStyle(style);
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }
}
