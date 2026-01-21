package qa.dboyko.rococo.model.sitedata;

import jakarta.annotation.Nonnull;
import qa.dboyko.rococo.apiservice.grpc.ArtistGrpcClient;
import qa.dboyko.rococo.apiservice.grpc.MuseumGrpcClient;
import qa.dboyko.rococo.model.*;

import java.util.List;

import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

public record PaintingData(
        String title,
        String description,
        String photoFilePath,
        String museumTitle,
        String artistName
) {

    @Nonnull
    public static PaintingData generateRandomPainting() {
        return new PaintingData(
                generatePaintingName(),
                generateDescription(),
                getRandomPaintingFile().getPath(),
                new MuseumGrpcClient().getRandomMuseum().title(),
                new ArtistGrpcClient().getRandomArtist().name()
        );
    }

    @Nonnull
    public static PaintingData generatePaintingForArtistMuseum(String artistName, String museumTitle) {
        return new PaintingData(
                generatePaintingName(),
                generateDescription(),
                getRandomPaintingFile().getPath(),
                museumTitle,
                artistName
        );
    }

    @Nonnull
    public PaintingJson toPaintingJson() {
        return new PaintingJson(
                "",
                this.title,
                this.description,
                jpegToString(this.photoFilePath),
                new MuseumJson("", museumTitle, "", "", new GeoJson("", new CountryJson("", ""))),
                new ArtistJson("", artistName, "", "", List.of())
        );
    }
}
