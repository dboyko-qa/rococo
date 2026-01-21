package qa.dboyko.rococo.pages.paintings;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import qa.dboyko.rococo.enums.LoggedUser;
import qa.dboyko.rococo.model.PaintingJson;
import qa.dboyko.rococo.pages.BasePage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class ViewPaintingPage extends BasePage<ViewPaintingPage> {
    public static final String url = CFG.frontUrl() + "/painting/%s";

    private final SelenideElement header = $("header.card-header");
    private final SelenideElement artist = $("div.text-center");

    private final SelenideElement editButton = $("button[data-testid=\"edit-painting\"]");

    private SelenideElement getImageElement(String altText) {
        return $("img[alt=\"%s\"]".formatted(altText));
    }

    public ViewPaintingPage verifyViewPaintingContent(PaintingJson paintingJson) {
        header.shouldHave(text(paintingJson.title()));
        artist.shouldBe(visible).shouldHave(text(paintingJson.artist().name()));
        getDivByText(paintingJson.description()).shouldBe(visible);
        getImageElement(paintingJson.title()).shouldHave(attribute("src", paintingJson.content()));
        return this;
    }

    public ViewPaintingPage verifyEditButtonForUser(LoggedUser user) {
        WebElementCondition visibleCondition = user == LoggedUser.USER_LOGGED_IN ? visible : not(visible);
        editButton.shouldBe(visibleCondition);
        return this;
    }

    public EditPaintingPage editPainting() {
        editButton.click();
        return new EditPaintingPage();
    }
}
