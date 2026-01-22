package uiTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.model.sitedata.UserData;
import qa.dboyko.rococo.pages.MainPage;

@WebTest
@DisplayName("UI tests: user page")
public class UserPageTest {

    @Test
    @User(newUser = true)
    @ApiLogin
    @DisplayName("Verify Profile dialog content for new user")
    void verifyForNewUser(UserdataJson user) {
        new MainPage()
                .getMainMenu().openUserPage()
                .verifyContent(user);
    }

    @Test
    @User(newUser = true)
    @ApiLogin
    @DisplayName("Verify that information can be updated")
    void updateUserTest(UserdataJson user) {
        UserData updateUser = user.toUserData().update();
        new MainPage()
                .getMainMenu().openUserPage()
                .verifyContent(user)
                .updateUserdata(updateUser)
                .getMainMenu().openUserPage()
                .verifyContent(updateUser.toUserdataJson());
    }

    @Test
    @User(username = "user")
    @ApiLogin
    @DisplayName("Verify logout")
    void verifyLogout() {
        new MainPage()
                .getMainMenu().openUserPage()
                .logout()
                .getMainMenu().verifyLoginButton();

    }

}
