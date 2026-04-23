package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UnitCategoryTest {
    /*
    * +---------------------------+
    * |Check units categories ALL.|
    * +---------------------------+
    * */

    @Test
    void testAllCategoriesReturnUnits() {
        for (UnitCategory category: UnitCategory.values()) {
            Unit[] units = UnitConverter.getUnits(category);

            assertNotNull(units, "Категорія " + category + " повернула NULL!");
            assertTrue(units.length > 0, "Категорія " + category + " порожня!");
        }
    }
}
