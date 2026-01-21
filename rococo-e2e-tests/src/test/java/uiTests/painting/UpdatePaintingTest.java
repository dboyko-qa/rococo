package uiTests.painting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.apiservice.grpc.ArtistGrpcClient;
import qa.dboyko.rococo.apiservice.grpc.MuseumGrpcClient;
import qa.dboyko.rococo.extensions.annotations.ApiLogin;
import qa.dboyko.rococo.extensions.annotations.Painting;
import qa.dboyko.rococo.extensions.annotations.TestPainting;
import qa.dboyko.rococo.extensions.annotations.User;
import qa.dboyko.rococo.extensions.annotations.meta.WebTest;
import qa.dboyko.rococo.model.PaintingJson;
import qa.dboyko.rococo.model.sitedata.PaintingData;
import qa.dboyko.rococo.pages.MainPage;
import qa.dboyko.rococo.pages.paintings.ViewPaintingPage;

import static qa.dboyko.rococo.pages.constants.TextConstants.PAINTING_UPDATE_MESSAGE;
import static qa.dboyko.rococo.utils.RandomDataUtils.getRandomPaintingFile;

@WebTest
public class UpdatePaintingTest {

    @Test
    @Painting(createNew = true)
    @User(username = "user")
    @ApiLogin
    @DisplayName("Painting can be successfully updated")
    void verifyEditPaintingSuccessful(@TestPainting PaintingJson paintingJson) {
        PaintingData updatePaintingData = new PaintingData(
                paintingJson.title() + "1",
                paintingJson.description() + "1",
                getRandomPaintingFile().toString(),
                new ArtistGrpcClient().getRandomArtist().name(),
                new MuseumGrpcClient().getRandomMuseum().title()
        );
        new MainPage().openPaintings()
                .openPainting(paintingJson.title())
                .editPainting()
                .verifyContentEditDialog(paintingJson)
                .updatePainting(updatePaintingData)
                .verifyPopupMessage(PAINTING_UPDATE_MESSAGE.formatted(updatePaintingData.title()), ViewPaintingPage.class)
                .verifyViewPaintingContent(updatePaintingData.toPaintingJson());

    }
}
