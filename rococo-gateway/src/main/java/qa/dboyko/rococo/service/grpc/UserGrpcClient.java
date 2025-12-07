package qa.dboyko.rococo.service.grpc;

import com.dboyko.rococo.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.service.UserClient;
import qa.dboyko.rococo.util.GrpcImpl;

import javax.annotation.Nonnull;

@Service
@GrpcImpl
public class UserGrpcClient implements UserClient {

    @GrpcClient("grpcUserdataClient")
    private UserDataServiceGrpc.UserDataServiceBlockingStub userDataStub;

    @Override
    public Userdata getUser(@Nonnull String username) {
        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername(username)
                .build();

        return userDataStub.getUser(request).getUserdata();
    }

    @Override
    public Userdata updateUser(@Nonnull UserdataJson user) {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUserdata(user.toGrpcMessage())
                .build();
        return userDataStub.updateUser(request).getUserdata();
    }
}

