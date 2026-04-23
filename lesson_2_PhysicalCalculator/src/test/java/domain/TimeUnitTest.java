package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeUnitTest {
    /*
    * +--------------------------+
    * |Unit time to Si {second}. |
    * |1) Minute => Second.      |
    * |2) Hour   => Second .     |
    * +--------------------------+
    * */

    @Test
    void minuteToSecond() {
        double result = TimeUnit.MINUTE.toSi(1);
        assertEquals(60.0, result,
                0.0001, "Конвертація хвили в секунди не працює!");
    }

    @Test
    void hourToSecond() {
        double result = TimeUnit.HOUR.toSi(1);
        assertEquals(3600.0, result,
                0.0001, "Конвертація годин в секунди не працює!");
    }

    /*
     * +----------------------------+
     * |Unit time from Si {Second}. |
     * |1) Second => Minute.        |
     * |2) Second => Hour.          |
     * +----------------------------+
     * */

    @Test
    void secondFromMinute() {
        double result = TimeUnit.MINUTE.fromSi(60.0);
        assertEquals(1.0, result,
                0.0001, "Конвертація секунд в хвилини не працює!");
    }

    @Test
    void secondFromHour() {
        double result = TimeUnit.HOUR.fromSi(3600.0);
        assertEquals(1.0, result,
                0.0001, "Конвертація секунд в години не працює!");
    }
}
