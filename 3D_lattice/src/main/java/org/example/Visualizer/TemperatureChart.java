package org.example.Visualizer;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class TemperatureChart {
    private final int MAX_POINTS = 10000;
    private final LineChart<Number, Number> chart;
    private final String style = "-fx-stroke: #8900fa; -fx-stroke-width: 5px";
    private final XYChart.Series<Number, Number> tempSeries = new XYChart.Series<>();

    public TemperatureChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time");
        yAxis.setLabel("Temperature");
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        yAxis.setForceZeroInRange(false);

        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setTitle("Temperature System");
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.getData().add(tempSeries);
    }

    public void addPoint(double time, double energyTotal) {
        XYChart.Data<Number, Number> point = new XYChart.Data<>(time, energyTotal);

        tempSeries.getData().add(point);
        if (tempSeries.getData().size() > MAX_POINTS) {
            tempSeries.getData().remove(0);
        }
    }

    public void appendStyle() {
        tempSeries.getNode().setStyle(style);
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }
}
