package uiTests;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.enums.LoggedUser;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.pageObjects.PaintingsPage;

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
}
