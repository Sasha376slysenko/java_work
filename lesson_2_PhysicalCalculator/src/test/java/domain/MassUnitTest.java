package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MassUnitTest {
    /*
    * +----------------------------+
    * |Unit mass to Si {Kilogram}. |
    * |1. Gram      => Kilogram.   |
    * |2. Milligram => Kilogram.   |
    * |3. Pound     => Kilogram.   |
    * |4. Ounce     => Kilogram.   |
    * +----------------------------+
    * */

    @Test
    void testGramToKilogram() {
        double result = MassUnit.GRAM.toSi(1e+3);
        assertEquals(1.0, result,
                0.0001, "Конвертація кілограм в грам не працює!");
    }

    @Test
    void testMilligramToKilogram() {
        double result = MassUnit.MILLIGRAM.toSi(1e+6);
        assertEquals(1.0, result,
                0.00001, "Конвертація міліграм в кілограм не працює!");
    }

    @Test
    void testPoundToKilogram() {
        double result = MassUnit.POUND.toSi(10);
        assertEquals(4.53592, result,
                0.00001, "Конвертація фунт в кілограм не працює!");
    }

    @Test
    void testOunceToKilogram() {
        double result = MassUnit.OUNCE.toSi(35.274);
        assertEquals(1.0, result,
                0.00001, "Конвертація унції в кілограм не працює!");
    }

    /*
     * +------------------------------+
     * |Unit mass from Si {Kilogram}. |
     * |1. Kilogram  => Gram          |
     * |2. Kilogram  => Milligram.    |
     * |3. Kilogram  => Pound.        |
     * |4. Kilogram  => Ounce.        |
     * +------------------------------+
     * */

    @Test
    void testKilogramFromGram() {
        double result = MassUnit.GRAM.fromSi(1);
        assertEquals(1000.0, result,
                0.0001, "Конвертація грам в кілограмне не працює!");
    }

    @Test
    void testKilogramFromMilligram() {
        double result = MassUnit.MILLIGRAM.fromSi(1);
        assertEquals(1e+6, result,
                0.00001, "Конвертація кілограм в міліграм не працює!");
    }

    @Test
    void testKilogramFromPound() {
        double result = MassUnit.POUND.fromSi(4.53592);
        assertEquals(10.0, result,
                0.00001, "Конвертація кілограм в фунт не працює!");
    }

    @Test
    void testKilogramFromOunce() {
        double result = MassUnit.OUNCE.fromSi(1.0);
        assertEquals(35.274, result,
                0.00001, "Конвертація кілограм в унції не працює!");
    }
}
