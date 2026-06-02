package org.example.Lattice;

public class CrystalLattice implements LatticeMD {
    private double[] accelerationsX;
    private double[] accelerationsY;
    private double[] accelerationsZ;
    private double[] arrDeltaCoor;
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
    private int geometry;
    private int nx, ny, nz;
    private double temperature;
    private double kineticEnergy;
    private double potentialEnergy;
    private double latticeConstant;
    private double temperatureSystem;

    // Mass atoms, step integration, ...
    private double rCut;
    private final double dt = 2e-4;     // CI: ps
    private final double mass = 63.546; // CI: a.o.m
    private final double k = 8.6173e-5; // CI: eV / K

    // Variables demon's algorithm
    private double EDemon = 1.5;
    private boolean flagDemon = false;
    private double summaEDemon = 0.0;
    private int demonStepCounter = 0;

    // Morse potential
    private final double De = 2.3;      // H, CI: eV
    private final double r_0 = 2.55;    // r_qe, CI: A
    private final double a_morse = 1.8; // W, CI: A^(-1)

    // Const convertation: (eV / A) -> (A / ps^2)
    private final double forceToAccel = 9648.533;

    public CrystalLattice(
        int nx,
        int ny,
        int nz,
        double rCut,
        int geometry,
        double temperature,
        double latticeConstant){
        // Lattice parameters
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.geometry = geometry;
        this.temperature = temperature;
        this.latticeConstant = latticeConstant;

        // Box parameter and radius cut
        this.Lx = nx * latticeConstant;
        this.Ly = ny * latticeConstant;
        this.Lz = nz * latticeConstant;
        this.rCut = rCut;

        // RUN MODEL
        initNumberAtoms();
        initVelocities();
        computeForce();
        computeAcceleration();
        thermostat();
        stabilizeSystem();
    }

    private void initSizeArrays(int N) {
        /*
        * +-----------------------+
        * |Init size arrays model.|
        * +-----------------------+
        */
        this.accelerationsX = new double[N];
        this.accelerationsY = new double[N];
        this.accelerationsZ = new double[N];
        this.arrDeltaCoor = new double[3];
        this.velocitiesX = new double[N];
        this.velocitiesY = new double[N];
        this.velocitiesZ = new double[N];
        this.positionsX = new double[N];
        this.positionsY = new double[N];
        this.positionsZ = new double[N];
        this.forcesX = new double[N];
        this.forcesY = new double[N];
        this.forcesZ = new double[N];
    }

    private void stabilizeSystem() {
        /*
        * +--------------------+
        * |System stabilization|
        * +--------------------+
        */
        int steps = 10;

        for (int i = 0; i < steps; i++) {
            stepVelocityVerlet();;
            double temp_i = getTempI();

            if (temp_i > 1e-3) {
                double scale = Math.sqrt(temperature / temp_i);
                
                if (scale > 1.2) scale = 1.2;
                if (scale < 0.8) scale = 0.8;

                for (int j = 0; j < N; j++) {
                    velocitiesX[j] *= scale;
                    velocitiesY[j] *= scale;
                    velocitiesZ[j] *= scale;
                }
            }
        }
    }

    @Override
    public void initNumberAtoms() {
        // init Size arrays
        if (geometry == 1) {
            this.N = nx * ny * nz;
            initSizeArrays(N);
            initSquareLattice();
        } else if (geometry == 2) {
            this.N = nx * ny * nz;
            initSizeArrays(N);
            initTriangleLattice();
        } else if (geometry == 3) {
            this.N = nx * ny * nz * 2;
            initSizeArrays(N);
            initBCCLattice();
        } else {
            this.N = nx * ny * nz;
            initSizeArrays(N);
            initSquareLattice();
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
    public void initBCCLattice() {
        /*
        * +----------------+
        * |Init FCC Lattice|
        * +----------------+
        */
        int A = 0;
        double a = latticeConstant;
        double halfA = latticeConstant / 2;

        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                for (int k = 0; k < nz; k++) {
                    // first atom
                    positionsX[A] = i * a;
                    positionsY[A] = j * a;
                    positionsZ[A] = k * a;
                    A++;

                    // second atom
                    positionsX[A] = i * a + halfA;
                    positionsY[A] = j * a + halfA;
                    positionsZ[A] = k * a + halfA;
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
            final double vxRandom = (Math.random() - 0.5) * 0.05;
            final double vyRandom = (Math.random() - 0.5) * 0.05;
            final double vzRandom = (Math.random() - 0.5) * 0.05;
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

        for (int i = 0; i < N; i++) {
            positionsX[i] += (Math.random() - 0.5) * 0.005;
            positionsY[i] += (Math.random() - 0.5) * 0.005;
            positionsZ[i] += (Math.random() - 0.5) * 0.005;

            if (positionsX[i] > Lx) positionsX[i] -= Lx;
            if (positionsX[i] < 0) positionsX[i] += Lx;
            if (positionsY[i] > Ly) positionsY[i] -= Ly;
            if (positionsY[i] < 0) positionsY[i] += Ly;
            if (positionsZ[i] > Lz) positionsZ[i] -= Lz;
            if (positionsZ[i] < 0) positionsZ[i] += Lz;
        }
    }

    private void checkDeltaCoor(double dx, double dy, double dz) {
        /*
        * +------------------------+
        * |Check delta coordinate: |
        * |1. Delta X.             |
        * |2. Delta Y.             |
        * |3. Delta Z.             |
        * +------------------------+
        */
        if (dx > Lx / 2)
            dx -= Lx;
        else if (dx < -Lx / 2)
            dx += Lx;

        if (dy > Ly / 2)
            dy -= Ly;
        else if (dy < -Ly / 2)
            dy += Ly;

        if (dz > Lz / 2)
            dz -= Lz;
        else if (dz < -Lz / 2)
            dz += Lz;
        
        arrDeltaCoor[0] = dx;
        arrDeltaCoor[1] = dy;
        arrDeltaCoor[2] = dz;
    } 

    @Override
    public double computePotential(double r_2) {
        /*
         * +------------------------+
         * |Compute potential Morse.|
         * +------------------------+
         */
        double r = Math.sqrt(r_2);
        double expTerm = Math.exp(-a_morse * (r - r_0));
        return De * ((1.0 - expTerm) * (1.0 - expTerm) - 1.0);
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
        kineticEnergy /= (2.0 * forceToAccel);
    }

    @Override
    public void computeEnergyPotential() {
        /*
         * +-------------------------+
         * |Compute Energy potential.|
         * +-------------------------+
         */
        double eps = 1e-12;
        this.potentialEnergy = 0.0;

        for (int A = 0; A < N; A++) {
            for (int B = A + 1; B < N; B++) {
                // dx, dy, dz
                double dx = positionsX[B] - positionsX[A];
                double dy = positionsY[B] - positionsY[A];
                double dz = positionsZ[B] - positionsZ[A];

                checkDeltaCoor(dx, dy, dz);
                dx = arrDeltaCoor[0];
                dy = arrDeltaCoor[1];
                dz = arrDeltaCoor[2];

                // radius vector
                double r_2 = dx * dx + dy * dy + dz * dz;

                if (r_2 < eps || r_2 > rCut * rCut)
                    continue;
                potentialEnergy += computePotential(r_2);
            }
        }
    }

    @Override
    public void thermostat() {
        /*
         * +--------------------------------------+
         * |Thermostat:                           |
         * |1. Compute temperature system.        |
         * |2. Check temperature system.          |
         * |3. T_system > T => rescale velocities.|
         * +--------------------------------------+
         */
        double temp_i = getTempI();
        temperatureSystem = temp_i;

        if (temp_i < 1e-4) return;
        double scale = Math.sqrt(temperature / temp_i);

        // Min, Max scale
        if (scale > 1.05) scale = 1.05;
        if (scale < 0.95) scale = 0.95;

        for (int i = 0; i < N; i++) {
            velocitiesX[i] *= scale;
            velocitiesY[i] *= scale;
            velocitiesZ[i] *= scale;
        }
    }

    private double getTempI() {
        /*
        * +---------------------------+
        * |Compute temperature system.|
        * +---------------------------+
        */
        double temp_i = 0.0;
        double summaKineticEnergy = 0.0;

        for (int i = 0; i < N; i++) {
            double v_x = velocitiesX[i];
            double v_y = velocitiesY[i];
            double v_z = velocitiesZ[i];
            summaKineticEnergy += mass * (v_x * v_x + v_y * v_y + v_z * v_z);
        }
        summaKineticEnergy /= 2.0 * forceToAccel;
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
    public double forceMorse(double r_2) {
        /*
         * +-------------------+
         * |Compute force Morse|
         * +-------------------+
         */
        double r = Math.sqrt(r_2);
        if (r < 1.5) return 5000.0 / r;

        double expTerm = Math.exp(-a_morse * (r - r_0));
        double force = 2.0 * a_morse * De * (expTerm * expTerm - expTerm);
        force /= r;

        final double MAX_POINTS = 1000.0;
        if (force > MAX_POINTS) force = MAX_POINTS;
        if (force < -MAX_POINTS) force = -MAX_POINTS;
        return force;
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

                checkDeltaCoor(dx, dy, dz);
                dx = arrDeltaCoor[0];
                dy = arrDeltaCoor[1];
                dz = arrDeltaCoor[2];

                // Radiuse vector
                double r_2 = dx * dx + dy * dy + dz * dz;

                if (r_2 < eps || r_2 > rCut * rCut)
                    continue;
                double force = forceMorse(r_2);
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
            accelerationsX[i] = (forcesX[i] / mass) * forceToAccel;
            accelerationsY[i] = (forcesY[i] / mass) * forceToAccel;
            accelerationsZ[i] = (forcesZ[i] / mass) * forceToAccel;
        }
    }

    @Override
    public void stepVelocityVerlet() {
        /*
         * +----------------------------------+
         * |Velocity Verlet Integration Step: |
         * |1. Update speed.                  |
         * |2. Update position.               |
         * |3. Update force and acceleration. |
         * |4. Update speed.                  |
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

            positionsX[i] = (positionsX[i] % Lx + Lx) % Lx;
            positionsY[i] = (positionsY[i] % Ly + Ly) % Ly;
            positionsZ[i] = (positionsZ[i] % Lz + Lz) % Lz;
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

    @Override
    public void forwardModel() {
        /*
         * +--------------------------+
         * |Forward Model Lattice 3D: |
         * |3. Step velocity verlet.  |
         * |4. Thermostat.            |
         * +--------------------------+
         */

        // Demon or Molecular dynamics
        flagDemon = false;

        // 1. Step velocity Verlet
        stepVelocityVerlet();

        // 2.Thermostat
        thermostat();
    }

    private void rescaleVelocityMCTemp() {
        /*
        * +---------------------------+
        * |Metropolis algorithm (III):|
        * |Compute temp => thermostat.|
        * +---------------------------+
        */
        double temp_i = getTempI();

        if (temp_i < 1e-4) {
            initVelocities();
            temp_i = getTempI();
        }
        double scale = Math.sqrt(temperatureSystem / temp_i);

        for (int i = 0; i < N; i++) {
            velocitiesX[i] *= scale;
            velocitiesY[i] *= scale;
            velocitiesZ[i] *= scale;
        }
    }

    private double computeLocalPotential(int atomA) {
        /*
        * +---------------------------+
        * |Metropolis algorithm (II): |
        * |Compute local potential.   |
        * +---------------------------+
        */
        double energy = 0.0;
        double eps = 1e-12;

        for (int atomB = 0; atomB < N; atomB++) {
            if (atomA == atomB) continue;

            double dx = positionsX[atomB] - positionsX[atomA];
            double dy = positionsY[atomB] - positionsY[atomA];
            double dz = positionsZ[atomB] - positionsZ[atomA];

            checkDeltaCoor(dx, dy, dz);
            dx = arrDeltaCoor[0];
            dy = arrDeltaCoor[1];
            dz = arrDeltaCoor[2];

            double r_2 = dx * dx + dy * dy + dz * dz;
            if (r_2 < eps || r_2 > rCut * rCut)
                continue;
            energy += computePotential(r_2);
        }
        return energy;
    }

    @Override
    public void forwardMetropolisSystem() {
        /*
        * +---------------------------------------+
        * |Metropolis algorithm (I):              |
        * |1. Random atom.                        |
        * |2. Compute energy old and old position.|
        * |3. Atomic displacement and new energy. |
        * |4. Criterion check.                    |
        * |5. Return atomic displacment.          |
        * |6. Compute energy kinetick.            |
        * |7. Thermostat.                         |
        * +---------------------------------------+
        */
        double deltaMax = 0.05;
        computeEnergyKinetic();
        double totalEPBefore = getEnergyPotential();

        for (int step = 0; step < N; step++) {
            // Step 1: Random atom
            int randomAtom = (int)(Math.random() * N);

            // Step 2: Compute energy old and old position
            double eOld = computeLocalPotential(randomAtom);
            double oldX = positionsX[randomAtom];
            double oldY = positionsY[randomAtom];
            double oldZ = positionsZ[randomAtom];

            // Step 3: Atomic displacement and new energy
            positionsX[randomAtom] += (Math.random() - 0.5) * deltaMax;
            positionsY[randomAtom] += (Math.random() - 0.5) * deltaMax;
            positionsZ[randomAtom] += (Math.random() - 0.5) * deltaMax;

            positionsX[randomAtom] = (positionsX[randomAtom] % Lx + Lx) % Lx;
            positionsY[randomAtom] = (positionsY[randomAtom] % Ly + Ly) % Ly;
            positionsZ[randomAtom] = (positionsZ[randomAtom] % Lz + Lz) % Lz;
            double eNew = computeLocalPotential(randomAtom);

            // Step 4: Criterion check
            double deltaE = eNew - eOld;
            boolean accept = false;

            if (deltaE < 0) {
                accept = true;
            }else {
                double factor = Math.exp(-deltaE / (k * temperature));
                if (Math.random() < factor) accept = true;
            }

            // Step 5: Return atomic displacment
            if (!accept) {
                positionsX[randomAtom] = oldX;
                positionsY[randomAtom] = oldY;
                positionsZ[randomAtom] = oldZ;
            }
        }
        
        // Step 6: Compute energy kinetick
        double totalEPAfter = getEnergyPotential();
        double deltaSystemEP = totalEPAfter - totalEPBefore;
        double targKineticEnergy = (3.0 / 2.0) * N * k * temperature;

        kineticEnergy = kineticEnergy - deltaSystemEP;
        kineticEnergy = kineticEnergy + 0.05 * (targKineticEnergy - kineticEnergy);
        temperatureSystem = (2.0 * kineticEnergy) / (3.0 * N * k);

        // Step 7: Thermostat
        rescaleVelocityMCTemp();
    }

    @Override
    public void forwardDemonSystem() {
        /*
        * +---------------------------------------+
        * |Demon algorithm:                       |
        * |1. Random atom.                        |
        * |2. Compute energy old and old position.|
        * |3. Atomic displacement and new energy. |
        * |4. Criterion check.                    |
        * |5. Return atomic displacment.          |
        * |6. Compute average temperature.        |
        * +---------------------------------------+
        */
        flagDemon = true;
        double deltaMax = 0.05;

        for (int step = 0; step < N; step++) {
            // Step 1: Random atom.
            int randomAtom = (int)(Math.random() * N);

            // Step 2: Compute energy old and old position.
            double eOld = computeLocalPotential(randomAtom);
            double oldX = positionsX[randomAtom];
            double oldY = positionsY[randomAtom];
            double oldZ = positionsZ[randomAtom];

            // Step 3: Atomic displacement and new energy.
            positionsX[randomAtom] += (Math.random() - 0.5) * deltaMax;
            positionsY[randomAtom] += (Math.random() - 0.5) * deltaMax;
            positionsZ[randomAtom] += (Math.random() - 0.5) * deltaMax;

            positionsX[randomAtom] = (positionsX[randomAtom] % Lx + Lx) % Lx;
            positionsY[randomAtom] = (positionsY[randomAtom] % Ly + Ly) % Ly;
            positionsZ[randomAtom] = (positionsZ[randomAtom] % Lz + Lz) % Lz;
            double eNew = computeLocalPotential(randomAtom);

            // Step 4: Criterion check.
            double deltaE = eNew - eOld;
            boolean accept = false;

            if (deltaE < 0) {
                accept = true;
                EDemon -= deltaE;
            } else {
                if (EDemon >= deltaE) {
                    accept = true;
                    EDemon -= deltaE;
                } else {
                    accept = false;
                }
            }

            // Step 5: Return atomic displacment.
            if (!accept) {
                positionsX[randomAtom] = oldX;
                positionsY[randomAtom] = oldY;
                positionsZ[randomAtom] = oldZ;
            }
        }

        // Step 6: Compute average temperature.
        demonStepCounter++;
        summaEDemon += EDemon;
        double meanEDemon = summaEDemon / demonStepCounter;
        temperatureSystem = meanEDemon / k;
        kineticEnergy = EDemon;
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
        if (!flagDemon) computeEnergyKinetic();
        return kineticEnergy;
    }

    @Override
    public double getEnergyPotential() {
        computeEnergyPotential();
        return potentialEnergy;
    }
}
