package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Userdata;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;
import qa.dboyko.rococo.config.RococoGatewayServiceConfig;
import qa.dboyko.rococo.validation.IsPhotoString;

public record UserdataJson(
        @JsonProperty("id")
    String id,
    @JsonProperty("username")
    @Size(min = 3, max = 255, message = "Username can`t be less than 3 and longer than 255 characters")
    String username,
    @JsonProperty("firstname")
    @Size(min = 3, max = 255, message = "firstname can`t be less than 3 and longer than 255 characters")
    String firstname,
    @JsonProperty("lastname")
    @Size(min = 3, max = 255, message = "lastname can`t be less than 3 and longer than 255 characters")
    String lastname,
    @JsonProperty("avatar")
    @IsPhotoString
    @Size(max = RococoGatewayServiceConfig.ONE_MB)
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
