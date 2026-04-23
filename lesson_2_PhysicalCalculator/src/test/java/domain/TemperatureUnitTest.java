package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemperatureUnitTest {
    /*
     * +---------------------------------+
     * |Unit temperature to Si {Kelvin}. |
     * |1) Celsius => Kelvin.            |
     * |2) Kelvin => Kelvin.             |
     * |3) Fahrenheit => Kelvin.         |
     * +---------------------------------+
     */

    @Test
    void testCelsiusToKelvin() {
        double result = TemperatureUnit.CELSIUS.toSi(374.15);
        assertEquals(647.3, result,
                0.0001, "Конвертація цельсія в кельвін не працьює!");
    }

    @Test
    void testKelvinToKelvin() {
        double result = TemperatureUnit.KELVIN.toSi(273.15);
        assertEquals(273.15, result,
                0.0001, "Конвертація кельвіна в кельвін не працює!");
    }

    @Test
    void testFahrenheitToKelvin() {
        double result = TemperatureUnit.FAHRENHEIT.toSi(32.0);
        assertEquals(0.0, result,
                0.0001, "Конвертація фаренйет в цельсія не працює!");
    }

    /*
     * +-----------------------------------+
     * |Unit temperature from Si {Kelvin}. |
     * |1) Kelvin => Celsius.              |
     * |2) Kelvin => Kelvin.               |
     * |3) Kelvin => Fahrenheit.           |
     * +-----------------------------------+
     */

    @Test
    void testKelvinFromCelsius() {
        double result = TemperatureUnit.CELSIUS.fromSi(275.15);
        assertEquals(2.0, result,
                0.0001, "Конвертація кельвін в цельсія не працьює!");
    }

    @Test
    void testKelvinFromKelvin() {
        double result = TemperatureUnit.KELVIN.fromSi(273.15);
        assertEquals(273.15, result,
                0.0001, "Конвертація кельвіна в кельвін не працює!");
    }

    @Test
    void testKelvinFromFahrenheit() {
        double result = TemperatureUnit.FAHRENHEIT.fromSi(0.0);
        assertEquals(-459.67, result,
                0.0001, "Конвертація кельвін в фаренгейт не працює!");
    }
}
