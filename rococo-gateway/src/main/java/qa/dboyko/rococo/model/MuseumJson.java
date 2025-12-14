package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Museum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;
import qa.dboyko.rococo.validation.IsPhotoString;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MuseumJson(
    @JsonProperty("id")
    String id,
    @JsonProperty("title")
    @Size(min = 3, max = 255, message = "Title can`t be less than 3 and longer than 255 characters")
    String title,
    @JsonProperty("description")
    @Size(max = 2000, message = "Description can`t be longer than 2000 characters")
    String description,
    @JsonProperty("photo")
    @IsPhotoString
    String photo,
    @JsonProperty("geo")
    GeoJson geo
) {

    public static MuseumJson defaultValues() {
        return new MuseumJson("1", "Museum", "1234567890", "",
                new GeoJson("Paris", new CountryJson("cf412371-47d7-41ad-9f33-36d8ece49a35", "France")));

    }

    public static @Nonnull MuseumJson fromGrpcMessage(@Nonnull Museum museum, String countryName) {
        return new MuseumJson(
                museum.getId(),
                museum.getTitle(),
                museum.getDescription(),
                museum.getPhoto(),
                new GeoJson(museum.getCity(), new CountryJson(museum.getId(), countryName))
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
