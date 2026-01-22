package uiTests;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.pages.MainPage;

@WebTest
@DisplayName("UI tests: main page")
public class MainPageTest {

    @Test
    @DisplayName("Verify Main page content for guest")
    void verifyMainPageNoUser() {
        Selenide.open(MainPage.url, MainPage.class)
                .getMainMenu().verifyLoginButton().verifyAllButtonsAvailable();
        new MainPage().verifyContent();
    }

    @Test
    @User(newUser = true)
    @ApiLogin
    @DisplayName("Verify Main page content for logged user")
    void verifyMainPageUserLogged() {
        new MainPage()
                .getMainMenu().verifyAvatarForNewUser().verifyAllButtonsAvailable();
        new MainPage().verifyContent();
    }

    @Test
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify Main page content for logged user")
    void verifyMainPageExistingUserLogged() {
        new MainPage()
                .verifyContent()
                .getMainMenu().verifyAllButtonsAvailable().verifyAvatar();
    }

}
