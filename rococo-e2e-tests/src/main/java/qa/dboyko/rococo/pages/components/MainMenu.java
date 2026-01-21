package qa.dboyko.rococo.pages.components;

import com.codeborne.selenide.SelenideElement;
import qa.dboyko.rococo.pages.LoginPage;
import qa.dboyko.rococo.pages.constants.LightDarkMode;
import qa.dboyko.rococo.pages.museums.MuseumsPage;
import qa.dboyko.rococo.pages.UserPage;
import qa.dboyko.rococo.pages.artists.ArtistsPage;
import qa.dboyko.rococo.pages.paintings.PaintingsPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static qa.dboyko.rococo.pages.constants.TextConstants.*;

public class MainMenu {
    private final SelenideElement mainElement = $(".app-bar-row-main");
    private final SelenideElement loginButton = mainElement.$x(".//button[text()='%s']".formatted(LOGIN_BUTTON_TEXT));
    private final SelenideElement avatarButton = mainElement.$("figure[data-testid='avatar']");
    private final SelenideElement newUserText = avatarButton.$("text");
    private final SelenideElement paintingButton = mainElement.$("a[href='/painting']");
    private final SelenideElement artistButton = mainElement.$("a[href='/artist']");
    private final SelenideElement museumButton = mainElement.$("a[href='/museum']");
    private final SelenideElement lightSwitcher = mainElement.$(".lightswitch-track");
    private final SelenideElement root = $("html");

    public MainMenu switchDayNightMode(LightDarkMode mode) {
        if (!lightSwitcher.attr("title").equals(mode.getSwitcherTitle())) lightSwitcher.click();
        return this;
    }

    public void verifyLightMode() {
        root.shouldHave(attribute("class", ""));
    }

    public void verifyDarkMode() {
        root.shouldHave(attribute("class", "dark"));
    }

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
        paintingButton.shouldBe(visible, clickable).shouldHave(text(PAINTINGS_HEADER));
        artistButton.shouldBe(visible, clickable).shouldHave(text(ARTISTS_HEADER));
        museumButton.shouldBe(visible, clickable).shouldHave(text(MUSEUMS_HEADER));
        lightSwitcher.shouldBe(visible, clickable);
        return this;
    }

    public MuseumsPage openMuseums() {
        museumButton.click();
        return new MuseumsPage();
    }

    public ArtistsPage openArtists() {
        artistButton.click();
        return new ArtistsPage();
    }

    public PaintingsPage openPaintings() {
        paintingButton.click();
        return new PaintingsPage();
    }
}
