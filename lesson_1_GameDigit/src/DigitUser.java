import java.util.concurrent.ThreadLocalRandom;

public class DigitUser implements IUser {
    private final int minDigit = 0;

    @Override
    public int randomDigit10() {
        final int maxDigit10 = 11;
        return ThreadLocalRandom.current()
                .nextInt(this.minDigit, maxDigit10);
    }

    @Override
    public int randomDigit100() {
        final int maxDigit100 = 101;
        return ThreadLocalRandom.current()
                .nextInt(this.minDigit, maxDigit100);
    }

    @Override
    public int randomDigit1000() {
        final int maxDigit1000 = 1001;
        return ThreadLocalRandom.current()
                .nextInt(this.minDigit, maxDigit1000);
    }
}
