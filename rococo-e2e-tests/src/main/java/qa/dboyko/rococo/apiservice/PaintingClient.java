package qa.dboyko.rococo.apiservice;

import io.restassured.response.Response;
import jakarta.annotation.Nullable;
import qa.dboyko.rococo.api.gateway.PaintingApi;
import qa.dboyko.rococo.api.gateway.ResponseSpecifications;
import qa.dboyko.rococo.config.Config;
import qa.dboyko.rococo.model.PaintingJson;

import java.util.ArrayList;
import java.util.List;

public class PaintingClient {
    private final PaintingApi paintingApi = new PaintingApi();
    private final ResponseSpecifications responseSpecifications = new ResponseSpecifications();

    public Response createPainting(PaintingJson paintingJson, @Nullable String token) {
        return paintingApi.createPainting(paintingJson, token);
    }

    public Response updatePainting(PaintingJson paintingJson, @Nullable String token) {
        return paintingApi.updatePainting(paintingJson, token);
    }

    public List<PaintingJson> getAllPaintings(@Nullable String token) {
        int page = 0;
        int size = 10;

        List<PaintingJson> result = new ArrayList<>();

        while (true) {
            Response response = paintingApi.getAllPaintings(page, size, token)
                    .then()
                    .spec(responseSpecifications.okResponseSpec())
                    .extract().response();

            List<PaintingJson> paintings = response.jsonPath()
                    .getList("", PaintingJson.class);

            if (paintings == null || paintings.isEmpty()) {
                break;
            }

            result.addAll(paintings);
            page++;
        }

        return result;
    }

}
