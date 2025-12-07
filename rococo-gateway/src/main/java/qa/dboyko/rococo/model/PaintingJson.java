package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Artist;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;
import qa.dboyko.rococo.config.RococoGatewayServiceConfig;
import qa.dboyko.rococo.validation.IsPhotoString;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaintingJson(
    @JsonProperty("id")
    String id,
    @JsonProperty("title")
    @Size(min = 3, max = 255, message = "Name can`t be less than 3 and longer than 255 characters")
    String title,
    @JsonProperty("description")
    @Size(max = 2000, message = "Biography can`t be longer than 2000 characters")
    String description,
    @JsonProperty("content")
    @IsPhotoString
    String content,
    @JsonProperty("museum")
    MuseumJson museum
) {

//    public static @Nonnull PaintingJson fromGrpcMessage(@Nonnull Artist artist) {
//        return new PaintingJson(
//                artist.getId(),
//                artist.getName(),
//                artist.getBiography(),
//                artist.getPhoto()
//        );
//    }
}
