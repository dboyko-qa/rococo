package qa.dboyko.rococo.pages.museums;

import io.qameta.allure.Step;
import qa.dboyko.rococo.pages.ContentBasePage;
import qa.dboyko.rococo.pages.constants.ContentType;

public class MuseumsPage extends ContentBasePage<MuseumsPage> {
    public static final String url = CFG.frontUrl() + "/museum";

    public MuseumsPage() {
        super(ContentType.MUSEUM);
    }

    @Step("Open museum {0}")
    public ViewMuseumPage openMuseum(String name) {
        openItem(name);
        return new ViewMuseumPage();
    }

    @Step("Open add museum page")
    public EditMuseumPage openAddMuseumPage() {
        addButton().click();
        return new EditMuseumPage();
    }
}
