package org.example.Lattice;

public interface LatticeMDS {
    // Lattice geometry
    void initVelocities();
    void initBCCLattice();
    void initNumberAtoms();
    void initSquareLattice();
    void initTriangleLattice();

    // Compute
    void thermostat();
    void updateForce();
    void computeForce();
    void stepVelocityVerlet();
    void computeAcceleration();
    void computeEnergyKinetic();
    void computeEnergyPotential();
    double forceMorse(double r_2);
    double computePotential(double r);

    // Model run
    void forwardModel();
    void forwardDemonSystem();
    void forwardMetropolisSystem();

    // Get
    double[] getCoordinateXLattice();
    double[] getCoordinateYLattice();
    double[] getCoordinateZLattice();
    double getEnergyKinetic();
    double getEnergyPotential();
    double getTemperatureSystem();
}
