package qa.dboyko.rococo.api.gateway;

import io.restassured.filter.Filter;
import qa.dboyko.rococo.api.core.CodeFilter;
import qa.dboyko.rococo.config.Config;

public class GatewayApi {
    private final Config CFG = Config.getInstance();
    Filter[] filters = {new CodeFilter()};
    private final ApiBase apiBase = new ApiBase.EmptyApiBase(CFG.gatewayUrl(), filters);


}
