public interface DPoint {
    // Methods for providing data
    public Long getId();
    public long getStep();
    public double getKineticEnergy();
    public double getPotentialEnergy();
    public double getTotalEnergy();
    public double getTemperature();
    public Experiment getExperiment();

    //  Methods for obtaining data
    public void setId(Long id);
    public void setStep(long step);
    public void setKineticEnergy(double kineticEnergy);
    public void setPotentialEnergy(double potentialEnergy);
    public void setTotalEnergy(double totalEnergy);
    public void setTemperature(double temperature);
    public void setExperiment(Experiment experiment);
}
