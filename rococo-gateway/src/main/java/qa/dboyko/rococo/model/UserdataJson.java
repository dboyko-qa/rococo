package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.GetUserResponse;
import com.dboyko.rococo.grpc.Userdata;
import com.google.protobuf.ByteString;
import jakarta.annotation.Nonnull;

import java.util.Base64;

public record UserdataJson(
    String id,
    String username,
    String firstname,
    String lastname,
    String avatar) {

    public static @Nonnull UserdataJson fromGrpcMessage(@Nonnull Userdata userdataMessage) {
        return new UserdataJson(
                userdataMessage.getUserId(),
                userdataMessage.getUsername(),
                userdataMessage.getFirstname(),
                userdataMessage.getLastname(),
                userdataMessage.getAvatar()
        );
    }

    public Userdata toGrpcMessage() {
        return Userdata.newBuilder()
                .setUserId(this.id)
                .setUsername(this.username)
                .setFirstname(this.firstname)
                .setLastname(this.lastname)
                .setAvatar(this.avatar)
                .build();
    }
}
