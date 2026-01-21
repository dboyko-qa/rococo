package qa.dboyko.rococo.pages.museums;

import qa.dboyko.rococo.pages.ContentBasePage;
import qa.dboyko.rococo.pages.constants.ContentType;

public class MuseumsPage extends ContentBasePage<MuseumsPage> {
    public static final String url = CFG.frontUrl() + "/museum";

    public MuseumsPage() {
        super(ContentType.MUSEUM);
    }

    public ViewMuseumPage openMuseum(String name) {
        openItem(name);
        return new ViewMuseumPage();
    }

    public EditMuseumPage openAddMuseumPage() {
        addButton().click();
        return new EditMuseumPage();
    }
}
