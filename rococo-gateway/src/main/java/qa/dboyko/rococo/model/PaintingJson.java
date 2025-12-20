package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Painting;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;
import qa.dboyko.rococo.validation.IsPhotoString;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaintingJson(
    @JsonProperty("id")
    String id,
    @JsonProperty("title")
    @Size(min = 3, max = 255, message = "Name can`t be less than 3 and longer than 255 characters")
    String title,
    @JsonProperty("description")
    @Size(max = 2000, message = "Description can`t be longer than 2000 characters")
    String description,
    @JsonProperty("content")
    @IsPhotoString
    String content,
    @JsonProperty("museum")
    MuseumJson museum,
    @JsonProperty("artist")
    ArtistJson artist
) {


    public static @Nonnull PaintingJson fromGrpcMessage(@Nonnull Painting painting,
                                                        @Nonnull MuseumJson museumJson,
                                                        @Nonnull ArtistJson artistJson) {
        return new PaintingJson(
                painting.getId(),
                painting.getTitle(),
                painting.getDescription(),
                painting.getContent(),
                museumJson,
                artistJson
        );
    }

    public Painting toGrpcMessage() {
        return Painting.newBuilder()
                .setId(this.id)
                .setTitle(this.title)
                .setDescription(this.description)
                .setContent(this.content)
                .setMuseumId(this.museum.id())
                .setArtistId(this.artist.id())
                .build();
    }
}
