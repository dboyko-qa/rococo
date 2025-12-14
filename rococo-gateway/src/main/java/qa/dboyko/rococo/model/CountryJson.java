package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Country;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;

public record CountryJson(
        @JsonProperty("id")
        String id,
        @JsonProperty("name")
        @Size(min = 3, max = 255, message = "Title can`t be less than 3 and longer than 255 characters")
        String name
) {

    public static @Nonnull CountryJson fromGrpcMessage(@Nonnull Country countryGrpc) {
        return new CountryJson(countryGrpc.getId(), countryGrpc.getName());
    }
}
