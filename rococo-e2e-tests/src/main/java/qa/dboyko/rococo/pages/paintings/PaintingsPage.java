package qa.dboyko.rococo.pages.paintings;

import io.qameta.allure.Step;
import qa.dboyko.rococo.pages.ContentBasePage;
import qa.dboyko.rococo.pages.constants.ContentType;

public class PaintingsPage extends ContentBasePage<PaintingsPage> {
    public static final String url = CFG.frontUrl() + "/painting";

    public PaintingsPage() {
        super(ContentType.PAINTINGS);
    }

    @Step("Open painting {0}")
    public ViewPaintingPage openPainting(String name) {
        openItem(name);
        return new ViewPaintingPage();
    }

    @Step("Open add painting dialog")
    public EditPaintingPage openAddPaintingPage() {
        addButton().click();
        return new EditPaintingPage();
    }
}
