package qa.dboyko.rococo.pageObjects;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import qa.dboyko.rococo.enums.LoggedUser;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Selenide.$x;
import static qa.dboyko.rococo.pageObjects.constants.TextConstants.*;

public class MuseumsPage extends BasePage<MuseumsPage> {
    public static final String url = CFG.frontUrl() + "/museum";

    private final SelenideElement addButton = $x("//button[text() = '$s']".formatted(ADD_MUSEUM_TEXT));

    public MuseumsPage verifyAddButtonVisibility(LoggedUser user) {
        WebElementCondition condition = user == LoggedUser.USER_LOGGED_IN ? visible : not(visible);
        addButton.shouldBe(condition);
        return this;
    }

    public MuseumsPage verifyMainContent() {
        header2.shouldBe(visible).shouldHave(text(MUSEUMS));
        searchInput.shouldBe(visible);
        searchButton.shouldBe(visible, clickable);
        itemsGrid.shouldBe(visible);
        return this;
    }

    public ViewMuseumPage openMuseum(String name) {
        openItem(name);
        return new ViewMuseumPage();
    }

}
