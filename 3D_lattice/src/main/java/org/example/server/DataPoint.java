package org.example.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class DataPoint implements DPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long step;
    private double kineticEnergy;
    private double potentialEnergy;
    private double totalEnergy;
    private double temperature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id")
    @JsonIgnore

    private Experiment experiment;
    public DataPoint() {}

    /*
    * +---------------------------+
    * |Methods for providing data.|
    * +---------------------------+
    */
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public long getStep() {
        return step;
    }

    @Override
    public double getKineticEnergy() {
        return kineticEnergy;
    }

    @Override
    public double getPotentialEnergy() {
        return potentialEnergy;
    }

    @Override
    public double getTotalEnergy() {
        return totalEnergy;
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    @JsonIgnore
    public Experiment getExperiment() {
        return experiment;
    }

    /*
    * +---------------------------+
    * |Methods for obtaining data.|
    * +---------------------------+
    */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setStep(long step) {
        this.step = step;
    }

    @Override
    public void setKineticEnergy(double kineticEnergy) {
        this.kineticEnergy = kineticEnergy;
    }

    @Override
    public void setPotentialEnergy(double potentialEnergy) {
        this.potentialEnergy = potentialEnergy;
    }

    @Override
    public void setTotalEnergy(double totalEnergy) {
        this.totalEnergy = totalEnergy;
    }

    @Override
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }
}
