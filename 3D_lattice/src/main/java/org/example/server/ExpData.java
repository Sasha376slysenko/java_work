public interface ExpData {
    // Methods for providing data
    public Long getId();
    public getMaterialName();
    public getAlgorithmType();
    public LocalDateTime getCreatedAt();
    public List<DataPoint> getDataPoints();

    // Methods for obtaining data
    public void setId(Long id);
    public void setMaterialName(String materialName);
    public void setAlgorithmType(String algorithmType);
    public void setCreatedAt(LocalDateTime createdAt);
    public void setDataPoints(List<DataPoint> dataPoints);
}
