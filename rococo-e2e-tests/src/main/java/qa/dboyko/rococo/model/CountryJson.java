package qa.dboyko.rococo.model;

import com.dboyko.rococo.grpc.Country;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

public record CountryJson(
        @JsonProperty("id")
        String id,
        @JsonProperty("name")
        String name
) {
        public static @Nonnull CountryJson fromGrpcMessage(@Nonnull Country countryGrpc) {
                return new CountryJson(countryGrpc.getId(), countryGrpc.getName());
        }
}
