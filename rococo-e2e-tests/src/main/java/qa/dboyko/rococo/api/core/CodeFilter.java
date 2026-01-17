package qa.dboyko.rococo.api.core;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.commons.lang3.StringUtils;
import qa.dboyko.rococo.extensions.ApiLoginExtension;

public class CodeFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        Response response = ctx.next(requestSpec, responseSpec);

        if (response.statusCode() / 100 == 3) {
            String location = response.getHeader("Location");
            if (location != null && location.contains("code=")) {
                ApiLoginExtension.setCode(
                        StringUtils.substringAfter(location, "code=")
                );
            }
        }

        return response;
    }

}
