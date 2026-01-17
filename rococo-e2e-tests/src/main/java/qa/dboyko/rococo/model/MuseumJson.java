package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Museum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

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

