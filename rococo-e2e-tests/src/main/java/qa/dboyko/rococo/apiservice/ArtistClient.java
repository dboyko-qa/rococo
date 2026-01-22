package qa.dboyko.rococo.apiservice;

import io.restassured.response.Response;
import jakarta.annotation.Nullable;
import qa.dboyko.rococo.api.gateway.ArtistApi;
import qa.dboyko.rococo.api.gateway.ResponseSpecifications;
import qa.dboyko.rococo.config.Config;
import qa.dboyko.rococo.model.ArtistJson;

import java.util.ArrayList;
import java.util.List;

public class ArtistClient {
    private final ArtistApi artistApi = new ArtistApi();
    private final ResponseSpecifications responseSpecifications = new ResponseSpecifications();

    public Response createArtist(ArtistJson artistJson, @Nullable String token) {
        return artistApi.createArtist(artistJson, token);
    }


    public Response updateArtist(ArtistJson artistJson, @Nullable String token) {
        return artistApi.updateArtist(artistJson, token);
    }

    public Response getArtist(String id) {
        return artistApi.getArtist(id);
    }

    public List<ArtistJson> getAllArtists(@Nullable String token) {
        int page = 0;
        int size = 10;

        List<ArtistJson> result = new ArrayList<>();

        while (true) {
            Response response = artistApi.getAllArtists(page, size, token)
                    .then()
                    .spec(responseSpecifications.okResponseSpec())
                    .extract().response();

            List<ArtistJson> artists = response.jsonPath()
                    .getList("", ArtistJson.class);

            if (artists == null || artists.isEmpty()) {
                break;
            }

            result.addAll(artists);
            page++;
        }

        return result;
    }

}
