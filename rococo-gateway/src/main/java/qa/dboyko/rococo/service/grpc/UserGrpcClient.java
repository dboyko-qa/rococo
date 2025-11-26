package qa.dboyko.rococo.service.grpc;

import com.dboyko.rococo.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import qa.dboyko.rococo.service.UserClient;


@Service
public class UserGrpcClient implements UserClient {

    @GrpcClient("grpcUserdataClient")
    private UserDataServiceGrpc.UserDataServiceBlockingStub userDataStub;

    @Override
    public GetUserResponse getUser(String username) {
        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername(username)
                .build();

        return userDataStub.getUser(request);
    }

    @Override
    public UpdateUserResponse updateUser(String userId,
                                         String username,
                                         String firstname,
                                         String lastname,
                                         String avatar) {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUserId(userId)
                .setUsername(username)
                .setFirstname(firstname)
                .setLastname(lastname)
                .setAvatar(avatar)
                .build();

        return userDataStub.updateUser(request);
    }
}

