package apiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import qa.dboyko.rococo.api.gateway.ResponseSpecifications;
import qa.dboyko.rococo.apiservice.ArtistClient;
import qa.dboyko.rococo.extensions.ApiLoginExtension;
import qa.dboyko.rococo.extensions.annotations.*;
import qa.dboyko.rococo.extensions.annotations.meta.RestTest;
import qa.dboyko.rococo.model.ArtistJson;

import java.util.ArrayList;
import java.util.stream.Stream;

import static qa.dboyko.rococo.api.constants.ApiErrorMessages.INVALID_UUID;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;
import static qa.dboyko.rococo.utils.RandomDataUtils.generateArtistName;

@RestTest
@DisplayName("API tests: artist api")
public class ArtistTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();

    private final ResponseSpecifications responseSpecs = new ResponseSpecifications();

    private ArtistClient artistClient = new ArtistClient();

    @Test
    @DisplayName("Verify error with invalid artist id")
    void verifyGetRequestWithInvalidId() {
        artistClient.getArtist("123")
                .then()
                .spec(responseSpecs.badRequestWithErrorResponseSpec(INVALID_UUID));
    }

    @Test
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify that artist can be created with authorized user")
    void createArtistWithUser(@Token String bearerToken) {
        ArtistJson newArtist = ArtistJson.generateRandomArtistJson();
        ArtistJson createdArtist = artistClient.createArtist(newArtist, bearerToken)
                .then()
                .spec(responseSpecs.okResponseSpec())
                .extract().as(ArtistJson.class);

        // verify that artist created
        artistClient.getArtist(createdArtist.id())
                .then()
                .spec(responseSpecs.okResponseSpec());
    }

    @Test
    @DisplayName("Verify that artist cannot  be created with guest user")
    void createArtistWithGuest() {
        ArtistJson newArtist = ArtistJson.generateRandomArtistJson();
        artistClient.createArtist(newArtist, null)
                .then()
                .spec(responseSpecs.unauthorizedResponseSpec());
    }

    private static Stream<Arguments> artistEmptyValues() {
        return Stream.of(
                Arguments.of(new ArtistJson("", "", "", "", new ArrayList<>())),
                Arguments.of(new ArtistJson("", generateRandomString(256), "", "", new ArrayList<>())),
                Arguments.of(new ArtistJson("", generateArtistName(), generateRandomString(2001), "", new ArrayList<>())),
                Arguments.of(new ArtistJson("", generateArtistName(), "", getRandomArtistFile().getName(), new ArrayList<>()))
        );
    }

    @ParameterizedTest
    @MethodSource("artistEmptyValues")
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify errors with invalid field values")
    void verifyEmptyValue(ArtistJson artistJson,
                          @Token String bearerToken) {
        artistClient.createArtist(artistJson, bearerToken)
                .then()
                .spec(responseSpecs.badRequestResponseSpec());
    }

    @Test
    @Artist
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify that authorized user can update artist")
    void updateArtistWithUserSuccessful(@TestArtist ArtistJson artistJson,
                                        @Token String bearerToken){
        ArtistJson updated = artistJson.updateJson();
        artistClient.updateArtist(updated, bearerToken)
                .then()
                .spec(responseSpecs.okResponseSpec());
    }

    @Test
    @Artist
    @DisplayName("Verify that guest user cannot update artist")
    void updateArtistWithGuestFailed(@TestArtist ArtistJson artistJson){
        ArtistJson updated = artistJson.updateJson();
        artistClient.updateArtist(updated, null)
                .then()
                .spec(responseSpecs.unauthorizedResponseSpec());
    }

}
