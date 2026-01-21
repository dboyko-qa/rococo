package qa.dboyko.rococo.pages.artists;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import io.qameta.allure.Step;
import qa.dboyko.rococo.enums.LoggedUser;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.pages.ContentBasePage;
import qa.dboyko.rococo.pages.constants.ContentType;
import qa.dboyko.rococo.pages.paintings.EditPaintingPage;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static qa.dboyko.rococo.pages.constants.TextConstants.ADD_PAINTING_TO_ARTIST_TEXT;

public class ViewArtistPage extends ContentBasePage<ViewArtistPage> {

    public ViewArtistPage() {super(ContentType.PAINTINGS);}

    private final SelenideElement avatar = $("figure[data-testid=\"avatar\"]");
    private final SelenideElement avatarImage = $("img.avatar-image");
    private final SelenideElement nameHeader = $("header.card-header");
    private final SelenideElement biography = $("p");
    private final SelenideElement paintingsGrid = $("ul.grid");
    private final ElementsCollection paintings = paintingsGrid.$$("li");
    private final SelenideElement editButton = $("button[data-testid=\"edit-artist\"]");

    private SelenideElement getAddPaintingButton() {
        return $x("//button[text()=\"%s\"]".formatted(ADD_PAINTING_TO_ARTIST_TEXT));
    }

    @Step("Verify content of view artist page")
    public ViewArtistPage verifyViewArtistContent(ArtistJson artistJson) {
        nameHeader.shouldBe(visible).shouldHave(text(artistJson.name()));
        biography.shouldBe(visible).shouldHave(text(artistJson.biography()));
        paintingsGrid.shouldBe(artistJson.paintings().size() > 0 ? visible : not(visible));
        return this;
    }

    @Step("Verify the list of painting for the artist")
    public ViewArtistPage verifyPaintingsForArtist(ArtistJson artistJson) {
        scrollToLastItem();
        paintings.shouldHave(CollectionCondition.textsInAnyOrder(
                artistJson.paintings().stream().map(p -> p.title()).toList()));
        return this;
    }

    @Step("Verify that edit button is available or not for the user {0}")
    public ViewArtistPage verifyEditButtonForUser(LoggedUser user) {
        WebElementCondition visibleCondition = user == LoggedUser.USER_LOGGED_IN ? visible : not(visible);
        editButton.shouldBe(visibleCondition);
        getAddPaintingButton().shouldBe(visibleCondition);
        return this;
    }

    @Step("Open edit artist page")
    public EditArtistPage editArtist() {
        editButton.click();
        return new EditArtistPage();
    }

    @Step("Verify the list of paintings for the artist")
    public ViewArtistPage verifyPaintingsList(List<String> expectedPaintingsTitles) {
        scrollToLastItem();
        paintings.shouldHave(CollectionCondition.exactTextsCaseSensitiveInAnyOrder(expectedPaintingsTitles));
        return this;
    }

    @Step("Verify that the painting is in the list")
    public ViewArtistPage verifyPaintingInList(String paintingTitle) {
        scrollToLastItem();
        paintings.shouldHave(CollectionCondition.itemWithText(paintingTitle));
        return this;
    }

    @Step("Open add painting page for the artist")
    public EditPaintingPage openAddPaintingForArtist() {
        nameHeader.shouldNotHave(text("undefined"));
        getAddPaintingButton().click();
        return new EditPaintingPage();
    }
}
