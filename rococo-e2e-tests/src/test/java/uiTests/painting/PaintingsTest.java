package uiTests.painting;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.enums.LoggedUser;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.Painting;
import qa.dboyko.rococo.extensions.annotations.TestPainting;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.PaintingJson;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.paintings.PaintingsPage;

import static qa.dboyko.rococo.enums.LoggedUser.GUEST;
import static qa.dboyko.rococo.enums.LoggedUser.USER_LOGGED_IN;

@WebTest
@DisplayName("UI tests: paintings page")
public class PaintingsTest {

    @Test
    @DisplayName("Verify page for guest")
    void verifyPaintingsPageForGuest() {
        Selenide.open(PaintingsPage.url, PaintingsPage.class)
                .verifyAddButtonVisibility(LoggedUser.GUEST)
                .verifyMainContent();
    }

    @Test
    @Painting
    @DisplayName("Paintings should be visible for guest")
    void verifyPaintingVisibleForGuest(@TestPainting PaintingJson paintingJson) {
        Selenide.open(PaintingsPage.url, PaintingsPage.class)
                .scrollToLastItem()
                .verifyItemInList(paintingJson.title())
                .findItem(paintingJson.title())
                .verifyItemInList(paintingJson.title())
                .openPainting(paintingJson.title())
                .verifyViewPaintingContent(paintingJson)
                .verifyEditButtonForUser(GUEST);
    }

    @Test
    @Painting
    @User(username = "user")
    @ApiLogin
    @DisplayName("Paintings should be visible and editable for logged user")
    void verifyPaintingVisibleForUser(@TestPainting PaintingJson paintingJson) {
        new MainPage().openPaintings()
                .scrollToLastItem()
                .verifyItemInList(paintingJson.title())
                .findItem(paintingJson.title())
                .verifyItemInList(paintingJson.title())
                .openPainting(paintingJson.title())
                .verifyViewPaintingContent(paintingJson)
                .verifyEditButtonForUser(USER_LOGGED_IN);
    }
}
