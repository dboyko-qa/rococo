package uiTests;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.pageObjects.MainPage;

@WebTest
public class UserPageTest {

    @Test
    @User(newUser = true)
    @ApiLogin
    @DisplayName("Verify Profile dialog content for new user")
    void verifyForNewUser(UserdataJson user) {
        Selenide.open(MainPage.url, MainPage.class)
                .getMainMenu().openUserPage()
                .verifyContent(user);
    }

}
