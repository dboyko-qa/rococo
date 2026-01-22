package qa.dboyko.rococo.api.gateway;

import com.dboyko.rococo.grpc.PageInfo;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import qa.dboyko.rococo.config.Config;
import qa.dboyko.rococo.model.MuseumJson;

import java.util.Map;

import static qa.dboyko.rococo.api.gateway.ApiBase.getAuthHeaders;

public class MuseumApi {

    private final Config CFG = Config.getInstance();
    private final ApiBase apiBase = new ApiBase.EmptyApiBase(
            CFG.gatewayUrl() + EndPoints.museumUrl,
            ContentType.JSON);

    public Response getMuseum(@Nonnull String id) {
        return apiBase.getCall("/{id}", "id", id);
    }

    public Response createMuseum(MuseumJson museumJson, @Nullable String token) {
        return apiBase.postCall("",
                new Headers(new Header("Authorization", token)),
                museumJson);
    }

    public Response updateMuseum(MuseumJson museumJson, @Nullable String token) {
        return apiBase.patchCall("",
                new Headers(new Header("Authorization", token)),
                museumJson);
    }

    public Response getAllMuseums(Integer page, Integer size, @Nullable String token) {
        return apiBase.getCall("",
                Map.of("page", page.toString(), "size", size.toString()),
                getAuthHeaders(token));
    }

    public Response getAllMuseums(PageInfo pageInfo, @Nullable String token) {
        return getAllMuseums(pageInfo.getPage(), pageInfo.getSize(), token);
    }



}
