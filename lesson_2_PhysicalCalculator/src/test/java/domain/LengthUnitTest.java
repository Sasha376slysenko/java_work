package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LengthUnitTest {
    /*
    * +---------------------------+
    * |Unit length to Si {Meter}. |
    * |1) Kilometer  => Meter.    |
    * |2) Centimeter => Meter.    |
    * |3) Millimeter => Meter.    |
    * |4) Inch       => Meter.    |
    * |5) Foot       => Meter.    |
    * |6) Yard       => Meter.    |
    * |7) Mile       => Meter.    |
    * +---------------------------+
    * */

    @Test
    void testKilometerToMeter() {
        double result = LengthUnit.KILOMETER.toSi(1);
        assertEquals(1e+3, result,
                0.0001, "Конвертація кілометри в метри не працює!");
    }

    @Test
    void testCentimeterToMeter() {
        double result = LengthUnit.CENTIMETER.toSi(1);
        assertEquals(0.01, result,
                0.0001, "Конвертація сантіметрів в метри не працює!");
    }

    @Test
    void testMillimeterToMeter() {
        double result = LengthUnit.MILLIMETER.toSi(1);
        assertEquals(1e-3, result,
                0.0001, "Конвертація міліметрів в метри не працює!");
    }

    @Test
    void testInchToMeter() {
        double result = LengthUnit.INCH.toSi(100);
        assertEquals(2.54, result,
                0.0001, "Конвертація дюмів в метри не працює!");
    }

    @Test
    void testFootToMeter() {
        double result = LengthUnit.FOOT.toSi(100);
        assertEquals(30.48, result,
                0.0001, "Конвертація футів в метри не працює!");
    }

    @Test
    void testYardToMeter() {
        double result = LengthUnit.YARD.toSi(50);
        assertEquals(45.72, result,
                0.0001, "Конвертація ярдів в метри не працює!");
    }

    @Test
    void testMileToMeter() {
        double result = LengthUnit.MILE.toSi(1);
        assertEquals(1609.34, result,
                0.0001, "Конвертація мілів в метри не працює!");
    }
}
