package org.example.Lattice;

public interface LatticeMD {
    // Lattice geometry
    void initVelocities();
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
    double forceGarmonik(double r_2);
    double computePotential(double r);

    // Set
    void setTemperature(double temp);

    // Get
    double[] getCoordinateXLattice();
    double[] getCoordinateYLattice();
    double[] getCoordinateZLattice();
    double getEnergyKinetic();
    double getEnergyPotential();
    double getTemperatureSystem();
}
