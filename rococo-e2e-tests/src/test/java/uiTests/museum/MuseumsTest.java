package uiTests.museum;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.Museum;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.pageObjects.MainPage;
import qa.dboyko.rococo.pageObjects.MuseumsPage;

import static qa.dboyko.rococo.enums.LoggedUser.GUEST;
import static qa.dboyko.rococo.enums.LoggedUser.USER_LOGGED_IN;

@WebTest
public class MuseumsTest {

    @Test
    @DisplayName("Verify page for guest")
    void verifyMuseumsPageForGuest() {
        Selenide.open(MuseumsPage.url, MuseumsPage.class)
                .verifyAddButtonVisibility(GUEST)
                .verifyMainContent();
    }

    @Test
    @Museum
    @DisplayName("Museums should be visible for guest")
    void verifyMuseumVisible(MuseumJson museumJson) {
        Selenide.open(MuseumsPage.url, MuseumsPage.class)
                .scrollToLastItem()
                .verifyItemInList(museumJson.title())
                .findItem(museumJson.title())
                .verifyItemInList(museumJson.title())
                .openMuseum(museumJson.title())
                .verifyViewMuseumContent(museumJson)
                .verifyEditButtonForUser(GUEST);
    }

    @Test
    @Museum
    @User(username = "user")
    @ApiLogin
    @DisplayName("Museums should be visible and editable for logged user")
    void verifyMuseumVisibleForUser(MuseumJson museumJson) {
        new MainPage().openMuseums()
                .scrollToLastItem()
                .verifyItemInList(museumJson.title())
                .findItem(museumJson.title())
                .verifyItemInList(museumJson.title())
                .openMuseum(museumJson.title())
                .verifyViewMuseumContent(museumJson)
                .verifyEditButtonForUser(USER_LOGGED_IN);
    }

}
