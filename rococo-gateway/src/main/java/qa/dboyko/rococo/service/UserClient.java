package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.GetUserResponse;
import com.dboyko.rococo.grpc.UpdateUserResponse;
import com.dboyko.rococo.grpc.Userdata;
import qa.dboyko.rococo.model.UserdataJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserClient {
    public Userdata getUser(String username);
    public Userdata updateUser(UserdataJson userdata);
}
