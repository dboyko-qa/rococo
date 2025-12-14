package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.Userdata;
import qa.dboyko.rococo.model.UserdataJson;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserClient {
    Userdata getUser(String username);

    Userdata updateUser(UserdataJson userdata);
}
