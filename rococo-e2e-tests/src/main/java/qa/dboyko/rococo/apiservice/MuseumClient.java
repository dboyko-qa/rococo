package qa.dboyko.rococo.apiservice;

import io.restassured.response.Response;
import jakarta.annotation.Nullable;
import qa.dboyko.rococo.api.gateway.MuseumApi;
import qa.dboyko.rococo.api.gateway.ResponseSpecifications;
import qa.dboyko.rococo.config.Config;
import qa.dboyko.rococo.model.MuseumJson;

import java.util.ArrayList;
import java.util.List;

public class MuseumClient {
    private final MuseumApi museumApi = new MuseumApi();
    private final ResponseSpecifications responseSpecifications = new ResponseSpecifications();

    public Response createMuseum(MuseumJson museumJson, @Nullable String token) {
        return museumApi.createMuseum(museumJson, token);
    }

    public Response updateMuseum(MuseumJson museumJson, @Nullable String token) {
        return museumApi.updateMuseum(museumJson, token);
    }

    public Response getMuseum(String id) {
        return museumApi.getMuseum(id);
    }

    public List<MuseumJson> getAllMuseums(@Nullable String token) {
        int page = 0;
        int size = 10;

        List<MuseumJson> result = new ArrayList<>();

        while (true) {
            Response response = museumApi.getAllMuseums(page, size, token)
                    .then()
                    .spec(responseSpecifications.okResponseSpec())
                    .extract().response();

            List<MuseumJson> museums = response.jsonPath()
                    .getList("", MuseumJson.class);

            if (museums == null || museums.isEmpty()) {
                break;
            }

            result.addAll(museums);
            page++;
        }

        return result;
    }

}
