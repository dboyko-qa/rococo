package qa.dboyko.rococo.client;

import com.dboyko.rococo.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class UserDataClient {

    @GrpcClient("grpcUserdataClient")
    private UserDataServiceGrpc.UserDataServiceBlockingStub stub;

    public GetUserResponse getUser(String username) {
        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername(username)
                .build();
        return stub.getUser(request);
    }

    public GetUserResponse currentUser(String username) {
        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername(username)
                .build();
        return stub.getUser(request);
    }

    public UpdateUserResponse updateUser(String userId, String name) {
        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUserId(userId)
                .setUsername(name)
                .build();
        return stub.updateUser(request);
    }
}

