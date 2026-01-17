package uiTests;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.pageObjects.MainPage;
import qa.dboyko.rococo.pageObjects.RegisterPage;
import qa.dboyko.rococo.utils.RandomDataUtils;

import static qa.dboyko.rococo.extensions.UserExtension.DEFAULT_PASSWORD;

@WebTest
@DisplayName("UI tests: user registration")
public class RegistrationTest {

    @Test
    @DisplayName("Verify that user can successfully register")
    void successfulRegistrationTest() {
        String username = RandomDataUtils.randomUsername();
        Selenide.open(RegisterPage.url, RegisterPage.class)
                .registerUser(username, DEFAULT_PASSWORD);

        Selenide.open(MainPage.url, MainPage.class)
                .getMainMenu().login()
                .userLogin(username, DEFAULT_PASSWORD);
    }

    @Test
    @DisplayName("Verify page content")
    void validateRegistrationPageContent() {
        Selenide.open(RegisterPage.url, RegisterPage.class).
                verifyPageContent();
    }

    @Test
    @User
    @DisplayName("Message should be shown when user already exists")
    void userAlreadyExists(UserdataJson user) {
        Selenide.open(RegisterPage.url, RegisterPage.class)
                .registerUser(user.username(), DEFAULT_PASSWORD);
        new RegisterPage().verifyUsernameError(user.username());
    }
}
