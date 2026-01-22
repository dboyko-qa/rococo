package uiTests.artist;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.*;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.pages.artists.ArtistsPage;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.museums.MuseumsPage;

import static qa.dboyko.rococo.enums.LoggedUser.GUEST;
import static qa.dboyko.rococo.enums.LoggedUser.USER_LOGGED_IN;

@WebTest
@DisplayName("UI tests: artists page")
public class ArtistTest {

    @Test
    @DisplayName("Verify page for guest")
    void verifyArtistsPageForGuest() {
        Selenide.open(ArtistsPage.url, ArtistsPage.class)
                .verifyAddButtonVisibility(GUEST)
                .verifyMainContent();
    }

    @Test
    @Artist
    @DisplayName("Artists should be visible for guest")
    void verifyArtistVisible(@TestArtist ArtistJson artistJson) {
        Selenide.open(ArtistsPage.url, ArtistsPage.class)
                .scrollToLastItem()
                .verifyItemInList(artistJson.name())
                .findItem(artistJson.name())
                .verifyItemInList(artistJson.name())
                .openArtist(artistJson.name())
                .verifyViewArtistContent(artistJson)
                .verifyEditButtonForUser(GUEST);
    }

    @Test
    @Artist
    @User(username = "user")
    @ApiLogin
    @DisplayName("Artists should be visible and editable for logged user")
    void verifyArtistVisibleForUser(@TestArtist ArtistJson artistJson) {
        new MainPage().openArtists()
                .scrollToLastItem()
                .verifyItemInList(artistJson.name())
                .findItem(artistJson.name())
                .verifyItemInList(artistJson.name())
                .openArtist(artistJson.name())
                .verifyViewArtistContent(artistJson)
                .verifyPaintingsForArtist(artistJson)
                .verifyEditButtonForUser(USER_LOGGED_IN);
    }

}
