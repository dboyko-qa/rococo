package qa.dboyko.rococo.utils;

import jakarta.annotation.Nonnull;
import lombok.SneakyThrows;
import org.eclipse.jdt.annotation.NonNullByDefault;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@NonNullByDefault
public class OAuthUtils {

  private static final SecureRandom secureRandom = new SecureRandom();

  @Nonnull
  public static String generateCodeVerifier() {
    byte[] codeVerifier = new byte[32];
    secureRandom.nextBytes(codeVerifier);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
  }

  @SneakyThrows
  @Nonnull
  public static String generateCodeChallenge(String codeVerifier) {
    byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);
    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
    messageDigest.update(bytes, 0, bytes.length);
    byte[] digest = messageDigest.digest();
    return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
  }
}
