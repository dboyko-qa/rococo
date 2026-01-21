package uiTests.painting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.*;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.model.sitedata.PaintingData;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.artists.ViewArtistPage;
import qa.dboyko.rococo.pages.paintings.PaintingsPage;

import static qa.dboyko.rococo.model.sitedata.PaintingData.generatePaintingForArtistMuseum;
import static qa.dboyko.rococo.model.sitedata.PaintingData.generateRandomPainting;
import static qa.dboyko.rococo.pages.constants.TextConstants.PAINTING_ADD_MESSAGE;
import static qa.dboyko.rococo.pages.constants.TextConstants.PAINTING_FOR_ARTIST_ADD_MESSAGE;

@WebTest
@DisplayName("UI tests: paintings dialog")
public class CreatePaintingTest {

    @Test
    @DisplayName("Verify dialog content")
    @User(username = "user")
    @ApiLogin
    void createPaintingContent() {
        new MainPage()
                .openPaintings()
                .openAddPaintingPage()
                .verifyContentCreateDialog();
    }

    @Test
    @DisplayName("New painting should be successfully created")
    @User(username = "user")
    @ApiLogin
    void createPaintingSuccessful() {
        PaintingData paintingData = generateRandomPainting();
        new MainPage()
                .openPaintings()
                .openAddPaintingPage()
                .addPainting(paintingData)
                .verifyPopupMessage(PAINTING_ADD_MESSAGE.formatted(paintingData.title()), PaintingsPage.class)
                .verifyItemInList(paintingData.title())
                .openPainting(paintingData.title())
                .verifyViewPaintingContent(paintingData.toPaintingJson());

    }

    @Test
    @DisplayName("New painting should be successfully created for Artist")
    @Artist
    @Museum
    @User(username = "user")
    @ApiLogin
    void createPaintingForArtistSuccessful(@TestArtist ArtistJson artistJson, @TestMuseum MuseumJson museumJson) {
        PaintingData paintingData = generatePaintingForArtistMuseum(artistJson.name(), museumJson.title());
        new MainPage()
                .openArtists()
                .openArtist(artistJson.name())
                .openAddPaintingForArtist()
                .addPaintingForArtist(paintingData)
                .verifyPopupMessage(PAINTING_FOR_ARTIST_ADD_MESSAGE.formatted(paintingData.title()), ViewArtistPage.class)
                .verifyItemInList(paintingData.title());

    }
}
