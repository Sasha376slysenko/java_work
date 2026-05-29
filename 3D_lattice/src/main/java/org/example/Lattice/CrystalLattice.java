package org.example.Lattice;

public class CrystalLattice implements LatticeMD {
    private int nx, ny, nz;
    private boolean flagSquareLattice = true;
    private boolean isFlagTriangleLattice = false;

    private double[] accelerationsX;
    private double[] accelerationsY;
    private double[] accelerationsZ;
    private double[] positionsX;
    private double[] positionsY;
    private double[] positionsZ;
    private double[] velocitiesX;
    private double[] velocitiesY;
    private double[] velocitiesZ;
    private double[] forcesX;
    private double[] forcesY;
    private double[] forcesZ;

    private int N;
    private double Lx;
    private double Ly;
    private double Lz;
    private double temperature;
    private double kineticEnergy;
    private double potentialEnergy;
    private double latticeConstant;
    private double temperatureSystem;

    // Mass atoms, step integration, k-Boltzmann
    private int stepCount = 0;
    private final double dt = 5e-4;
    private final double mass = 1.2;
    private final double k = 1.38;

    // LJ
    private final double k_spring = 50.0;
    private final double r_0 = 1.4;
    private final double rCut = 1.6;

    public CrystalLattice(int nx, int ny, int nz,
            int geometry, double latticeConstant) {
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.N = nx * ny * nz;
        this.latticeConstant = latticeConstant;
        this.Lx = nx * latticeConstant;
        this.Ly = ny * latticeConstant;
        this.Lz = nz * latticeConstant;

        this.accelerationsX = new double[N];
        this.accelerationsY = new double[N];
        this.accelerationsZ = new double[N];
        this.velocitiesX = new double[N];
        this.velocitiesY = new double[N];
        this.velocitiesZ = new double[N];
        this.positionsX = new double[N];
        this.positionsY = new double[N];
        this.positionsZ = new double[N];
        this.forcesX = new double[N];
        this.forcesY = new double[N];
        this.forcesZ = new double[N];

        if (geometry == 1) {
            this.flagSquareLattice = true;
            this.isFlagTriangleLattice = false;
        } else if (geometry == 2) {
            this.flagSquareLattice = false;
            this.isFlagTriangleLattice = true;
        } else {
            this.flagSquareLattice = true;
            this.isFlagTriangleLattice = true;
        }
    }

    @Override
    public void initTriangleLattice() {
        /*
         * +--------------------------------+
         * |Initialization Triangle lattice.|
         * +--------------------------------+
         */
        int A = 0;
        double a = latticeConstant;
        double sqrt_3_2 = Math.sqrt(3) / 2;

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                for (int k = 0; k < nz; k++) {
                    double spacing = 0.0;

                    if (j % 2 != 0) {
                        spacing = a / 2;
                    }

                    positionsX[A] = i * a + spacing;
                    positionsY[A] = j * a * sqrt_3_2;
                    positionsZ[A] = k * a;
                    A++;
                }
            }
        }
    }

    @Override
    public void initSquareLattice() {
        /*
         * +------------------------------+
         * |Initialization Square lattice.|
         * +------------------------------+
         */
        int A = 0;
        double a = latticeConstant;

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                for (int k = 0; k < nz; k++) {
                    positionsX[A] = i * a;
                    positionsY[A] = j * a;
                    positionsZ[A] = k * a;
                    A++;
                }
            }
        }
    }

    @Override
    public void initVelocities() {
        /*
         * +-----------------------------------+
         * |Initialization speed Max Boltzmann.|
         * +-----------------------------------+
         */
        double vxMean = 0.0;
        double vyMean = 0.0;
        double vzMean = 0.0;

        for (int i = 0; i < N; i++) {
            final double vxRandom = (Math.random() - 0.5) * 0.1;
            final double vyRandom = (Math.random() - 0.5) * 0.1;
            final double vzRandom = (Math.random() - 0.5) * 0.1;
            velocitiesX[i] = vxRandom;
            velocitiesY[i] = vyRandom;
            velocitiesZ[i] = vzRandom;
            vxMean += vxRandom;
            vyMean += vyRandom;
            vzMean += vzRandom;
        }

        vxMean /= N;
        vyMean /= N;
        vzMean /= N;

        for (int i = 0; i < N; i++) {
            velocitiesX[i] -= vxMean;
            velocitiesY[i] -= vyMean;
            velocitiesZ[i] -= vzMean;
        }
    }

    @Override
    public double computePotential(double r) {
        /*
         * +---------------------+
         * |Compute potential LJ.|
         * +---------------------+
         */
        double dr = r - r_0;
        return 0.5 * k_spring * dr * dr;
    }

    @Override
    public void computeEnergyKinetic() {
        /*
         * +-----------------------+
         * |Compute energy Kinetic.|
         * +-----------------------+
         */
        this.kineticEnergy = 0.0;

        for (int i = 0; i < N; i++) {
            double v_x = velocitiesX[i];
            double v_y = velocitiesY[i];
            double v_z = velocitiesZ[i];
            kineticEnergy += 0.5 * mass * (v_x * v_x + v_y * v_y + v_z * v_z);
        }
    }

    @Override
    public void computeEnergyPotential() {
        /*
         * +-------------------------+
         * |Compute Energy potential.|
         * +-------------------------+
         */
        double eps = 1e-8;
        this.potentialEnergy = 0.0;

        for (int A = 0; A < N; A++) {
            for (int B = A + 1; B < N; B++) {
                double dx = positionsX[B] - positionsX[A];
                double dy = positionsY[B] - positionsY[A];
                double dz = positionsZ[B] - positionsZ[A];

                if (dx > Lx / 2)
                    dx -= Lx;
                if (dy > Ly / 2)
                    dy -= Ly;
                if (dz > Lz / 2)
                    dz -= Lz;
                if (dx < -Lx / 2)
                    dx += Lx;
                if (dy < -Ly / 2)
                    dy += Ly;
                if (dz < -Lz / 2)
                    dz += Lz;

                double r_2 = dx * dx + dy * dy + dz * dz;
                double r = Math.sqrt(r_2);

                if (r < eps || r > rCut)
                    continue;
                potentialEnergy += computePotential(r);
            }
        }
    }

    @Override
    public void thermostat() {
        /*
         * +--------------------------------------+
         * |Thermostat: |
         * |1. Compute temperature system. |
         * |2. Check temperature system. |
         * |3. T_system > T => rescale velocities.|
         * +--------------------------------------+
         */
        double temp_i = getTempI();
        temperatureSystem = temp_i;

        if (Math.abs(temp_i - temperature) < 0.1)
            return;

        // Scale Temperature
        double tau = 0.1;
        double scale = Math.sqrt(1 + (dt / tau) * (temperature / temp_i - 1.0));

        for (int i = 0; i < N; i++) {
            velocitiesX[i] *= scale;
            velocitiesY[i] *= scale;
            velocitiesZ[i] *= scale;
        }
    }

    private double getTempI() {
        double temp_i = 0.0;
        double summaKineticEnergy = 0.0;

        for (int i = 0; i < N; i++) {
            double v_x = velocitiesX[i];
            double v_y = velocitiesY[i];
            double v_z = velocitiesZ[i];
            summaKineticEnergy += mass * (v_x * v_x + v_y * v_y + v_z * v_z);
        }
        temp_i = summaKineticEnergy / (3.0 * N * k);
        return temp_i;
    }

    @Override
    public void updateForce() {
        /*
         * +-----------------+
         * |Update force => 0|
         * +-----------------+
         */

        for (int i = 0; i < N; i++) {
            forcesX[i] = 0.0;
            forcesY[i] = 0.0;
            forcesZ[i] = 0.0;
        }
    }

    @Override
    public double forceGarmonik(double r_2) {
        /*
         * +---------------------------+
         * |Compute force Leonard Jonse|
         * +---------------------------+
         */
        double r = Math.sqrt(r_2);
        double dr = r - r_0;
        return -k_spring * dr / r;
    }

    @Override
    public void computeForce() {
        /*
         * +-------------------------------+
         * |Compute force => f_x, f_y, f_z.|
         * +-------------------------------+
         */
        updateForce();
        double eps = 1e-12;

        for (int A = 0; A < N; A++) {
            for (int B = A + 1; B < N; B++) {
                // dx, dy, dz
                double dx = positionsX[B] - positionsX[A];
                double dy = positionsY[B] - positionsY[A];
                double dz = positionsZ[B] - positionsZ[A];

                if (dx > Lx / 2)
                    dx -= Lx;
                if (dy > Ly / 2)
                    dy -= Ly;
                if (dz > Lz / 2)
                    dz -= Lz;
                if (dx < -Lx / 2)
                    dx += Lx;
                if (dy < -Ly / 2)
                    dy += Ly;
                if (dz < -Lz / 2)
                    dz += Lz;

                // Radiuse vector
                double r_2 = dx * dx + dy * dy + dz * dz;

                if (r_2 < eps || r_2 > rCut * rCut)
                    continue;
                double force = forceGarmonik(r_2);
                double fx = force * dx;
                double fy = force * dy;
                double fz = force * dz;

                // Add forces
                forcesX[A] -= fx;
                forcesY[A] -= fy;
                forcesZ[A] -= fz;

                forcesX[B] += fx;
                forcesY[B] += fy;
                forcesZ[B] += fz;
            }
        }
    }

    @Override
    public void computeAcceleration() {
        /*
         * +---------------------+
         * |Compute acceleration.|
         * +---------------------+
         */
        for (int i = 0; i < N; i++) {
            accelerationsX[i] = forcesX[i] / mass;
            accelerationsY[i] = forcesY[i] / mass;
            accelerationsZ[i] = forcesZ[i] / mass;
        }
    }

    @Override
    public void stepVelocityVerlet() {
        /*
         * +----------------------------------+
         * |Velocity Verlet Integration Step: |
         * |1. Update speed. |
         * |2. Update position. |
         * |3. Update force and acceleration. |
         * |4. Update speed. |
         * +----------------------------------+
         */

        // Step 1: Update speed.
        for (int i = 0; i < N; i++) {
            velocitiesX[i] += 0.5 * dt * accelerationsX[i];
            velocitiesY[i] += 0.5 * dt * accelerationsY[i];
            velocitiesZ[i] += 0.5 * dt * accelerationsZ[i];
        }

        // Step 2: Update position.
        for (int i = 0; i < N; i++) {
            positionsX[i] += dt * velocitiesX[i];
            positionsY[i] += dt * velocitiesY[i];
            positionsZ[i] += dt * velocitiesZ[i];

            if (positionsX[i] >= Lx)
                positionsX[i] -= Lx;
            if (positionsY[i] >= Ly)
                positionsY[i] -= Ly;
            if (positionsZ[i] >= Lz)
                positionsZ[i] -= Lz;

            if (positionsX[i] < 0)
                positionsX[i] += Lx;
            if (positionsY[i] < 0)
                positionsY[i] += Ly;
            if (positionsZ[i] < 0)
                positionsZ[i] += Lz;
        }

        // Step 3: Update force and acceleration.
        computeForce();
        computeAcceleration();

        // Step 4: Update speed.
        for (int i = 0; i < N; i++) {
            velocitiesX[i] += 0.5 * dt * accelerationsX[i];
            velocitiesY[i] += 0.5 * dt * accelerationsY[i];
            velocitiesZ[i] += 0.5 * dt * accelerationsZ[i];
        }
    }

    public void forwardModel() {
        /*
         * +----------------------------+
         * |Forward Model Lattice 3D: |
         * |1. Init Lattice. |
         * |2. Init Init v, F, a. |
         * |3. Step velocity verlet. |
         * |4. Thermostat. |
         * |5. Counter. |
         * +----------------------------+
         */

        if (stepCount == 0) {
            // 1. Init Lattice.
            if (flagSquareLattice) {
                initSquareLattice();
            } else if (isFlagTriangleLattice) {
                initTriangleLattice();
            } else {
                initSquareLattice();
            }

            // 2. Init v, F, a
            initVelocities();
            computeForce();
            computeAcceleration();
        }

        // 3. Step velocity Verlet
        stepVelocityVerlet();

        // 4.Thermostat
        thermostat();

        // 5. Counter
        stepCount++;
    }

    @Override
    public void setTemperature(double temp) {
        this.temperature = temp;
    }

    @Override
    public double[] getCoordinateXLattice() {
        return positionsX;
    }

    @Override
    public double[] getCoordinateYLattice() {
        return positionsY;
    }

    @Override
    public double[] getCoordinateZLattice() {
        return positionsZ;
    }

    @Override
    public double getTemperatureSystem() {
        return temperatureSystem;
    }

    @Override
    public double getEnergyKinetic() {
        computeEnergyKinetic();
        return kineticEnergy;
    }

    @Override
    public double getEnergyPotential() {
        computeEnergyPotential();
        return potentialEnergy;
    }
}
