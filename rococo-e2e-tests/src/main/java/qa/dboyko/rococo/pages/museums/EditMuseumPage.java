package qa.dboyko.rococo.pages.museums;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.model.sitedata.MuseumData;
import qa.dboyko.rococo.pages.BasePage;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.components.SelectComponent;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static qa.dboyko.rococo.pages.constants.TextConstants.*;

public class EditMuseumPage extends BasePage<EditMuseumPage> {

    private final SelenideElement mainElement = $("div.card");
    private final SelenideElement titleInput = mainElement.$("input[name='title']");
    private final SelenideElement cityInput = mainElement.$("input[name='city']");
    private final SelenideElement photoInput = mainElement.$("input[name='photo']");
    private final SelenideElement descriptionInput = mainElement.$("textarea[name='description']");
    private final SelenideElement titleLabel = mainElement.$x(".//input[@name='title']//preceding-sibling::span");
    private final SelenideElement selectCountryLabel = mainElement.$x(".//select[@name='countryId']//preceding-sibling::span");
    private final SelenideElement cityInputLabel = mainElement.$x(".//input[@name='city']//preceding-sibling::span");
    private final SelenideElement photoInputLabel = mainElement.$x(".//input[@name='photo']/preceding-sibling::span");
    private final SelenideElement descriptionInputLabel = mainElement.$x(".//textarea[@name='description']/preceding-sibling::span");
    private final SelectComponent countrySelect = new SelectComponent($("select[name='countryId']"));
    private final SelenideElement submitButton = mainElement.$("button[type='submit']");
    private final SelenideElement closeButton = mainElement.$("button[type='button']");

    @Step("Verify that content of empty Create Museum dialog is correct")
    public EditMuseumPage verifyContentCreateDialog() {
        titleInput.shouldBe(visible, editable);
        titleLabel.shouldBe(visible).shouldHave(text(MUSEUM_TITLE_TEXT));
        countrySelect.visible();
        selectCountryLabel.shouldBe(visible).shouldHave(text(SET_COUNTRY_TEXT));
        cityInput.shouldBe(visible, editable);
        cityInputLabel.shouldHave(text(SET_CITY_TEXT));
        photoInput.shouldBe(visible);
        photoInputLabel.shouldHave(text(MUSEUM_PHOTO_TEXT));
        descriptionInput.shouldBe(visible, editable);
        descriptionInputLabel.shouldBe(visible).shouldHave(text(MUSEUM_DESCRIPTION_TEXT));
        submitButton.shouldBe(visible, clickable).shouldHave(text(ADD_TEXT));
        closeButton.shouldBe(visible, clickable).shouldHave(text(CLOSE_BUTTON_TEXT));
        return this;
    }

    @Step("Verify that content of Edit Museum dialog is correct")
    public EditMuseumPage verifyContentEditDialog(MuseumJson museumJson) {
        titleInput.shouldBe(visible, editable).shouldHave(value(museumJson.title()));
        titleLabel.shouldBe(visible).shouldHave(text(MUSEUM_TITLE_TEXT));
        countrySelect.visible().selected(museumJson.geo().country().name());
        selectCountryLabel.shouldBe(visible).shouldHave(text(SET_COUNTRY_TEXT));
        cityInput.shouldBe(visible, editable).shouldHave(value(museumJson.geo().city()));
        cityInputLabel.shouldHave(text(SET_CITY_TEXT));
        photoInput.shouldBe(visible);
        photoInputLabel.shouldHave(text(MUSEUM_PHOTO_TEXT));
        descriptionInput.shouldBe(visible, editable).shouldHave(value(museumJson.description()));
        descriptionInputLabel.shouldBe(visible).shouldHave(text(MUSEUM_DESCRIPTION_TEXT));
        submitButton.shouldBe(visible, clickable).shouldHave(text(SAVE_TEXT));
        closeButton.shouldBe(visible, clickable).shouldHave(text(CLOSE_BUTTON_TEXT));
        return this;
    }

    @Step("Edit Museum")
    public ViewMuseumPage updateMuseum(MuseumData museumData) {
        addMuseum(museumData);
        return new ViewMuseumPage();
    }

    @Step("Add museum")
    public MainPage addMuseum(MuseumData museumData) {
        enterTitle(museumData.title());
        countrySelect.setOption(museumData.countryName());
        enterCity(museumData.city());
        enterDescription(museumData.description());
        uploadPhoto(museumData.photoFilePath());
        submitButton.click();
        return new MainPage();
    }

    public EditMuseumPage enterTitle(String title) {
        titleInput.setValue(title);
        return this;
    }

    public EditMuseumPage enterCity(String city) {
        cityInput.setValue(city);
        return this;
    }

    public EditMuseumPage enterDescription(String description) {
        descriptionInput.setValue(description);
        return this;
    }

    public EditMuseumPage uploadPhoto(String fullFilePath) {
        photoInput.setValue(fullFilePath);
        return this;
    }



}
