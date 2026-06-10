package org.example.server;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpData {
    // Methods for providing data
    public Long getId();
    public String getMaterialName();
    public String getAlgorithmType();
    public LocalDateTime getCreatedAt();
    public List<DataPoint> getDataPoints();

    // Methods for obtaining data
    public void setId(Long id);
    public void setMaterialName(String materialName);
    public void setAlgorithmType(String algorithmType);
    public void setCreatedAt(LocalDateTime createdAt);
    public void setDataPoints(List<DataPoint> dataPoints);
}
