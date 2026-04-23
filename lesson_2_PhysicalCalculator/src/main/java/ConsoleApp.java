import domain.*;

import java.util.Scanner;

public class ConsoleApp implements IConsoleApp {

    private UnitCategory category;
    private int unitUserSelection;
    private double valueInput;
    private String unitUser;
    private Unit fromOrTop;

    private String symbolRepeat(String symbol, int count) {
        return symbol.repeat(count);
    }

    @Override
    public void listCategory() {
        UnitCategory[] units = UnitCategory.values();
        String row1 = symbolRepeat("*", 20);
        String row2 = symbolRepeat("*", 20);

        row1 += " Вас вітає калькулятор фізичних одиниць. ";
        row2 += " Виберіть категорію, яка Вас цікавить. ";
        row1 += symbolRepeat("*", 20);
        row2 += symbolRepeat("*", 22);

        System.out.println(row1);
        System.out.println(row2);
        System.out.println();

        for (int i = 0; i < units.length; i++) {
            System.out.println((i+1) + ") " + units[i]);
        }
    }

    @Override
    public void setUserCategory() {
        /*
        * +------------------------+
        * |1) Input category.      |
        * |2) Definition category. |
        * +------------------------+
        * */

        Scanner scanner = new Scanner(System.in);
        String row1 = symbolRepeat("=", 20);
        String row2 = symbolRepeat("=", 20);
        String row3 = symbolRepeat("=", 60);

        row1 += " Введіть категорію. ";
        row2 += " Ви обрали категорію: ";
        row1 += symbolRepeat("=", 20);
        row2 += symbolRepeat("=", 18);
        row2 += "\n";

        System.out.println();
        System.out.println(row1);

        // 1) Input category
        int categoryChoice;
        while (true) {
            String input = scanner.next();

            try {
                categoryChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ви ввели не число!");
                continue;
            }

            if (categoryChoice > 4 || categoryChoice <= 0) {
                System.out.println("Такої категорії нема!");
            } else {
                break;
            }
        }

        // 2) Definition category
        switch (categoryChoice) {
            case 1 -> {
                row2 += "LENGTH";
                category = UnitCategory.LENGTH;
            }
            case 2 -> {
                row2 += "MASS";
                category = UnitCategory.MASS;
            }
            case 3 -> {
                row2 += "TIME";
                category = UnitCategory.TIME;
            }
            default -> {
                row2 += "TEMPERATURE";
                category = UnitCategory.TEMPERATURE;
            }
        };
        System.out.println(row2);
        System.out.println(row3);
    }

    @Override
    public void listUnit() {
        /*
        * +--------------------------+
        * |1) Print list units.      |
        * |2) Input user unit.       |
        * |3) Parse string.          |
        * |4) Input user value unit. |
        * +--------------------------+
        * */

        Scanner scanner = new Scanner(System.in);
        Unit[] units = UnitConverter.getUnits(category);
        String row1 = symbolRepeat("<=>", 4);
        String row2 = symbolRepeat("<=>", 4);
        String row3 = symbolRepeat("<=>", 20);
        String row4 = symbolRepeat("<=>", 6);

        row1 += " Виберіть одинцю з списка заданих. ";
        row2 += " Ведіть значення для певної одиниці. ";
        row4 += " Ви обрали одиницю: ";
        row1 += symbolRepeat("<=>", 4);
        row2 += symbolRepeat("<=>", 4);
        row4 += symbolRepeat("<=>", 7);
        System.out.println();
        System.out.println(row1);

        // 1) Print list unit
        int lengthUnits = units.length;
        for (int i = 0; i < lengthUnits; i++) {
            System.out.println((i + 1) + ") " + units[i]);
        }
        System.out.println(row1);

        // 2) Input user unit
        while (true) {
            String input = scanner.next();

            try {
                unitUserSelection = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ви ввели не число!");
                continue;
            }

            if (unitUserSelection > lengthUnits || unitUserSelection <= 0) {
                System.out.println("Такої одиниці немає!");
            } else {
                unitUserSelection--;
                break;
            }
        }

        // 3) Parse string
        unitUser = units[unitUserSelection].toString();
        System.out.println(row4);
        System.out.println(unitUser);
        System.out.println(row2);

        // 4) Input user value unit
        while (true) {
            String input = scanner.next();

            try {
                valueInput = Double.parseDouble(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ви ввели не число!");
            }
        }
        System.out.println(row3);
    }

    @Override
    public void setUserUnitCategory() {
        switch (category) {
            case LENGTH      -> fromOrTop = LengthUnit.valueOf(unitUser);
            case MASS        -> fromOrTop = MassUnit.valueOf(unitUser);
            case TIME        -> fromOrTop = TimeUnit.valueOf(unitUser);
            case TEMPERATURE -> fromOrTop = TemperatureUnit.valueOf(unitUser);
        }
    }

    @Override
    public void getConversion() {
        /*
        * +--------------------+
        * |1) Input user mode. |
        * |2) Output user mode.|
        * +--------------------+
        * */

        double result;
        int inputFromOrTo;
        boolean flag = false;
        String resultStr = "&\t\t\t\t\t";
        Scanner scanner = new Scanner(System.in);
        String row1 = symbolRepeat("=", 16);
        String row3 = symbolRepeat("=", 61);
        String row4 = symbolRepeat("=", 21);
        String row5 = symbolRepeat("&", 22);
        String row6 = symbolRepeat("&", 61);
        String row2 = "1) Від одиниці - {from} \n2) До одиниці - {to}";

        row5 += " РЕЗУЛЬТАТ: ";
        row4 += " Ви обрали режим. ";
        row1 += " Виберіть режим переведення: ";
        row1 += symbolRepeat("=", 16);
        row4 += symbolRepeat("=", 22);
        row5 += symbolRepeat("&", 27);

        System.out.println();
        System.out.println(row1);
        System.out.println(row2);
        System.out.println(row3);

        // 1) Input user mode
        while (true) {
            String input = scanner.next();

            try {
                inputFromOrTo = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ви ввели не число!");
                continue;
            }

            if (inputFromOrTo > 2 || inputFromOrTo <= 0) {
                System.out.println("Такого режиму нема!");
            } else {
                break;
            }
        }

        // 2) Output user mode
        System.out.println(row4);
        if (inputFromOrTo == 1) {
            result = fromOrTop.fromSi(valueInput);
            System.out.println("Від одинці - {from}");
            resultStr += result + " (" + unitUser + ")";
        } else {
            flag = true;
            result = fromOrTop.toSi(valueInput);
            System.out.println("До одиниці - {to}");
        }

        if (flag) {
            resultStr += result + switch (category) {
                case LENGTH      -> " (METER)";
                case MASS        -> " (KILOGRAM)";
                case TIME        -> " (SECOND)";
                case TEMPERATURE -> " (KELVIN)";
            };
        }

        System.out.println(row3);
        System.out.println();
        System.out.println(row5);
        System.out.println(resultStr);
        System.out.println(row6);
    }
}
