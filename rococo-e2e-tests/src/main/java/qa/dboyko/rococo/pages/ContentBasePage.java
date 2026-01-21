package qa.dboyko.rococo.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import lombok.SneakyThrows;
import qa.dboyko.rococo.enums.LoggedUser;
import qa.dboyko.rococo.pages.constants.ContentType;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Selenide.*;

public abstract class ContentBasePage<T extends ContentBasePage<T>> extends BasePage<T> {
    protected ContentType type;

    protected ContentBasePage(ContentType type) {this.type = type;}
    protected final SelenideElement header2 = $("h2");
    protected final SelenideElement searchInput = $("input[type='search']");
    protected final SelenideElement searchButton = $x("//input[@type='search']//following-sibling::button");
    protected final SelenideElement itemsGrid = $("ul.grid");
    protected final ElementsCollection items = itemsGrid.$$("li");

    public SelenideElement itemByName(String name) {
        return $x(type.getOptionLocator().formatted(name));
    }

    public T openItem(String name) {
        scrollToLastItem();
        itemByName(name).click();
        return self();
    }

    @SneakyThrows
    protected SelenideElement addButton() {
        return $x("//button[text() = '%s']".formatted(type.getTextAddEntity()));
    }

    public T verifyAddButtonVisibility(LoggedUser user) {
        WebElementCondition condition = user == LoggedUser.USER_LOGGED_IN ? visible : not(visible);
        addButton().shouldBe(condition);
        return self();
    }

    public T verifyMainContent() {
        header2.shouldBe(visible).shouldHave(text(type.getTextHeader()));
        searchInput.shouldBe(visible);
        searchButton.shouldBe(visible, clickable);
        itemsGrid.shouldBe(visible);
        return self();
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

    public T verifyItemInList(String name) {
        scrollToLastItem();
        itemByName(name).shouldBe(visible);
        return (T) this;
    }

}
