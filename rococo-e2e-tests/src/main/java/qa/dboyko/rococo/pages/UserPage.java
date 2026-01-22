package qa.dboyko.rococo.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.mifmif.common.regex.Main;
import io.qameta.allure.Step;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.model.sitedata.UserData;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static qa.dboyko.rococo.pages.constants.TextConstants.HEADER;
import static qa.dboyko.rococo.pages.constants.TextConstants.*;

public class UserPage extends BasePage<UserPage> {
    private final SelenideElement mainElement = $("div[data-testid='modal-backdrop']");
    private final SelenideElement usernameElement = mainElement.$("h4");
    private final SelenideElement logoutButton = mainElement.$(".btn.variant-ghost");
    private final SelenideElement closeButton = mainElement.$(".btn.variant-ringed");
    private final SelenideElement submitButton = mainElement.$("button[type='submit']");
    private final SelenideElement fileInput = mainElement.$("input[type='file']");
    private final SelenideElement fileInputLabel = mainElement.$x(".//input[@type='file']/preceding-sibling::span");
    private final SelenideElement firstnameInput = mainElement.$("input[name='firstname']");
    private final SelenideElement firstnameLabel = mainElement.$x(".//input[@name='firstname']/preceding-sibling::span");
    private final SelenideElement surnameInput = mainElement.$("input[name='surname']");
    private final SelenideElement surnameLabel = mainElement.$x(".//input[@name='surname']/preceding-sibling::span");
    private final SelenideElement header = mainElement.$("header");

    @Step("Verify that user {0} is logged")
    public UserPage verifyLoggedUser(String username) {
        usernameElement.shouldHave(text(username));
        return this;
    }

    @Step("Verify content of user page")
    public UserPage verifyContent(UserdataJson user) {
        header.shouldHave(text(HEADER)).shouldBe(visible);
        logoutButton.shouldBe(visible).shouldHave(text(LOGOUT_BUTTON_TEXT));
        closeButton.shouldBe(visible).shouldHave(text(CLOSE_BUTTON_TEXT));
        submitButton.shouldBe(visible).shouldHave(text(UPDATE_BUTTON_TEXT));
        verifyLoggedUser(user.username());
        fileInputLabel.shouldBe(visible).shouldHave(text(PROFILE_PHOTO_TEXT));
        fileInput.shouldBe(visible);
        firstnameLabel.shouldBe(visible).shouldHave(text(FIRSTNAME_TEXT));
        WebElementCondition firstnameCondition = user.firstname().isBlank() ? empty : have(value(user.firstname()));
        WebElementCondition lastnameCondition = user.lastname().isBlank() ? empty : have(value(user.lastname()));
        firstnameInput.shouldBe(visible, editable).shouldBe(firstnameCondition);
        surnameInput.shouldBe(visible, editable).shouldBe(lastnameCondition);
        surnameLabel.shouldBe(visible).shouldHave(text(SURNAME_TEXT));
        return this;
    }

    @Step("Update userdata in User dialog")
    public MainPage updateUserdata(UserData userData) {
        fileInput.setValue(userData.avatar());
        firstnameInput.setValue(userData.firstname());
        surnameInput.setValue(userData.lastname());
        submitButton.click();
        return new MainPage();
    }

    @Step("Logout")
    public MainPage logout() {
        logoutButton.click();
        return new MainPage();
    }

}
