package org.example.server;

public interface DPoint {
    // Methods for providing data
    Long getId();
    long getStep();
    double getKineticEnergy();
    double getPotentialEnergy();
    double getTotalEnergy();
    double getTemperature();

    //  Methods for obtaining data
    void setId(Long id);
    void setStep(long step);
    void setKineticEnergy(double kineticEnergy);
    void setPotentialEnergy(double potentialEnergy);
    void setTotalEnergy(double totalEnergy);
    void setTemperature(double temperature);
    void setExperiment(Experiment experiment);
}
