package qa.dboyko.rococo.pages;

import com.codeborne.selenide.SelenideElement;
import qa.dboyko.rococo.config.Config;
import qa.dboyko.rococo.pages.museums.MuseumsPage;
import qa.dboyko.rococo.pages.artists.ArtistsPage;
import qa.dboyko.rococo.pages.components.MainMenu;
import qa.dboyko.rococo.pages.paintings.PaintingsPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();
    protected final MainMenu mainMenu = new MainMenu();
    protected final SelenideElement popupMessage = $("div.toast div");

    public SelenideElement getDivByText(String text) {
        return $x("//div[text()=\"%s\"]".formatted(text));
    }

    protected T self() {
        return (T) this;
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }


    public MuseumsPage openMuseums() {
        getMainMenu().openMuseums();
        return new MuseumsPage();
    }

    public ArtistsPage openArtists() {
        getMainMenu().openArtists();
        return new ArtistsPage();
    }

    public PaintingsPage openPaintings() {
        getMainMenu().openPaintings();
        return new PaintingsPage();
    }

    public <T> T verifyPopupMessage(String message, Class<T> pageClass) {
        popupMessage.shouldHave(text(message));
        try {
            return pageClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create page: " + pageClass, e);
        }
    }

}
