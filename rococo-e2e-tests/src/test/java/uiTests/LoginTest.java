package uiTests;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.config.Config;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.pageObjects.LoginPage;
import qa.dboyko.rococo.pageObjects.MainPage;
import qa.dboyko.rococo.utils.RandomDataUtils;

import static qa.dboyko.rococo.pageObjects.constants.TextConstants.BAD_CREDENTIALS;

@WebTest
@DisplayName("UI tests: user login")
public class LoginTest {
    private final Config CFG = Config.getInstance();

    @Test
    @User(newUser = true)
    @DisplayName("Verify successful login")
    void successfulLoginTest(UserdataJson user) {
        Selenide.open(MainPage.url, MainPage.class)
                .getMainMenu().login()
                .userLogin(user.username(), CFG.registrationPassword())
                .getMainMenu().verifyAvatarForNewUser()
                .openUserPage()
                .verifyLoggedUser(user.username());
    }

    @Test
    @User
    @DisplayName("Verify failed login with incorrect password")
    void failedLoginWithIncorrectPassword(UserdataJson user) {
        Selenide.open(MainPage.url, MainPage.class)
                .getMainMenu().login()
                .userLogin(user.username(), CFG.registrationPassword() + "1");
        new LoginPage().verifyOpenedAndContent()
                .verifyFormErrorShown(BAD_CREDENTIALS);
    }

    @Test
    @DisplayName("Verify failed login with incorrect username")
    void failedLoginWithIncorrectUserName() {
        Selenide.open(MainPage.url, MainPage.class)
                .getMainMenu().login()
                .userLogin(RandomDataUtils.randomUsername(), CFG.registrationPassword());
        new LoginPage().verifyOpenedAndContent()
                .verifyFormErrorShown(BAD_CREDENTIALS);
    }
}
