package qa.dboyko.rococo.pageObjects;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import qa.dboyko.rococo.enums.LoggedUser;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$x;
import static qa.dboyko.rococo.pageObjects.constants.TextConstants.ADD_PAINTING_TEXT;
import static qa.dboyko.rococo.pageObjects.constants.TextConstants.PAINTINGS;

public class PaintingsPage extends BasePage<PaintingsPage> {
    public static final String url = CFG.frontUrl() + "/painting";

    private final SelenideElement addPaintingButton = $x("//button[text() = '$s']".formatted(ADD_PAINTING_TEXT));

    public PaintingsPage verifyAddButtonVisibility(LoggedUser user) {
        WebElementCondition condition = user == LoggedUser.USER_LOGGED_IN ? visible : not(visible);
        addPaintingButton.shouldBe(condition);
        return this;
    }

    public PaintingsPage verifyMainContent() {
        header2.shouldBe(visible).shouldHave(text(PAINTINGS));
        searchInput.shouldBe(visible);
        searchButton.shouldBe(visible, clickable);
        itemsGrid.shouldBe(visible);
        return this;
    }
}
