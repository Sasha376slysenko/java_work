package org.example;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.CheckBox;
import javafx.application.Application;

import org.example.Lattice.CrystalLattice;
import org.example.Visualizer.EnergyChart;
import org.example.Visualizer.LatticeView1;
import org.example.Visualizer.TemperatureChart;

import org.example.server.ServerApp;
import org.example.server.Experiment;
import org.example.server.DataPoint;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Main extends Application {
    private EnergyChart energyChartS;
    private EnergyChart energyChartT;
    private LatticeView1 viewLatticeS;
    private LatticeView1 viewLatticeT;
    private CrystalLattice latticeSquare;
    private CrystalLattice latticeTriangle;
    private TemperatureChart temperatureChartS;
    private TemperatureChart temperatureChartT;

    // Window Geometry and counter RUN
    private long step = 0;
    private final int widthWindow = 1800;
    private final int heightWindow = 900;

    // Type
    private final int typeS = 1;
    private final int typeT = 2;

    private CheckBox cbDemon;
    private CheckBox cbVerlet;
    private CheckBox cbMetropolis;

    // Experiments for backend
    private Experiment expSquare;
    private Experiment expTriangle;

    private void createLattice() {
        /*
        * +--------------------------------+
        * |Create Lattice:                 |
        * |1. Create Square lattice.       |
        * |2. Create Triangle lattice.     |
        * |3. Create View Square lattice.  |
        * |4. Create View Triangle lattice.|
        * +--------------------------------+
        * */
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
    }

    private String getSelectedAlgorithm() {
        /*
        * +-------------------------+
        * |Selected name algorithm: |
        * |1. Velocity Verlet.      |
        * |2. Metropolis.           |
        * |3. Demon Karmen.         |
        * +-------------------------+
        */
        if (cbVerlet.isSelected() && !cbMetropolis.isSelected() && !cbDemon.isSelected())
            return "Verlet";
        if (!cbVerlet.isSelected() && cbMetropolis.isSelected() && !cbDemon.isSelected())
            return "Metropolis";
        if (!cbVerlet.isSelected() && !cbMetropolis.isSelected() && cbDemon.isSelected())
            return "Demon";
        
        if (cbVerlet.isSelected() && !cbMetropolis.isSelected() && cbDemon.isSelected())
            return "Verlet + Demon";
        if (cbVerlet.isSelected() && cbMetropolis.isSelected() && !cbDemon.isSelected())
            return "Verlet + Metropolis";
        if (!cbVerlet.isSelected() && cbMetropolis.isSelected() && cbDemon.isSelected())
            return "Metropolis + Demon";
        if (cbVerlet.isSelected() && cbMetropolis.isSelected() && cbDemon.isSelected())
            return "All (Verlet + Metropolis + Demon)";

        return "Не обрано";
    }

    private void createCheckBox() {
        /*
        * +--------------------------+
        * |Create CheckBox algorithm:|
        * |1. Velocity Verlet.       |
        * |2. Metropolis.            |
        * |3. Demon.                 |
        * +--------------------------+
        * */
        final String styleCheckBox = "-fx-text-fill: black;";

        cbDemon = new CheckBox("Алгоритм Demon");
        cbVerlet = new CheckBox("Алгоритм Verlet");
        cbMetropolis = new CheckBox("Алгоритм Metropolis");

        cbVerlet.setSelected(true);
        cbDemon.setStyle(styleCheckBox);
        cbVerlet.setStyle(styleCheckBox);
        cbMetropolis.setStyle(styleCheckBox);
    }

    @Override
    public void start(Stage stage) {
        /*
        * +----------------------------------+
        * |Start Model:                      |
        * |1. Create lattice.                |
        * |2. Create CheckBox.               |
        * |3. Create GridPane => Table(View).|
        * |4. Create Scene.                  |
        * |5. Chart append style.            |
        * |6. Start Animation => Timer.      |
        * +----------------------------------+
        */
        createLattice();
        createCheckBox();
        
        // Initialization name algorithm
        expSquare = new Experiment("Square", getSelectedAlgorithm());
        expTriangle = new Experiment("Triangle", getSelectedAlgorithm());

        // UI GRID: TABLE (ROW: 4, COLIS: 2)
        BorderPane root = new BorderPane();
        GridPane grid = new GridPane();

        GridPane.setMargin(cbMetropolis, new Insets(0, 0, 5, 0));
        GridPane.setMargin(cbVerlet, new Insets(0, 0, 5, 0));
        GridPane.setMargin(cbDemon, new Insets(0, 0, 5, 0));

        grid.add(viewLatticeS.getSubScene(), 0, 0);
        grid.add(energyChartS.getContainer(), 1, 0);
        grid.add(temperatureChartS.getChart(), 2,0);

        grid.add(viewLatticeT.getSubScene(), 0, 1);
        grid.add(energyChartT.getContainer(), 1, 1);
        grid.add(temperatureChartT.getChart(), 2, 1);
        grid.add(cbMetropolis, 1, 2);
        grid.add(cbVerlet, 0, 2);
        grid.add(cbDemon, 2, 2);
        root.setCenter(grid);

        // Scene
        Scene scene = new Scene(root, widthWindow, heightWindow);
        stage.setTitle("Lattice Model");
        stage.setScene(scene);
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
        /*
        * +------------------------------------+
        * |Start animation model:              |
        * |1. Selected algorithm.              |
        * |2. Update visualization.            |
        * |3. Update charts: Square, Triangle. |
        * |4. Saved point lattice.             |
        * +------------------------------------+
        */
        final int MAX_ITR = 5_000;

        javafx.animation.AnimationTimer timer =
        new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (step >= MAX_ITR) {
                    System.out.println("Симуляцію завершено. Відправляємо дані на сервер...");
                    this.stop();

                    expSquare.setAlgorithmType(getSelectedAlgorithm());
                    expTriangle.setAlgorithmType(getSelectedAlgorithm());
                    sendDataToBackend(expSquare);
                    sendDataToBackend(expTriangle);
                    return;
                }

                // 1. Selected algorithm
                if (cbVerlet.isSelected()) {
                    latticeSquare.forwardModel();
                    latticeTriangle.forwardModel();
                }
                if (cbMetropolis.isSelected()) {
                    latticeSquare.forwardMetropolisSystem();
                    latticeTriangle.forwardMetropolisSystem();
                }
                if (cbDemon.isSelected()) {
                    latticeSquare.forwardDemonSystem();
                    latticeTriangle.forwardDemonSystem();
                }

                if (step % 30 == 0) {
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

                    // 4. Saved point lattice
                    DataPoint dpS = new DataPoint();
                    dpS.setStep(step);
                    dpS.setKineticEnergy(kineticS);
                    dpS.setPotentialEnergy(potentialS);
                    dpS.setTotalEnergy(totalS);
                    dpS.setTemperature(temperatureS);
                    expSquare.addDataPoint(dpS);

                    DataPoint dpT = new DataPoint();
                    dpT.setStep(step);
                    dpT.setKineticEnergy(kineticT);
                    dpT.setPotentialEnergy(potentialT);
                    dpT.setTotalEnergy(totalT);
                    dpT.setTemperature(temperatureT);
                    expTriangle.addDataPoint(dpT);
                }
                step++;
            }
        };

        // Start Model
        timer.start();
    }

    private void sendDataToBackend(Experiment exp) {
        CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                String json = mapper.writeValueAsString(exp);
                
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/experiments"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
                
                client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("SUCCESS DATA");
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        });
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        new Thread(() -> ServerApp.runServer(args)).start();
        launch();
    }
}
