package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Painting;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import qa.dboyko.rococo.apiservice.grpc.ArtistGrpcClient;
import qa.dboyko.rococo.apiservice.grpc.MuseumGrpcClient;

import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaintingJson(
    @JsonProperty("id")
    String id,
    @JsonProperty("title")
    String title,
    @JsonProperty("description")
    String description,
    @JsonProperty("content")
    String content,
    @JsonProperty("museum")
    MuseumJson museum,
    @JsonProperty("artist")
    ArtistJson artist
) {
    public PaintingJson updateJson() {
        return new PaintingJson(
                this.id,
                this.title + "1",
                this.description + "1",
                jpegToString(getRandomPaintingFile().getPath()),
                new MuseumGrpcClient().getRandomMuseum(),
                new ArtistGrpcClient().getRandomArtist()
        );
    }

    public static PaintingJson generateRandomPaintingJson() {
        return new PaintingJson(
                "",
                generatePaintingName(),
                generateDescription(),
                jpegToString(getRandomPaintingFile().getPath()),
                new MuseumGrpcClient().getRandomMuseum(),
                new ArtistGrpcClient().getRandomArtist()
        );
    }

    public static @Nonnull PaintingJson fromGrpcMessage(@Nonnull Painting painting,
                                                        @Nullable MuseumJson museumJson,
                                                        @Nullable ArtistJson artistJson) {
        return new PaintingJson(
                painting.getId(),
                painting.getTitle(),
                painting.getDescription(),
                painting.getContent(),
                museumJson,
                artistJson
        );
    }

}
