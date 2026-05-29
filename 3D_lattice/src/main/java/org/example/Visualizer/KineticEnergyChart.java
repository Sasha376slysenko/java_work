package org.example.Visualizer;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class KineticEnergyChart {
    private final int MAX_POINTS = 10000;
    private final LineChart<Number, Number> chart;
    private final String style = "-fx-stroke: #ff0505; -fx-stroke-width: 5px;";
    private final XYChart.Series<Number, Number> kenergySeries = new XYChart.Series<>();

    public KineticEnergyChart() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time");
        yAxis.setLabel("Kinetic energy");
        xAxis.setAnimated(false);
        yAxis.setAnimated(false);
        yAxis.setForceZeroInRange(false);

        chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.setTitle("Kinetic energy");
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.getData().add(kenergySeries);
    }

    public void addPoint(double time, double energyTotal) {
        XYChart.Data<Number, Number> point = new XYChart.Data<>(time, energyTotal);

        kenergySeries.getData().add(point);
        if (kenergySeries.getData().size() > MAX_POINTS) {
            kenergySeries.getData().remove(0);
        }
    }

    public void appendStyle() {
        kenergySeries.getNode().setStyle(style);
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }
}
