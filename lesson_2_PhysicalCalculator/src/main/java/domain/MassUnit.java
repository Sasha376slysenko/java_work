package domain;

public enum MassUnit implements Unit {
    KILOGRAM(1.0),
    GRAM(0.001),

    // SGS
    MILLIGRAM(1e-6),

    // imperial
    POUND(0.453592),
    OUNCE(0.0283495);

    private final double factor;

    MassUnit(double factor) { this.factor = factor; }

    @Override
    public double toSi(double value) { return value * factor; }

    @Override
    public double fromSi(double value) { return value / factor; }
}
