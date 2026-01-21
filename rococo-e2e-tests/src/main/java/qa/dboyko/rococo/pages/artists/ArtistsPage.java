package qa.dboyko.rococo.pages.artists;

import io.qameta.allure.Step;
import qa.dboyko.rococo.pages.ContentBasePage;
import qa.dboyko.rococo.pages.constants.ContentType;

public class ArtistsPage extends ContentBasePage<ArtistsPage> {
    public static final String url = CFG.frontUrl() + "/artist";

    public ArtistsPage() {
        super(ContentType.ARTIST);
    }

    @Step("Open artist {0}")
    public ViewArtistPage openArtist(String name) {
        openItem(name);
        return new ViewArtistPage();
    }

    @Step("Open Add artist page")
    public EditArtistPage openAddArtistPage() {
        addButton().click();
        return new EditArtistPage();
    }
}
