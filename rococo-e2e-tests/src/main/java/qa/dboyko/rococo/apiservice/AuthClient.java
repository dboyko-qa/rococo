package qa.dboyko.rococo.apiservice;

import io.restassured.response.Response;
import lombok.SneakyThrows;
import qa.dboyko.rococo.api.gateway.AuthApi;
import qa.dboyko.rococo.api.core.ThreadSafeCookieFilter;
import qa.dboyko.rococo.config.Config;
import qa.dboyko.rococo.extensions.ApiLoginExtension;
import qa.dboyko.rococo.utils.OAuthUtils;


public class AuthClient {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi = new AuthApi();

    @SneakyThrows
    public String login(String username, String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "/authorized";
        final String clientId = "client";

        Response response = authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        );

        authApi.loginGet().then().log().all();

        authApi.login(
                username,
                password,
                ThreadSafeCookieFilter.INSTANCE.cookieValue("XSRF-TOKEN")
        ).then().log().all();

        String redirectLocation = authApi.authorize(
                        "code",
                        clientId,
                        "openid",
                        redirectUri,
                        codeChallenge,
                        "S256"
                )
                .then().log().all().extract().header("Location");

        authApi.authorized(ApiLoginExtension.getCode());

        return authApi.token(
                        ApiLoginExtension.getCode(),
                        redirectUri,
                        clientId,
                        codeVerifier,
                        "authorization_code"
                ).then()
                .log().all()
                .extract().body().jsonPath().get("id_token");

    }
}
