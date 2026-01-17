package qa.dboyko.rococo.api;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import qa.dboyko.rococo.api.core.CodeFilter;
import qa.dboyko.rococo.api.core.ThreadSafeCookieFilter;
import qa.dboyko.rococo.config.Config;

import java.util.HashMap;
import java.util.Map;

import static qa.dboyko.rococo.api.EndPoints.*;
import io.restassured.filter.Filter;

public class AuthApi {

    private final Config CFG = Config.getInstance();
    Filter[] filters = {new CodeFilter()};
    private final ApiBase apiBase = new ApiBase.EmptyApiBase(CFG.authUrl(), filters);

    public Response getRegisterForm() {
        return apiBase.getCall(registerUrl,
                new Headers());
    }

    public Response postRegisterForm(String username, String password) {
        return apiBase.postCall(registerUrl,
                new Headers(),
                Map.of(
                        "username", username,
                        "password", password,
                        "passwordSubmit", password,
                        "_csrf", ThreadSafeCookieFilter.INSTANCE.cookieValue("XSRF-TOKEN")
                        ));
    }

    public Response authorize(String responseType,
                              String clientId,
                              String scope,
                              String redirectUri,
                              String codeChallenge,
                              String codeChallengeMethod) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("response_type", responseType);
        queryParams.put("client_id", clientId);
        queryParams.put("scope", scope);
        queryParams.put("redirect_uri", redirectUri); //need encoding
        queryParams.put("code_challenge", codeChallenge);
        queryParams.put("code_challenge_method", codeChallengeMethod);
        return apiBase.getCall(authorizeUrl, queryParams, new Headers());
    }

    public Response authorized(String code) {
        Map<String, String> queryParams = Map.of("code", code);
        return apiBase.getCall(authorizedUrl, queryParams, new Headers());
    }

    public Response loginGet() {
        return apiBase.getCall(loginUrl);
    }

    public Response login(String username, String password, String csrf) {
        Map<String, String> formParams = Map.of(
                "username", username,
                "password", password,
                "_csrf", csrf
        );
        return apiBase.postCall(loginUrl, new Headers(), formParams);
    }

    public Response token(String code,
                          String redirectUri,
                          String clientId,
                          String codeVerifier,
                          String grantType) {
        Map<String, String> formFields = Map.of(
                "code", code,
                "redirect_uri", redirectUri,
                "client_id", clientId,
                "code_verifier", codeVerifier,
                "grant_type", grantType
        );
        return apiBase.postCall(tokenUrl, new Headers(), formFields);
    }

}
