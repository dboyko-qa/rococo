package qa.dboyko.rococo.utils;

import com.github.javafaker.Faker;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

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
    public static String generateArtistName() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    @Nonnull
    public static String generatePaintingName() {
        return String.join(" ", faker.lorem().words(2));
    }

    @Nonnull
    public static String generateDescription() {
        return faker.lorem().characters(1000, 2000);
    }

    @Nonnull
    public static String randomCity() {
        return faker.address().cityName();
    }

    @Nullable
    public static File[] getImagesFromResourceFolder(String folder) {
        URL url = RandomDataUtils.class
                .getClassLoader()
                .getResource(folder);

        if (url == null) {
            throw new IllegalStateException("Directory not found: " + folder);
        }

        File dir = null;
        try {
            dir = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return dir.listFiles((d, name) -> name.endsWith(".jpg"));
    }

    @Nullable
    public static File getRandomImageFileFromResouces(String folderName) {
        File[] files = getImagesFromResourceFolder("images/" + folderName);
        return files[ThreadLocalRandom.current().nextInt(files.length)];
    }

    @Nullable
    public static File getRandomMuseumFile() {
        return getRandomImageFileFromResouces("museums");
    }

    @Nullable
    public static File getRandomArtistFile() {
        return getRandomImageFileFromResouces("artists");
    }

    @Nullable
    public static File getRandomPaintingFile() {
        return getRandomImageFileFromResouces("paintings");
    }

    @Nullable
    public static String generateRandomString(int length) {
        return faker.lorem().characters(length);
    }
}
