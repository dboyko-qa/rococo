package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.GetUserResponse;
import com.dboyko.rococo.grpc.UpdateUserResponse;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserClient {
    public GetUserResponse getUser(String username);
    public UpdateUserResponse updateUser(String userId, String username, String firstname, String lastname, String avatar);
}
