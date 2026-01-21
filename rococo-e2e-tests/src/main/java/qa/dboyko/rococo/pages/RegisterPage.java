package qa.dboyko.rococo.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static qa.dboyko.rococo.pages.constants.TextConstants.PASSWORD_LABEL;
import static qa.dboyko.rococo.pages.constants.TextConstants.*;
import static qa.dboyko.rococo.pages.constants.TextConstants.USERNAME_LABEL;

public class RegisterPage extends BasePage<RegisterPage> {
    public static final String url = CFG.authUrl() + "/register";

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement usernameLabel = $x("//input[@id='username']/preceding-sibling::span");
    private final SelenideElement passwordLabel = $x("//input[@id='password']/preceding-sibling::span");
    private final SelenideElement passwordSubmitLabel = $x("//input[@id='passwordSubmit']/preceding-sibling::span");
    private final SelenideElement formFooter = $(".form__paragraph");
    private final SelenideElement formFooterLink = formFooter.$(".form__link");
    private final SelenideElement usernameErrorMessage = $(".form__error.error__username");


    public RegisterPage enterUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage enterPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage enterSubmitPassword(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    public void registerUser(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        enterSubmitPassword(password);
        submitButton.click();
    }

    public RegisterPage verifyPageContent() {
        usernameLabel.should(allOf(visible, have(text(USERNAME_LABEL))));
        passwordLabel.should(allOf(visible, have(text(PASSWORD_LABEL))));
        passwordSubmitLabel.should(allOf(visible, have(text(PASSWORD_SUBMIT_LABEL))));
        formFooter.should(allOf(visible, have(text(FOOTER_TEXT))));
        formFooterLink.should(allOf(have(text(FOOTER_LINK_TEXT)), attribute("href", LoginPage.pageUrl)));
        usernameInput.should(visible, editable);
        passwordInput.should(visible, editable);
        passwordSubmitInput.should(visible, clickable);
        submitButton.should(exist).should(allOf(visible, have(text(SUBMIT_BUTTON))));
        return this;
    }

    public RegisterPage verifyUsernameError(String username) {
        usernameErrorMessage.should(allOf(visible, have(text(USERS_EXISTS_ERROR.formatted(username)))));
        return this;
    }
}
