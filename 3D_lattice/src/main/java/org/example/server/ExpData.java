package org.example.server;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpData {
    // Methods for providing data
    Long getId();
    String getMaterialName();
    String getAlgorithmType();
    LocalDateTime getCreatedAt();
    List<DataPoint> getDataPoints();

    // Methods for obtaining data
    void setId(Long id);
    void setMaterialName(String materialName);
    void setAlgorithmType(String algorithmType);
    void setCreatedAt(LocalDateTime createdAt);
    void setDataPoints(List<DataPoint> dataPoints);
}
