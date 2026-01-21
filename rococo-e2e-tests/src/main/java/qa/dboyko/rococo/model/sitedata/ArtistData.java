package qa.dboyko.rococo.model.sitedata;

import jakarta.annotation.Nonnull;
import qa.dboyko.rococo.model.ArtistJson;

import java.util.List;

import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

public record ArtistData(
        String name,
        String biography,
        String photoFilePath
) {

    @Nonnull
    public static ArtistData generateRandomArtist() {
        return new ArtistData(
                generateArtistName(),
                generateDescription(),
                getRandomArtistFile().getPath()
        );
    }

    @Nonnull
    public ArtistJson toArtistJson() {
        return new ArtistJson(
                "",
                name,
                biography,
                jpegToString(this.photoFilePath),
                List.of()
        );
    }
}
