package qa.dboyko.rococo.api.gateway;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jakarta.annotation.Nullable;
import org.eclipse.jdt.annotation.NonNullByDefault;
import qa.dboyko.rococo.api.core.ThreadSafeCookieFilter;
import qa.dboyko.rococo.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.with;
import static org.apache.commons.lang3.ArrayUtils.*;

public abstract class ApiBase {
    private final String baseUri;
    private List<Filter> filters = null;
    private ContentType contentType = null;

    public ApiBase(String baseUri) {
        this.baseUri = baseUri;
    }

    public ApiBase(String baseUri, Filter... filters) {
        this.baseUri = baseUri;
        if (isNotEmpty(filters))
            this.filters = new ArrayList<>(List.of(filters));
    }

    public ApiBase(String baseUri, ContentType contentType, Filter... filters) {
        this.baseUri = baseUri;
        if (isNotEmpty(filters))
            this.filters = new ArrayList<>(List.of(filters));
        this.contentType = contentType;
    }

    protected RequestSpecification baseRequestSpecification = with()
            .config(RestAssuredConfig.config()
                    .redirect(RestAssuredConfig.config().getRedirectConfig()
                            .followRedirects(false)))
            .filters(ThreadSafeCookieFilter.INSTANCE.get())
            .filter(new AllureRestAssured())
            .log().all();

    private RequestSpecification getRequestSpec() {
        baseRequestSpecification.with().baseUri(baseUri);
        if (!(filters == null) && (!filters.isEmpty()))
            filters.forEach(filter -> baseRequestSpecification.with().filter(filter));
        if (!(contentType == null)) baseRequestSpecification.with().contentType(contentType);
        return baseRequestSpecification;
    }

    public static Headers getAuthHeaders(@Nullable String bearerToken) {
        Headers headers = new Headers();
        if (bearerToken != null) new Headers(new Header("Authorization", bearerToken));
        return headers;
    }

    protected Response postCall(String url,
                                Headers headers,
                                Map<String, String> formParams) {
        return RestAssured
                .given()
                .spec(getRequestSpec())
                .headers(headers)
                .formParams(formParams)
                .when()
                .post(url);
    }

    protected Response postCall(String url,
                                Headers headers,
                                Object body) {
        return RestAssured
                .given()
                .spec(getRequestSpec())
                .headers(headers)
                .body(body)
                .when()
                .post(url);

    }

    protected Response patchCall(String url,
                                Headers headers,
                                Object body) {
        return RestAssured
                .given()
                .spec(getRequestSpec())
                .headers(headers)
                .body(body)
                .when()
                .patch(url);

    }

    protected Response getCall(String url,
                               Headers headers) {
        return RestAssured.given()
                .spec(getRequestSpec())
                .headers(headers)
                .get(url);
    }

    protected Response getCall(String url) {
        return RestAssured.given()
                .spec(getRequestSpec())
                .get(url);
    }

    protected Response getCall(String url,
                               Cookies cookies) {
        return RestAssured.given()
                .spec(getRequestSpec())
                .cookies(cookies)
                .get(url);
    }

    protected Response getCall(String url,
                               Map<String, String> queryParams,
                               Headers headers) {
        return RestAssured.given()
                .spec(getRequestSpec())
                .headers(headers)
                .queryParams(queryParams)
                .get(url);
    }

    @NonNullByDefault
    public static final class EmptyApiBase extends ApiBase {
        public EmptyApiBase(String baseUri) {
            super(baseUri);
        }
        public EmptyApiBase(String baseUri, Filter... codeFilters) {
            super(baseUri, codeFilters);
        }
        public EmptyApiBase(String baseUri, ContentType contentType, Filter... filters) {
            super(baseUri, contentType, filters);
        }
    }
}
