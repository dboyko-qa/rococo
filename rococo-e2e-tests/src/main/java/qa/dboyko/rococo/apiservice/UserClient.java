package qa.dboyko.rococo.apiservice;

import qa.dboyko.rococo.api.AuthApi;
import qa.dboyko.rococo.api.ResponseSpecifications;
import qa.dboyko.rococo.model.UserdataJson;

public class UserClient {

    private final AuthApi authApi = new AuthApi();
    private final ResponseSpecifications responseSpecs = new ResponseSpecifications();

    public UserdataJson createUser(String username, String password) {
        authApi.getRegisterForm().then().log().all();
        authApi.postRegisterForm(username, password)
                .then()
                .log().all()
                .spec(responseSpecs.createResponseSpec());
        return new UserdataJson(username);
    }
}
