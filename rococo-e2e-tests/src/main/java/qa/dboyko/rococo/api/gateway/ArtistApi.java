package qa.dboyko.rococo.api.gateway;

import com.dboyko.rococo.grpc.PageInfo;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import qa.dboyko.rococo.config.Config;
import qa.dboyko.rococo.model.ArtistJson;

import java.util.Map;

import static qa.dboyko.rococo.api.gateway.ApiBase.getAuthHeaders;

public class ArtistApi {

    private final Config CFG = Config.getInstance();
    private final ApiBase apiBase = new ApiBase.EmptyApiBase(
            CFG.gatewayUrl() + EndPoints.artistUrl,
            ContentType.JSON);

    public Response createArtist(ArtistJson artistJson, @Nullable String token) {
        return apiBase.postCall("",
                new Headers(new Header("Authorization", token)),
                artistJson);
    }

    public Response getArtist(@Nonnull String id) {
        return apiBase.getCall("/{id}", "id", id);
    }

    public Response updateArtist(ArtistJson artistJson, @Nullable String token) {
        return apiBase.patchCall("",
                new Headers(new Header("Authorization", token)),
                artistJson);
    }

    public Response getAllArtists(Integer page, Integer size, @Nullable String token) {
        return apiBase.getCall("",
                Map.of("page", page.toString(), "size", size.toString()),
                getAuthHeaders(token));
    }

    public Response getAllArtists(PageInfo pageInfo, @Nullable String token) {
        return getAllArtists(pageInfo.getPage(), pageInfo.getSize(), token);
    }



}
