package apiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import qa.dboyko.rococo.api.gateway.ResponseSpecifications;
import qa.dboyko.rococo.apiservice.MuseumClient;
import qa.dboyko.rococo.extensions.ApiLoginExtension;
import qa.dboyko.rococo.extensions.annotations.*;
import qa.dboyko.rococo.extensions.annotations.meta.RestTest;
import qa.dboyko.rococo.model.CountryJson;
import qa.dboyko.rococo.model.GeoJson;
import qa.dboyko.rococo.model.MuseumJson;

import java.util.stream.Stream;

import static qa.dboyko.rococo.api.constants.ApiErrorMessages.INVALID_UUID;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

@RestTest
@DisplayName("API tests: museum api")
public class MuseumTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();

    private final ResponseSpecifications responseSpecs = new ResponseSpecifications();

    private MuseumClient museumClient = new MuseumClient();

    @Test
    @DisplayName("Verify error with invalid museum id")
    void verifyGetRequestWithInvalidId() {
        museumClient.getMuseum("123")
                .then()
                .spec(responseSpecs.badRequestWithErrorResponseSpec(INVALID_UUID));
    }

    @Test
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify that museum can be created with authorized user")
    void createMuseumWithUser(@Token String bearerToken) {
        MuseumJson newMuseum = MuseumJson.generateRandomMuseumJson();
        MuseumJson createdMuseum = museumClient.createMuseum(newMuseum, bearerToken)
                .then()
                .spec(responseSpecs.okResponseSpec())
                .extract().as(MuseumJson.class);

        museumClient.getMuseum(createdMuseum.id())
                .then().spec(responseSpecs.okResponseSpec());
    }

    @Test
    @DisplayName("Verify that museum cannot be created with guest user")
    void createMuseumWithGuest() {
        MuseumJson newMuseum = MuseumJson.generateRandomMuseumJson();
        museumClient.createMuseum(newMuseum, null)
                .then()
                .spec(responseSpecs.unauthorizedResponseSpec());
    }

    private static Stream<Arguments> museumEmptyValues() {
        return Stream.of(
                Arguments.of(new MuseumJson("", "", "", "", new GeoJson("", new CountryJson("", "")))),
                Arguments.of(new MuseumJson("", generateRandomString(256), "", "", new GeoJson("", new CountryJson("", "")))),
                Arguments.of(new MuseumJson("", generateMuseumName(), generateRandomString(2001), "", new GeoJson("", new CountryJson("", "")))),
                Arguments.of(new MuseumJson("", generateMuseumName(), "", getRandomMuseumFile().getName(), new GeoJson("", new CountryJson("", ""))))
        );
    }

    @ParameterizedTest
    @MethodSource("museumEmptyValues")
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify errors with invalid field values")
    void verifyEmptyValue(MuseumJson museumJson,
                          @Token String bearerToken) {
        museumClient.createMuseum(museumJson, bearerToken)
                .then()
                .spec(responseSpecs.badRequestResponseSpec());
    }

    @Test
    @Museum
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify that authorized user can update museum")
    void updateMuseumWithUserSuccessful(@TestMuseum MuseumJson museumJson,
                             @Token String bearerToken){
        MuseumJson updated = museumJson.updateJson();
        museumClient.updateMuseum(updated, bearerToken)
                .then()
                .spec(responseSpecs.okResponseSpec());
    }

    @Test
    @Museum
    @DisplayName("Verify that guest user cannot update museum")
    void updateMuseumWithGuestFailed(@TestMuseum MuseumJson museumJson){
        MuseumJson updated = museumJson.updateJson();
        museumClient.updateMuseum(updated, null)
                .then()
                .spec(responseSpecs.unauthorizedResponseSpec());
    }

}
