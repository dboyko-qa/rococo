package apiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import qa.dboyko.rococo.api.gateway.ResponseSpecifications;
import qa.dboyko.rococo.apiservice.PaintingClient;
import qa.dboyko.rococo.extensions.ApiLoginExtension;
import qa.dboyko.rococo.extensions.annotations.*;
import qa.dboyko.rococo.extensions.annotations.meta.RestTest;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.model.PaintingJson;

import java.util.stream.Stream;

import static qa.dboyko.rococo.api.constants.ApiErrorMessages.INVALID_UUID;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

@RestTest
@DisplayName("API tests: painting api")
public class PaintingTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();

    private final ResponseSpecifications responseSpecs = new ResponseSpecifications();

    private PaintingClient paintingClient = new PaintingClient();

    @Test
    @DisplayName("Verify error with invalid painting id")
    void verifyGetRequestWithInvalidId() {
        paintingClient.getPaintung("123")
                .then()
                .spec(responseSpecs.badRequestWithErrorResponseSpec(INVALID_UUID));
    }

    @Test
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify that painting can be created with authorized user")
    void createPaintingWithUser(@Token String bearerToken) {
        PaintingJson newPainting = PaintingJson.generateRandomPaintingJson();
        PaintingJson createdPainting = paintingClient.createPainting(newPainting, bearerToken)
                .then()
                .spec(responseSpecs.okResponseSpec())
                .extract().as(PaintingJson.class);

        paintingClient.getPaintung(createdPainting.id())
                .then().spec(responseSpecs.okResponseSpec());
    }

    @Test
    @DisplayName("Verify that painting cannot be created with guest user")
    void createPaintingWithGuest() {
        PaintingJson newPainting = PaintingJson.generateRandomPaintingJson();
        paintingClient.createPainting(newPainting, null)
                .then()
                .spec(responseSpecs.unauthorizedResponseSpec());
    }

    private static Stream<Arguments> paintingEmptyValues() {
        return Stream.of(
                Arguments.of(new PaintingJson("", "", "", "", MuseumJson.generateRandomMuseumJson(), ArtistJson.generateRandomArtistJson())),
                Arguments.of(new PaintingJson("", generateRandomString(256), "", "", MuseumJson.generateRandomMuseumJson(), ArtistJson.generateRandomArtistJson())),
                Arguments.of(new PaintingJson("", generatePaintingName(), generateRandomString(2001), "", MuseumJson.generateRandomMuseumJson(), ArtistJson.generateRandomArtistJson())),
                Arguments.of(new PaintingJson("", generatePaintingName(), "", getRandomPaintingFile().getName(), MuseumJson.generateRandomMuseumJson(), ArtistJson.generateRandomArtistJson())),
                Arguments.of(new PaintingJson("", generatePaintingName(), "", getRandomPaintingFile().getName(), null, ArtistJson.generateRandomArtistJson())),
                Arguments.of(new PaintingJson("", generatePaintingName(), "", getRandomPaintingFile().getName(), MuseumJson.generateRandomMuseumJson(), null))
        );
    }

    @ParameterizedTest
    @MethodSource("paintingEmptyValues")
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify errors with invalid field values")
    void verifyEmptyValue(PaintingJson paintingJson,
                          @Token String bearerToken) {
        paintingClient.createPainting(paintingJson, bearerToken)
                .then()
                .spec(responseSpecs.badRequestResponseSpec());
    }

    @Test
    @Painting
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify that authorized user can update painting")
    void updatePaintingWithUserSuccessful(@TestPainting PaintingJson paintingJson,
                                          @Token String bearerToken) {
        PaintingJson updated = paintingJson.updateJson();
        paintingClient.updatePainting(updated, bearerToken)
                .then()
                .spec(responseSpecs.okResponseSpec());
    }

    @Test
    @Painting
    @DisplayName("Verify that guest user cannot update painting")
    void updatePaintingWithGuestFailed(@TestPainting PaintingJson paintingJson) {
        PaintingJson updated = paintingJson.updateJson();
        paintingClient.updatePainting(updated, null)
                .then()
                .spec(responseSpecs.unauthorizedResponseSpec());
    }

}
