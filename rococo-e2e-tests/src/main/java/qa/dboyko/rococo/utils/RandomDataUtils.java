package qa.dboyko.rococo.utils;

import com.github.javafaker.Faker;
import jakarta.annotation.Nonnull;

import static java.time.LocalTime.now;

public class RandomDataUtils {
    private static final Faker faker = new Faker();

    @Nonnull
    public static String randomUsername() {
        String nowValue = now().toString();
        return faker.name().username() + nowValue.substring(nowValue.length() - 5);
    }

    @Nonnull
    public static String generateMuseumName() {
        return faker.company().name();
    }

    @Nonnull
    public static String generateDescription() {
        return faker.lorem().characters(1000, 2000);
    }

    @Nonnull
    public static String randomCity() {
        return faker.address().cityName();
    }
}
