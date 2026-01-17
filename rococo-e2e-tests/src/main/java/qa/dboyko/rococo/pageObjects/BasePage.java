package qa.dboyko.rococo.pageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import qa.dboyko.rococo.config.Config;import qa.dboyko.rococo.pageObjects.components.MainMenu;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();
    protected final MainMenu mainMenu = new MainMenu();

    protected final SelenideElement header2 = $("h2");
    protected final SelenideElement searchInput = $("input[type='search']");
    protected final SelenideElement searchButton = $x("//input[@type='search']//following-sibling::button");
    protected final SelenideElement itemsGrid = $("ul.grid");
    protected final ElementsCollection items = itemsGrid.$$("li");

    public SelenideElement itemByName(String name) {
        return $x("//li//img[contains(@alt,\"%s\")]".formatted(name));
    }

    public SelenideElement getDivByText(String text) {
        return $x("//div[text()=\"%s\"]".formatted(text));
    }

    public T openItem(String name) {
        itemByName(name).click();
        return (T) this;
    }

    public MainMenu getMainMenu() {
        return mainMenu;
    }

    public T verifyGridNotEmpty() {
        items.shouldHave(sizeGreaterThan(0));
        return (T) this;
    }

    public T findItem(String name) {
        searchInput.setValue(name);
        searchButton.click();
        return (T) this;
    }

    public T verifyItemInList(String name) {
        scrollToLastItem();
        itemByName(name).shouldBe(visible);
        return (T) this;
    }

    public T scrollToLastItem() {

        int previousSize = 0;

        while (true) {
            itemsGrid.shouldBe(visible, Duration.ofSeconds(20));
            int currentSize = items.size();

            if (currentSize == previousSize) {
                break;
            }

            previousSize = currentSize;

            items.last().scrollIntoView(true);
            sleep(800);
        }
        return (T) this;
    }

    public MuseumsPage openMuseums() {
        getMainMenu().openMuseums();
        return new MuseumsPage();
    }
}
