package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Museum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import qa.dboyko.rococo.model.sitedata.MuseumData;

import static qa.dboyko.rococo.enums.CountryJsonCache.getRandomCountryJson;
import static qa.dboyko.rococo.utils.ConversionUtils.jpegToString;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;
import static qa.dboyko.rococo.utils.RandomDataUtils.randomCity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MuseumJson(
    @JsonProperty("id")
    String id,
    @JsonProperty("title")
    String title,
    @JsonProperty("description")
    String description,
    @JsonProperty("photo")
    String photo,
    @JsonProperty("geo")
    GeoJson geo
) {
    public static @Nonnull MuseumJson fromGrpcMessage(@Nonnull Museum museum, CountryJson countryJson) {
        return new MuseumJson(
                museum.getId(),
                museum.getTitle(),
                museum.getDescription(),
                museum.getPhoto(),
                new GeoJson(museum.getCity(), countryJson)
        );
    }

    @Nonnull
    public static MuseumJson generateRandomMuseumJson() {
        return new MuseumJson(
                "",
                generateMuseumName(),
                generateDescription(),
                jpegToString(getRandomMuseumFile().getPath()),
                new GeoJson(randomCity(), getRandomCountryJson())
        );
    }

    @Nonnull
    public MuseumJson updateJson() {
        return new MuseumJson(
                this.id,
                this.title() + "1",
                this.description() + "1",
                jpegToString(getRandomMuseumFile().toString()),
                new GeoJson(randomCity(), getRandomCountryJson())
        );
    }

    @Nonnull
    public MuseumData toMuseumData(String imageFilePath) {
        return new MuseumData(
                this.title,
                this.description,
                imageFilePath,
                this.geo.city(),
                this.geo.country().name()
        );
    }

    public @Nonnull Museum toGrpcMessage() {
        return Museum.newBuilder()
                .setId(this.id)
                .setTitle(this.title)
                .setDescription(this.description)
                .setPhoto(this.photo)
                .setCity(this.geo.city())
                .setCountryId(this.geo.country().id())
                .build();
    }
}

