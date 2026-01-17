package qa.dboyko.rococo.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class ConversionUtils {
    public static String jpegToString(String path) {
        byte[] photoBytes = null;
        try {
            photoBytes = Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(photoBytes);
    }
}
