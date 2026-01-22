package uiTests.museum;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.Museum;
import qa.dboyko.rococo.extensions.annotations.TestMuseum;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.model.sitedata.MuseumData;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.museums.ViewMuseumPage;

import static qa.dboyko.rococo.enums.Country.getRandomCountry;
import static qa.dboyko.rococo.pages.constants.TextConstants.MUSEUM_UPDATE_MESSAGE;
import static qa.dboyko.rococo.utils.RandomDataUtils.getRandomMuseumFile;
import static qa.dboyko.rococo.utils.RandomDataUtils.randomCity;

@WebTest
@DisplayName("UI tests: update museum page")
public class UpdateMuseumTest {

    @Test
    @Museum(createNew = true)
    @User(username = "user")
    @ApiLogin
    @DisplayName("Museum can be successfully updated")
    void verifyEditMuseumSuccessful(@TestMuseum MuseumJson museumJson) {
        MuseumData updateMuseumData = new MuseumData(
                museumJson.title() + "1",
                museumJson.description() + "1",
                getRandomMuseumFile().toString(),
                randomCity(),
                getRandomCountry().getName()
        );
        new MainPage().openMuseums()
                .openMuseum(museumJson.title())
                .editMuseum()
                .verifyContentEditDialog(museumJson)
                .updateMuseum(updateMuseumData)
                .verifyPopupMessage(MUSEUM_UPDATE_MESSAGE.formatted(updateMuseumData.title()), ViewMuseumPage.class)
                .verifyViewMuseumContent(updateMuseumData.toMuseumJson());

    }
}
