package domain;

public class UnitConverter {
    public static Unit[] getUnits(UnitCategory category) {
        return switch (category) {
            case LENGTH -> LengthUnit.values();
            case MASS -> MassUnit.values();
            case TIME -> TimeUnit.values();
            default -> TemperatureUnit.values();
        };
    }
}
