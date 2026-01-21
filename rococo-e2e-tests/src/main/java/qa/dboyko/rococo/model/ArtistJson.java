package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Artist;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import qa.dboyko.rococo.model.sitedata.ArtistData;

import java.util.ArrayList;
import java.util.List;

import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtistJson(
    @JsonProperty("id")
    String id,
    @JsonProperty("name")
    String name,
    @JsonProperty("biography")
    String biography,
    @JsonProperty("photo")
    String photo,
    @Nullable
    List<PaintingJson> paintings

) {

    public ArtistJson updateJson() {
        return new ArtistJson(
                this.id,
                this.name() + "1",
                this.biography() + "1",
                jpegToString(getRandomArtistFile().toString()),
                this.paintings
        );
    }

    public static @Nonnull ArtistJson generateRandomArtistJson() {
        return new ArtistJson(
                "",
                generateArtistName(),
                generateDescription(),
                jpegToString(getRandomArtistFile().getPath()),
                new ArrayList<>()
        );
    }

    public static @Nonnull ArtistJson fromGrpcMessage(@Nonnull Artist artist) {
        return new ArtistJson(
                artist.getId(),
                artist.getName(),
                artist.getBiography(),
                artist.getPhoto(),
                new ArrayList<>()
        );

    }

    @Nonnull
    public ArtistData toArtistData(String imageFilePath) {
        return new ArtistData(
                this.name,
                this.biography,
                imageFilePath
        );
    }
}
