package qa.dboyko.rococo.model.sitedata;

import jakarta.annotation.Nonnull;
import qa.dboyko.rococo.model.CountryJson;
import qa.dboyko.rococo.model.GeoJson;
import qa.dboyko.rococo.model.MuseumJson;

import static qa.dboyko.rococo.enums.Country.getRandomCountry;
import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

public record MuseumData(
        String title,
        String description,
        String photoFilePath,
        String city,
        String countryName
) {

    @Nonnull
    public static MuseumData generateRandomMuseum() {
        return new MuseumData(
                generateMuseumName(),
                generateDescription(),
                getRandomMuseumFile().getPath(),
                randomCity(),
                getRandomCountry().getName()
        );
    }

    @Nonnull
    public MuseumJson toMuseumJson() {
        return new MuseumJson(
                "",
                this.title,
                this.description,
                jpegToString(this.photoFilePath),
                new GeoJson(this.city, new CountryJson("", this.countryName))
        );
    }
}
