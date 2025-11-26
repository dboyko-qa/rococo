package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.GetUserResponse;
import com.google.protobuf.ByteString;
import jakarta.annotation.Nonnull;

import java.util.Base64;

public record UserdataJson(
    String id,
    String username,
    String firstname,
    String lastname,
    String avatar) {

    private static String convertAvatarToString(ByteString avatarBytes) {
        return (avatarBytes != null && !avatarBytes.isEmpty())
                ? Base64.getEncoder().encodeToString(avatarBytes.toByteArray())
                : "";
    }

    public static ByteString convertStringToAvatar(String avatarBase64) {
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(avatarBase64);
                return ByteString.copyFrom(decodedBytes);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid Base64 string for avatar", e);
            }
        }
        return ByteString.EMPTY;
    }

    public static @Nonnull UserdataJson fromGrpcMessage(@Nonnull GetUserResponse userdataMessage) {
        return new UserdataJson(
                userdataMessage.getUserId(),
                userdataMessage.getUsername(),
                userdataMessage.getFirstname(),
                userdataMessage.getLastname(),
                userdataMessage.getAvatar()
        );
    }
}
