package domain;

public enum TimeUnit implements Unit {
    SECOND(1.0),
    MINUTE(60.0),
    HOUR(3600.0);

    private final double factor;

    TimeUnit(double factor) {
        this.factor = factor;
    }

    @Override
    public double toSi(double value) { return value * factor; }

    @Override
    public double fromSi(double value) { return value / factor; }
}
