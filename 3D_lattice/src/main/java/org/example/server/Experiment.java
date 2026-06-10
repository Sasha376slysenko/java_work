package org.example.server;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Experiment extends ExpData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String materialName;
    private String algorithmType;
    private LocalDateTime createdAt;

    @OneToMany(
        mappedBy = "experiment",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<DataPoint> dataPoints = new ArrayList<>();

    public Experiment() {
        this.createdAt = LocalDateTime.now();
    }

    public Experiment(String materialName, String algorithmType) {
        this();
        this.materialName = materialName;
        this.algorithmType = algorithmType;
    }

    public void addDataPoint(DataPoint dp) {
        dataPoints.add(dp);
        dp.setExperiment(this);
    }

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
    public String getMaterialName() {
        return materialName;
    }

    @Override
    public String getAlgorithmType() {
        return algorithmType;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public List<DataPoint> getDataPoints() {
        return dataPoints;
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
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    @Override
    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
