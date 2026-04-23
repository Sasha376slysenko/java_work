package domain;

public enum LengthUnit implements Unit {
    // SI
    METER(1.0),
    KILOMETER(1000.0),
    CENTIMETER(0.01),

    // SGS
    MILLIMETER(0.001),

    // Imperial
    INCH(0.0254),
    FOOT(0.3048),
    YARD(0.9144),
    MILE(1609.34);

    private final double factor;

    LengthUnit(double factor) { this.factor = factor; }

    @Override
    public double toSi(double value) { return value * factor; }

    @Override
    public double fromSi(double value) { return value / factor; }
}
