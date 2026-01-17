package qa.dboyko.rococo.pageObjects.components;

import com.codeborne.selenide.SelenideElement;
import qa.dboyko.rococo.pageObjects.LoginPage;
import qa.dboyko.rococo.pageObjects.MuseumsPage;
import qa.dboyko.rococo.pageObjects.UserPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static qa.dboyko.rococo.pageObjects.constants.TextConstants.*;

public class MainMenu {
    private final SelenideElement mainElement = $(".app-bar-row-main");
    private final SelenideElement loginButton = mainElement.$x(".//button[text()='%s']".formatted(LOGIN_BUTTON_TEXT));
    private final SelenideElement avatarButton = mainElement.$("figure[data-testid='avatar']");
    private final SelenideElement newUserText = avatarButton.$("text");
    private final SelenideElement paintingButton = mainElement.$("a[href='/painting']");
    private final SelenideElement artistButton = mainElement.$("a[href='/artist']");
    private final SelenideElement museumButton = mainElement.$("a[href='/museum']");
    private final SelenideElement lightSwitcher = mainElement.$(".lightswitch-track");

    public LoginPage login() {
        loginButton.shouldBe(clickable).click();
        return new LoginPage();
    }

    public <T> T login(Class<T> pageClass) {
        loginButton.click();
        try {
            return pageClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create page: " + pageClass, e);
        }
    }

    public MainMenu verifyLoginButton() {
        loginButton.shouldBe(visible, clickable).shouldHave(text(LOGIN_BUTTON_TEXT));
        return this;
    }

    public MainMenu verifyAvatarForNewUser() {
        avatarButton.should(visible);
        newUserText.should(have(text("AB")));
        return this;
    }

    public MainMenu verifyAvatar() {
        avatarButton.should(visible);
        return this;
    }

    public UserPage openUserPage() {
        avatarButton.click();
        return new UserPage();
    }

    public MainMenu verifyAllButtonsAvailable() {
        paintingButton.shouldBe(visible, clickable).shouldHave(text(PAINTINGS));
        artistButton.shouldBe(visible, clickable).shouldHave(text(ARTISTS));
        museumButton.shouldBe(visible, clickable).shouldHave(text(MUSEUMS));
        lightSwitcher.shouldBe(visible, clickable);
        return this;
    }

    public MuseumsPage openMuseums() {
        museumButton.click();
        return new MuseumsPage();
    }
}
