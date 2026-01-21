package qa.dboyko.rococo.pages.paintings;

import com.codeborne.selenide.SelenideElement;
import qa.dboyko.rococo.apiservice.grpc.ArtistGrpcClient;
import qa.dboyko.rococo.apiservice.grpc.MuseumGrpcClient;
import qa.dboyko.rococo.model.PaintingJson;
import qa.dboyko.rococo.model.sitedata.PaintingData;
import qa.dboyko.rococo.pages.BasePage;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.artists.EditArtistPage;
import qa.dboyko.rococo.pages.components.SelectComponent;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.editable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static qa.dboyko.rococo.pages.constants.TextConstants.*;
import static qa.dboyko.rococo.pages.constants.TextConstants.CLOSE_BUTTON_TEXT;

public class EditPaintingPage extends BasePage<EditPaintingPage> {

    private final SelenideElement mainElement = $("div.card");
    private final SelenideElement header = mainElement.$("header");
    private final SelenideElement image = mainElement.$("img");
    private final SelenideElement titleInput = mainElement.$("input[name='title']");
    private final SelenideElement photoInput = mainElement.$("input[name='content']");
    private final SelenideElement descriptionInput = mainElement.$("textarea[name='description']");
    private final SelectComponent artistSelect = new SelectComponent($("select[name='authorId'"));
    private final SelectComponent museumSelect = new SelectComponent($("select[name='museumId'"));
    private final SelenideElement titleLabel = mainElement.$x(".//input[@name='title']//preceding-sibling::span");
    private final SelenideElement selectArtistLabel = mainElement.$x(".//select[@name='authorId']//preceding-sibling::span");
    private final SelenideElement selectMuseumLabel = mainElement.$x(".//select[@name='museumId']//preceding-sibling::span");
    private final SelenideElement photoInputLabel = mainElement.$x(".//input[@name='content']/preceding-sibling::span");
    private final SelenideElement descriptionInputLabel = mainElement.$x(".//textarea[@name='description']/preceding-sibling::span");
    private final SelenideElement submitButton = mainElement.$("button[type='submit']");
    private final SelenideElement closeButton = mainElement.$("button[type='button']");

    public EditPaintingPage verifyContentCreateDialog() {
        titleInput.shouldBe(visible, editable);
        titleLabel.shouldBe(visible).shouldHave(text(PAINTING_TITLE_TEXT));
        photoInput.shouldBe(visible);
        photoInputLabel.shouldHave(text(UPLOAD_PAINTING_PHOTO_TEXT));
        descriptionInput.shouldBe(visible, editable);
        descriptionInputLabel.shouldBe(visible).shouldHave(text(PAINTING_DESCRIPTION_TEXT));
        selectArtistLabel.shouldBe(visible).shouldHave(text(PAITING_ARTIST_SELECT_TEXT));
        artistSelect.visible().verifyOptions(new ArtistGrpcClient().allArtistsNames());
        selectMuseumLabel.shouldBe(visible).shouldHave(text(PAITING_MUSEUM_SELECT_TEXT));
        museumSelect.visible().verifyOptions(new MuseumGrpcClient().allMuseumsTitles());
        submitButton.shouldBe(visible, clickable).shouldHave(text(ADD_TEXT));
        closeButton.shouldBe(visible, clickable).shouldHave(text(CLOSE_BUTTON_TEXT));
        return this;
    }

    public EditPaintingPage verifyContentEditDialog(PaintingJson paintingJson) {
        header.shouldBe(visible).shouldHave(text(PAINTING_EDIT_HEADER_TEXT));
        image.shouldHave(attribute("src", paintingJson.content()));
        photoInputLabel.shouldBe(visible).shouldHave(text(UPDATE_PAINTING_PHOTO_TEXT));
        photoInput.shouldBe(visible);
        titleInput.shouldBe(visible, editable).shouldHave(value(paintingJson.title()));
        titleLabel.shouldBe(visible).shouldHave(text(PAINTING_TITLE_TEXT));
        selectArtistLabel.shouldBe(visible).shouldHave(text(PAITING_ARTIST_SELECT_TEXT));
        artistSelect.visible().selected(paintingJson.artist().name());
        selectMuseumLabel.shouldBe(visible).shouldHave(text(PAITING_MUSEUM_SELECT_TEXT));
        museumSelect.visible().selected(paintingJson.museum().title());
        descriptionInput.shouldBe(visible, editable).shouldHave(value(paintingJson.description()));
        descriptionInputLabel.shouldBe(visible).shouldHave(text(PAINTING_DESCRIPTION_TEXT));
        submitButton.shouldBe(visible, clickable).shouldHave(text(SAVE_TEXT));
        closeButton.shouldBe(visible, clickable).shouldHave(text(CLOSE_BUTTON_TEXT));
        return this;
    }

    public ViewPaintingPage updatePainting(PaintingData paintingData) {
        addPainting(paintingData);
        return new ViewPaintingPage();
    }

    public void addPainting(PaintingData paintingData, boolean forArtist) {
        photoInput.setValue(paintingData.photoFilePath());
        titleInput.setValue(paintingData.title());
        if (!forArtist) artistSelect.setOption(paintingData.artistName());
        descriptionInput.setValue(paintingData.description());
        museumSelect.setOption(paintingData.museumTitle());
        submitButton.click();
    }

    public MainPage addPainting(PaintingData paintingData) {
        addPainting(paintingData, false);
        return new MainPage();
    }

    public EditArtistPage addPaintingForArtist(PaintingData paintingData) {
        addPainting(paintingData, true);
        return new EditArtistPage();
    }

}
