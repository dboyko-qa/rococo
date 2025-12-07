package qa.dboyko.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
}
