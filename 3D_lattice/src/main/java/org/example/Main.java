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

public class Main extends Application {
    private EnergyChart energyChartS;
    private EnergyChart energyChartT;
    private LatticeView1 viewLatticeS;
    private LatticeView1 viewLatticeT;
    private CrystalLattice latticeSquare;
    private CrystalLattice latticeTriangle;
    private TemperatureChart temperatureChartS;
    private TemperatureChart temperatureChartT;

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
        int nx = 3;
        int ny = 3;
        int nz = 3;
        double rCut = 5.5;
        double temp = 300.0;
        double latticeParameter1 = 2.944;
        double latticeParameter2 = 2.55;

        latticeSquare = new CrystalLattice(
            nx, ny, nz,
            rCut, typeS,
            temp, latticeParameter1
        );
        latticeTriangle = new CrystalLattice(
            nx, ny, nz,
            rCut, typeT,
            temp, latticeParameter2
        );

        viewLatticeS = new LatticeView1(
            nx, ny, nz, latticeParameter1,
            latticeSquare.getCoordinateXLattice(),
            latticeSquare.getCoordinateYLattice(),
            latticeSquare.getCoordinateZLattice()
        );
        energyChartS = new EnergyChart();
        temperatureChartS = new TemperatureChart();

        viewLatticeT = new LatticeView1(
            nx, ny, nz, latticeParameter2,
            latticeTriangle.getCoordinateXLattice(),
            latticeTriangle.getCoordinateYLattice(),
            latticeTriangle.getCoordinateZLattice()
        );
        energyChartT = new EnergyChart();
        temperatureChartT = new TemperatureChart();

        // UI GRID: TABEL(ROW: 4, COLIS: 2)
        BorderPane root = new BorderPane();
        GridPane grid = new GridPane();

        grid.add(viewLatticeS.getSubScene(), 0, 0);
        grid.add(energyChartS.getContainer(), 1, 0);
        grid.add(temperatureChartS.getChart(), 2,0);

        grid.add(viewLatticeT.getSubScene(), 0, 1);
        grid.add(energyChartT.getContainer(), 1, 1);
        grid.add(temperatureChartT.getChart(), 2, 1);
        root.setCenter(grid);

        // Scene
        Scene scene = new Scene(root, widthWindow, heightWindow);
        stage.setScene(scene);
        stage.setTitle("Lattice Model");
        stage.show();

        // Append style line
        energyChartS.appendStyleEK();
        energyChartS.appendStyleEP();
        energyChartS.appendStyleET();
        energyChartT.appendStyleEK();
        energyChartT.appendStyleEP();
        energyChartT.appendStyleET();
        temperatureChartS.appendStyle();
        temperatureChartT.appendStyle();

        // Start Model Animation
        startSimulationLoop();
    }

    private void startSimulationLoop() {
        javafx.animation.AnimationTimer timer =
        new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                // 1. Step lattice
                // latticeSquare.forwardModel();
                // latticeTriangle.forwardModel()
                latticeSquare.forwardMetropolisSystem();
                latticeTriangle.forwardMetropolisSystem();

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

                energyChartS.addPointET(step, totalS);
                energyChartS.addPointEK(step, kineticS);
                energyChartS.addPointEP(step, potentialS);
                temperatureChartS.addPoint(step, temperatureS);

                energyChartT.addPointET(step, totalT);
                energyChartT.addPointEK(step, kineticT);
                energyChartT.addPointEP(step, potentialT);
                temperatureChartT.addPoint(step, temperatureT);
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
