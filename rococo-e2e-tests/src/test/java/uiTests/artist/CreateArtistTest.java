package uiTests.artist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.sitedata.ArtistData;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.artists.ArtistsPage;

import static qa.dboyko.rococo.model.sitedata.ArtistData.generateRandomArtist;
import static qa.dboyko.rococo.pages.constants.TextConstants.ARTIST_ADD_MESSAGE;

@WebTest
public class CreateArtistTest {

    @Test
    @DisplayName("Verify dialog content")
    @User(username = "user")
    @ApiLogin
    void createArtistContent() {
        new MainPage()
                .openArtists()
                .openAddArtistPage()
                .verifyContentCreateDialog();
    }

    @Test
    @DisplayName("New artist should be successfully created")
    @User(username = "user")
    @ApiLogin
    void createArtistSuccessful() {
        ArtistData artistData = generateRandomArtist();
        new MainPage()
                .openArtists()
                .openAddArtistPage()
                .addArtist(artistData)
                .verifyPopupMessage(ARTIST_ADD_MESSAGE.formatted(artistData.name()), ArtistsPage.class)
                .verifyItemInList(artistData.name())
                .openArtist(artistData.name())
                .verifyViewArtistContent(artistData.toArtistJson());

    }
}
