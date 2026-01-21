package uiTests.museum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.sitedata.MuseumData;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.museums.MuseumsPage;

import static qa.dboyko.rococo.model.sitedata.MuseumData.generateRandomMuseum;
import static qa.dboyko.rococo.pages.constants.TextConstants.MUSEUM_ADD_MESSAGE;

@WebTest
public class CreateMuseumTest {

    @Test
    @DisplayName("Verify dialog content")
    @User(username = "user")
    @ApiLogin
    void createMuseumContent() {
        new MainPage()
                .openMuseums()
                .openAddMuseumPage()
                .verifyContentCreateDialog();
    }

    @Test
    @DisplayName("New museum should be successfully created")
    @User(username = "user")
    @ApiLogin
    void createMuseumSuccessful() {
        MuseumData museumData = generateRandomMuseum();
        new MainPage()
                .openMuseums()
                .openAddMuseumPage()
                .addMuseum(museumData)
                .verifyPopupMessage(MUSEUM_ADD_MESSAGE.formatted(museumData.title()), MuseumsPage.class)
                .verifyItemInList(museumData.title())
                .openMuseum(museumData.title())
                .verifyViewMuseumContent(museumData.toMuseumJson());

    }
}
