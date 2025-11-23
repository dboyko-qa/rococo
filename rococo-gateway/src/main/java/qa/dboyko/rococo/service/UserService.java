package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import static qa.dboyko.rococo.model.UserdataJson.convertStringToAvatar;

@Service
public class UserService {

    @GrpcClient("grpcUserdataClient")
    private UserDataServiceGrpc.UserDataServiceBlockingStub userDataStub;

    public GetUserResponse getUser(String username) {
        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername(username)
                .build();

        return userDataStub.getUser(request);
    }

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
                .setAvatar(convertStringToAvatar(avatar))
                .build();

        return userDataStub.updateUser(request);
    }
}

