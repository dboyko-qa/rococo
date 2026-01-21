package qa.dboyko.rococo.pages;

import com.codeborne.selenide.Selenide;import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    public static final String pageUrl = CFG.authUrl() + "/login";

    private final SelenideElement usernameInput = $("input[name=username]");
    private final SelenideElement passwordInput = $("input[name=password]");
    private final SelenideElement submitButton = $(".form__submit");
    private final SelenideElement formErrorText = $(".form__error.login__error");

    public LoginPage open() {
        Selenide.open(pageUrl);
        return this;
    }

    public LoginPage enterUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public MainPage userLogin(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        submitButton.click();
        return new MainPage();
    }

    public LoginPage verifyOpenedAndContent() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        return this;
    }

    public LoginPage verifyFormErrorShown(String error) {
        formErrorText.shouldHave(text(error));
        return this;
    }

}
