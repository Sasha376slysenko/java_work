import java.util.Scanner;

public class UserCLI extends DigitUser {
    private short numberLevel = 0;

    private String symbolRepeat(String word, int count) {
        return word.repeat(count);
    }

    private void printDeviation(int numberUser, int randomDigit, int maxRange) {
        int width = 100;
        char[] bar = new char[width];
        StringBuilder rowFinish = new StringBuilder("Відхилення: 0 [ ");

        int userPos = (int) ((double) numberUser / maxRange * (width - 1));
        int randomPos = (int) ((double) randomDigit / maxRange * (width - 1));

        java.util.Arrays.fill(bar, '-');
        bar[userPos] = 'X';
        bar[randomPos] = '|';

        for (char c : bar) {
            rowFinish.append(c);
        }
        rowFinish.append(']');
        System.out.println(rowFinish);
    }

    public void StartGame() {
        Scanner scanner = new Scanner(System.in);
        String row1 = symbolRepeat("=", 50);
        String row2 = symbolRepeat("=", 10);
        String row3 = symbolRepeat("=", 10);
        String row4 = symbolRepeat("=", 10);
        String row5 = symbolRepeat("*", 30);

        row2 += " Вас вітає гра вгадає число.";
        row3 += " Оберіть рівень складності: ";
        row4 += " 1) Легкий (число в діапазоні 1-10).\n";
        row4 += "=".repeat(10);
        row4 += " 2) Середній (число в діапазоні 1-100).\n";
        row4 += "=".repeat(10);
        row4 += " 3) Складний (число в діапазоні 1-1000)\n";
        row5 += " Ведіть рівень складності: ";

        row2 += symbolRepeat("=", 10);
        row3 += symbolRepeat("=", 10);
        row4 += symbolRepeat("=", 50);
        row5 += symbolRepeat("*", 30);

        System.out.println(row1);
        System.out.println(row2);
        System.out.println(row3);
        System.out.println(row4);

        while (true) {
            System.out.println("\n" + row5);
            String input = scanner.next();

            try {
                numberLevel = Short.parseShort(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Це не число!");
            }
        }
    }

    public void Game() {
        Scanner scanner = new Scanner(System.in);
        String row1 = symbolRepeat("<=>", 50);
        String row2 = symbolRepeat("<=>", 20);
        String row3 = symbolRepeat("*", 20);
        String row4 = symbolRepeat("*", 68);
        String row5 = symbolRepeat("&", 20);
        String row6 = symbolRepeat("=", 55);
        int numberUser = 0;

        int maxRange = switch (numberLevel) {
            case 1 -> 10;
            case 2 -> 100;
            default -> 1000;
        };
        int randomDigitGame = switch (numberLevel) {
            case 1 -> this.randomDigit10();
            case 2 -> this.randomDigit100();
            default -> this.randomDigit1000();
        };

        row2 += " Ватію число згенероване! ";
        row3 += " ГРА РОЗПОЧАЛАСЬ! ";
        row5 += " ГРУ ЗАВЕРШЕНО! ";
        row2 += symbolRepeat("<=>", 22);
        row3 += symbolRepeat("*", 30);
        row5 += symbolRepeat("&", 20);
        System.out.println(row1);
        System.out.println(row2);

        System.out.println();
        System.out.println(row3);
        System.out.println(row4);

        while (true) {
            String input = scanner.next();

            try {
                numberUser = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Це не число!");
            }

            if (numberUser > maxRange) {
                System.out.println("Ви вели число більше заданого діапазону!");
                continue;
            }
            printDeviation(numberUser, randomDigitGame, maxRange);

            if (numberUser > randomDigitGame) {
                String rowi = symbolRepeat("=", 10);
                rowi += " Ви ввели число більше.";
                rowi += symbolRepeat("=", 15);
                System.out.println(rowi);
            } else if (numberUser < randomDigitGame) {
                String rowi = symbolRepeat("=", 10);
                rowi += " Ви ввели число менше.";
                rowi += symbolRepeat("=", 15);
                System.out.println(rowi);
            } else {
                String rowFinish = symbolRepeat("*", 20);
                rowFinish += " ВІТАЮ ВИ ПЕРЕМОГЛИ!";
                rowFinish += symbolRepeat("*", 20);
                System.out.println(rowFinish);
                break;
            }
        }
        System.out.println();
        System.out.println(row5);
        System.out.println(row6);
    }
}
