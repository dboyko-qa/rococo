package qa.dboyko.rococo.pages.paintings;

import qa.dboyko.rococo.pages.ContentBasePage;
import qa.dboyko.rococo.pages.constants.ContentType;

public class PaintingsPage extends ContentBasePage<PaintingsPage> {
    public static final String url = CFG.frontUrl() + "/painting";

    public PaintingsPage() {
        super(ContentType.PAINTINGS);
    }

    public ViewPaintingPage openPainting(String name) {
        openItem(name);
        return new ViewPaintingPage();
    }

    public EditPaintingPage openAddPaintingPage() {
        addButton().click();
        return new EditPaintingPage();
    }
}
