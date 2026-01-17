package qa.dboyko.rococo.api;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import qa.dboyko.rococo.api.core.CodeFilter;
import qa.dboyko.rococo.config.Config;

public class MuseumApi {
    private Config CFG = Config.getInstance();
    private final ApiBase apiBase = new ApiBase.EmptyApiBase(CFG.gatewayUrl(), new CodeFilter());

    public Response getAllMuseums() {
        return apiBase.getCall("/api/museum", new Headers());
    }


}
