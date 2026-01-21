package qa.dboyko.rococo.pages.museums;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import qa.dboyko.rococo.enums.LoggedUser;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.pages.BasePage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class ViewMuseumPage extends BasePage<ViewMuseumPage> {
    public static final String url = CFG.frontUrl() + "/museum/%s";

    private final SelenideElement header = $("header.card-header");
    private final SelenideElement address = $("div.text-center");

    private final SelenideElement image = $("img");
    private final SelenideElement editButton = $("button[data-testid=\"edit-museum\"]");

    private SelenideElement getImageElement(String altText) {
        return $("img[alt=\"%s\"]".formatted(altText));
    }

    public ViewMuseumPage verifyViewMuseumContent(MuseumJson museum) {
        header.shouldHave(text(museum.title()));
        address.shouldHave(text(museum.geo().country().name() + ", " + museum.geo().city()));
        getDivByText(museum.description()).shouldBe(visible);
        getImageElement(museum.title()).shouldHave(attribute("src", museum.photo()));
        return this;
    }

    public ViewMuseumPage verifyEditButtonForUser(LoggedUser user) {
        WebElementCondition visibleCondition = user == LoggedUser.USER_LOGGED_IN ? visible : not(visible);
        editButton.shouldBe(visibleCondition);
        return this;
    }

    public EditMuseumPage editMuseum() {
        editButton.click();
        return new EditMuseumPage();
    }
}
