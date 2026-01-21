package uiTests.artist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.Artist;
import qa.dboyko.rococo.extensions.annotations.TestArtist;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.model.sitedata.ArtistData;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.artists.ViewArtistPage;

import static qa.dboyko.rococo.pages.constants.TextConstants.ARTIST_UPDATE_MESSAGE;
import static qa.dboyko.rococo.utils.RandomDataUtils.*;

@WebTest
public class UpdateArtistTest {

    @Test
    @Artist(createNew = true)
    @User(username = "user")
    @ApiLogin
    @DisplayName("Artist can be successfully updated")
    void verifyEditArtistSuccessful(@TestArtist ArtistJson artistJson) {
        ArtistData updateArtistData = new ArtistData(
                artistJson.name() + "1",
                artistJson.biography() + "1",
                getRandomArtistFile().toString()
        );
        new MainPage().openArtists()
                .openArtist(artistJson.name())
                .editArtist()
                .verifyContentEditDialog(artistJson)
                .updateArtist(updateArtistData)
                .verifyPopupMessage(ARTIST_UPDATE_MESSAGE.formatted(updateArtistData.name()), ViewArtistPage.class)
                .verifyViewArtistContent(updateArtistData.toArtistJson());

    }
}
