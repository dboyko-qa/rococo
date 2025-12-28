package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dboyko.rococo.model.UserdataJson;
import qa.dboyko.rococo.service.grpc.UserGrpcClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGrpcClientTest {

    private UserGrpcClient userGrpcClient;

    @Mock
    private UserDataServiceGrpc.UserDataServiceBlockingStub userDataStub;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        userGrpcClient = new UserGrpcClient();
        // inject mocked stub
        var stubField = userGrpcClient.getClass().getDeclaredField("userDataStub");
        stubField.setAccessible(true);
        stubField.set(userGrpcClient, userDataStub);
    }

    // -----------------------
    // getUser
    // -----------------------
    @Test
    @DisplayName("Should return user data when user exists")
    void shouldReturnUserDataWhenUserExists() {
        // Arrange
        Userdata grpcUser = Userdata.newBuilder()
                .setUserId("1")
                .setUsername("johndoe")
                .setFirstname("John")
                .setLastname("Doe")
                .setAvatar("avatar_data")
                .build();

        when(userDataStub.getUser(any(GetUserRequest.class)))
                .thenReturn(GetUserResponse.newBuilder()
                        .setUserdata(grpcUser)
                        .build());

        // Act
        Userdata result = userGrpcClient.getUser("johndoe");

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getUserId());
        assertEquals("johndoe", result.getUsername());
    }

    @Test
    @DisplayName("Should throw exception when gRPC service fails for getUser")
    void shouldThrowWhenGrpcFailsForGetUser() {
        // Arrange
        when(userDataStub.getUser(any(GetUserRequest.class)))
                .thenThrow(new RuntimeException("gRPC failure"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userGrpcClient.getUser("johndoe"));
        assertEquals("gRPC failure", exception.getMessage());
    }

    // -----------------------
    // updateUser
    // -----------------------
    @Test
    @DisplayName("Should update user data successfully")
    void shouldUpdateUserDataSuccessfully() {
        // Arrange
        UserdataJson userJson = new UserdataJson("1", "johndoe", "Johnny", "Doe", "avatar_data");
        Userdata grpcResponse = Userdata.newBuilder()
                .setUserId("1")
                .setUsername("johndoe")
                .setFirstname("Johnny")
                .setLastname("Doe")
                .setAvatar("avatar_data")
                .build();

        when(userDataStub.updateUser(any(UpdateUserRequest.class)))
                .thenReturn(UpdateUserResponse.newBuilder()
                        .setUserdata(grpcResponse)
                        .build());

        // Act
        Userdata result = userGrpcClient.updateUser(userJson);

        // Assert
        assertNotNull(result);
        assertEquals("Johnny", result.getFirstname());
        assertEquals("Doe", result.getLastname());
    }

    @Test
    @DisplayName("Should throw exception when gRPC service fails for updateUser")
    void shouldThrowWhenGrpcFailsForUpdateUser() {
        // Arrange
        UserdataJson userJson = new UserdataJson("1", "johndoe", "Johnny", "Doe", "avatar_data");

        when(userDataStub.updateUser(any(UpdateUserRequest.class)))
                .thenThrow(new RuntimeException("gRPC failure"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userGrpcClient.updateUser(userJson));
        assertEquals("gRPC failure", exception.getMessage());
    }
}

