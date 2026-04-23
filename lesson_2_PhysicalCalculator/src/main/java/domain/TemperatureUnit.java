package domain;

public enum TemperatureUnit implements Unit {
    CELSIUS {
        @Override
        public double toSi(double value) { return value + 273.15; }
        public double fromSi(double value) { return value - 273.15; }
    },

    KELVIN {
        @Override
        public double toSi(double value) { return value; }
        public double fromSi(double value) { return value; }
    },

    FAHRENHEIT {
        @Override
        public double toSi(double value) { return 9.0 / 5.0 * (value - 32); }
        public double fromSi(double value) { return 9.0 / 5.0 * (value - 273.15) + 32.0; }
    };
}
