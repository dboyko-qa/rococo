package qa.dboyko.rococo.pages.components;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

@ParametersAreNonnullByDefault
public class SelectComponent {
    private final SelenideElement base;

    public SelectComponent(SelenideElement baseElement) {
        this.base = baseElement;
    }

    SelectComponent() {
        this.base = $("select");
    }

    private ElementsCollection getItems() {
        return base.$$("option");
    }

    private SelenideElement itemElement(String countryName) {
        String itemLocator = "//select//option[text() = \"%s\"]";
        return $x(itemLocator.formatted(countryName));
    }

    @Step("Verify that option {0} is selected")
    public SelectComponent selected(String optionSelected) {
        scrollToLastItem();
        itemElement(optionSelected).shouldHave(cssValue("background-color", "rgba(230, 200, 51, 1)"));
        itemElement(optionSelected).shouldHave(cssValue("color", "rgba(0, 0, 0, 1)"));
        return this;
    }

    @Step("Select option {0}")
    public SelectComponent setOption(String option) {
        scrollToLastItem();
        itemElement(option).scrollIntoView(true).click();
        return this;
    }

    @Step("Verify that all options are in the list")
    public SelectComponent verifyOptions(List<String> expected) {
        scrollToLastItem();
        getItems().shouldHave(CollectionCondition.exactTextsCaseSensitiveInAnyOrder(expected));
        return this;
    }

    @Step("Verify component is visible")
    public SelectComponent visible() {
        base.shouldBe(visible);
        return this;
    }

    @Step("Scroll to the end of the list")
    public SelectComponent scrollToLastItem() {

        int previousSize = 0;

        while (true) {
            base.shouldBe(visible, Duration.ofSeconds(20));
            base.hover();
            int currentSize = getItems().size();

            if (currentSize == previousSize) {
                break;
            }

            previousSize = currentSize;

            getItems().last().scrollIntoView(true);
            sleep(800);
        }
        return this;
    }
}
