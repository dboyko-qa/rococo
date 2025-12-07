package qa.dboyko.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record GeoJson(
        @JsonProperty("city")
        @Size(min = 3, max = 255, message = "City can`t be less than 3 and longer than 255 characters")
        String city,
        @JsonProperty("country")
        CountryJson country
) {
}
