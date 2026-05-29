package org.example;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;

import org.example.Lattice.CrystalLattice;
import org.example.Visualizer.EnergyChart;
import org.example.Visualizer.LatticeView1;
import org.example.Visualizer.TemperatureChart;
import org.example.Visualizer.KineticEnergyChart;
import org.example.Visualizer.PotentialEnergyChart;

public class Main extends Application {
    private EnergyChart energyChartS;
    private EnergyChart energyChartT;
    private LatticeView1 viewLatticeS;
    private LatticeView1 viewLatticeT;
    private CrystalLattice latticeSquare;
    private CrystalLattice latticeTriangle;
    private TemperatureChart temperatureChartS;
    private TemperatureChart temperatureChartT;
    private KineticEnergyChart kineticEnergyChartS;
    private KineticEnergyChart kineticEnergyChartT;
    private PotentialEnergyChart potentialEnergyChartS;
    private PotentialEnergyChart potentialEnergyChartT;

    // Window Geometry
    private int widthWindow = 1800;
    private int heightWindow = 860;

    // Type
    private int typeS = 1;
    private int typeT = 2;

    // Step
    private long step = 0;

    @Override
    public void start(Stage stage) {
        int nx = 4;
        int ny = 4;
        int nz = 4;
        double latticeParameter = 1.4;

        latticeSquare = new CrystalLattice(nx, ny, nz, typeS, latticeParameter);
        latticeTriangle = new CrystalLattice(nx, ny, nz, typeT, latticeParameter);

        latticeSquare.setTemperature(0.01);
        latticeTriangle.setTemperature(0.01);

        viewLatticeS = new LatticeView1(
            nx, ny, nz,
            latticeSquare.getCoordinateXLattice(),
            latticeSquare.getCoordinateYLattice(),
            latticeSquare.getCoordinateZLattice()
        );
        energyChartS = new EnergyChart();
        temperatureChartS = new TemperatureChart();
        kineticEnergyChartS = new KineticEnergyChart();
        potentialEnergyChartS = new PotentialEnergyChart();

        viewLatticeT = new LatticeView1(
            nx, ny, nz,
            latticeTriangle.getCoordinateXLattice(),
            latticeTriangle.getCoordinateYLattice(),
            latticeTriangle.getCoordinateZLattice()
        );
        energyChartT = new EnergyChart();
        temperatureChartT = new TemperatureChart();
        kineticEnergyChartT = new KineticEnergyChart();
        potentialEnergyChartT = new PotentialEnergyChart();

        // UI GRID: TABEL(ROW: 4, COLIS: 2)
        BorderPane root = new BorderPane();
        GridPane grid = new GridPane();

        grid.add(viewLatticeS.getSubScene(), 0, 0);
        grid.add(energyChartS.getChart(), 1, 0);
        grid.add(temperatureChartS.getChart(), 2,0);
        grid.add(kineticEnergyChartS.getChart(), 3, 0);
        grid.add(potentialEnergyChartS.getChart(), 4, 0);

        grid.add(viewLatticeT.getSubScene(), 0, 1);
        grid.add(energyChartT.getChart(), 1, 1);
        grid.add(temperatureChartT.getChart(), 2, 1);
        grid.add(kineticEnergyChartT.getChart(), 3, 1);
        grid.add(potentialEnergyChartT.getChart(), 4, 1);
        root.setCenter(grid);

        // Scene
        Scene scene = new Scene(root, widthWindow, heightWindow);
        stage.setScene(scene);
        stage.setTitle("Lattice Model");
        stage.show();

        // Append style line
        energyChartS.appendStyle();
        energyChartT.appendStyle();
        temperatureChartS.appendStyle();
        temperatureChartT.appendStyle();
        kineticEnergyChartS.appendStyle();
        kineticEnergyChartT.appendStyle();
        potentialEnergyChartS.appendStyle();
        potentialEnergyChartT.appendStyle();

        // Start Model Animation
        startSimulationLoop();
    }

    private void startSimulationLoop() {
        javafx.animation.AnimationTimer timer =
        new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                // 1. Step lattice
                latticeSquare.forwardModel();
                latticeTriangle.forwardModel();

                // 2. Update visualization
                viewLatticeS.setPositionsX(latticeSquare.getCoordinateXLattice());
                viewLatticeS.setPositionsY(latticeSquare.getCoordinateYLattice());
                viewLatticeS.setPositionsZ(latticeSquare.getCoordinateZLattice());
                viewLatticeT.setPositionsX(latticeTriangle.getCoordinateXLattice());
                viewLatticeT.setPositionsY(latticeTriangle.getCoordinateYLattice());
                viewLatticeT.setPositionsZ(latticeTriangle.getCoordinateZLattice());
                viewLatticeS.updatePositions();
                viewLatticeT.updatePositions();

                // 3. Update charts: Square, Triangle
                double kineticS = latticeSquare.getEnergyKinetic();
                double potentialS = latticeSquare.getEnergyPotential();
                double temperatureS = latticeSquare.getTemperatureSystem();
                double totalS = kineticS + potentialS;

                double kineticT = latticeTriangle.getEnergyKinetic();
                double potentialT = latticeTriangle.getEnergyPotential();
                double temperatureT = latticeTriangle.getTemperatureSystem();
                double totalT = kineticT + potentialT;

                energyChartS.addPoint(step, totalS);
                kineticEnergyChartS.addPoint(step, kineticS);
                temperatureChartS.addPoint(step, temperatureS);
                potentialEnergyChartS.addPoint(step, potentialS);

                energyChartT.addPoint(step, totalT);
                kineticEnergyChartT.addPoint(step, kineticT);
                temperatureChartT.addPoint(step, temperatureT);
                potentialEnergyChartT.addPoint(step, potentialT);
                step++;
            }
        };
        
        // Start Model
        timer.start();
    }

    public static void main(String[] args) {
        launch();
    }
}
