package qa.dboyko.rococo.service.grpc;

import com.dboyko.rococo.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.service.UserClient;

import javax.annotation.Nonnull;


@Service
@Component("grpcClient")
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

//    @Nonnull
//    @Override
//    public UserJson updateUserInfo(UserJson user) {
//        return UserJson.fromGrpc(userdataStub.updateUser(
//                UpdateUserRequest.newBuilder()
//                        .setUser(user.toGrpcUser())
//                        .build()
//        ).getUser());
//    }

    @Override
    public Userdata updateUser(@Nonnull UserdataJson user) {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUserdata(user.toGrpcMessage())
                .build();
        return userDataStub.updateUser(request).getUserdata();
    }
}

