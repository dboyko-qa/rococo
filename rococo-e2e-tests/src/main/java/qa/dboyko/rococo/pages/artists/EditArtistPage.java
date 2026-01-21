package qa.dboyko.rococo.pages.artists;

import com.codeborne.selenide.SelenideElement;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.model.sitedata.ArtistData;
import qa.dboyko.rococo.pages.BasePage;

import static com.codeborne.selenide.Condition.clickable;
import static com.codeborne.selenide.Condition.editable;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static qa.dboyko.rococo.pages.constants.TextConstants.*;

public class EditArtistPage extends BasePage<EditArtistPage> {
    private final SelenideElement mainElement = $("div.card");
    private final SelenideElement header = $("header");
    private final SelenideElement avatarImage = $("img.avatar-image");
    private final SelenideElement photoInput = mainElement.$("input[name='photo']");
    private final SelenideElement photoInputLabel = mainElement.$x(".//input[@name='photo']/preceding-sibling::span");
    private final SelenideElement nameInput = mainElement.$("input[name='name']");
    private final SelenideElement nameLabel = mainElement.$x(".//input[@name='name']//preceding-sibling::span");
    private final SelenideElement biographyInput = mainElement.$("textarea[name='biography']");
    private final SelenideElement biographyLabel = mainElement.$x(".//textarea[@name='biography']//preceding-sibling::span");
    private final SelenideElement submitButton = mainElement.$("button[type='submit']");
    private final SelenideElement closeButton = mainElement.$("button[type='button']");

    public EditArtistPage verifyContentEditDialog(ArtistJson artistJson) {
        header.shouldBe(visible).shouldHave(text(EDIT_ARTIST_TEXT));
        nameInput.shouldBe(visible, editable).shouldHave(value(artistJson.name()));
        nameLabel.shouldBe(visible).shouldHave(text(ARTIST_NAME_TEXT));
        photoInput.shouldBe(visible);
        photoInputLabel.shouldHave(text(UPDATE_ARTIST_PHOTO_TEXT));
        biographyInput.shouldBe(visible, editable).shouldHave(value(artistJson.biography()));
        biographyLabel.shouldBe(visible).shouldHave(text(ARTIST_BIOGRAPHY_TEXT));
        submitButton.shouldBe(visible, clickable).shouldHave(text(SAVE_TEXT));
        closeButton.shouldBe(visible, clickable).shouldHave(text(CLOSE_BUTTON_TEXT));
        return this;
    }

    public EditArtistPage verifyContentCreateDialog() {
        nameInput.shouldBe(visible, editable);
        nameLabel.shouldBe(visible).shouldHave(text(ARTIST_NAME_TEXT));
        photoInput.shouldBe(visible);
        photoInputLabel.shouldBe(visible);
        biographyInput.shouldBe(visible, editable);
        biographyLabel.shouldBe(visible).shouldHave(text(ARTIST_BIOGRAPHY_TEXT));
        submitButton.shouldBe(visible, clickable).shouldHave(text(ADD_TEXT));
        closeButton.shouldBe(visible, clickable).shouldHave(text(CLOSE_BUTTON_TEXT));
        return this;
    }

    public ViewArtistPage updateArtist(ArtistData artistData) {
        addArtist(artistData);
        return new ViewArtistPage();
    }

    public ArtistsPage addArtist(ArtistData artistData) {
        nameInput.setValue(artistData.name());
        photoInput.setValue(artistData.photoFilePath());
        biographyInput.setValue(artistData.biography());
        submitButton.click();
        return new ArtistsPage();
    }
}
