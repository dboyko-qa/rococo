package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Artist;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;
import qa.dboyko.rococo.config.RococoGatewayServiceConfig;
import qa.dboyko.rococo.validation.IsPhotoString;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtistJson(
    @JsonProperty("id")
    String id,
    @JsonProperty("name")
    @Size(min = 3, max = 255, message = "Name can`t be less than 3 and longer than 255 characters")
    String name,
    @JsonProperty("biography")
    @Size(max = 2000, message = "Biography can`t be longer than 2000 characters")
    String biography,
    @JsonProperty("photo")
    @IsPhotoString
    @Size(max = RococoGatewayServiceConfig.ONE_MB)
    String photo

) {

    public static @Nonnull ArtistJson fromGrpcMessage(@Nonnull Artist artist) {
        return new ArtistJson(
                artist.getId(),
                artist.getName(),
                artist.getBiography(),
                artist.getPhoto()
        );
    }

    public Artist toGrpcMessage() {
        return Artist.newBuilder()
                .setId(this.id)
                .setBiography(this.biography)
                .setName(this.name)
                .setPhoto(this.photo)
        .build();
    }
}
