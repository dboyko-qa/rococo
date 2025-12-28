package qa.dboyko.rococo.integrationtests;

import com.dboyko.rococo.grpc.GetUserRequest;
import com.dboyko.rococo.grpc.GetUserResponse;
import com.dboyko.rococo.grpc.UpdateUserRequest;
import com.dboyko.rococo.grpc.UpdateUserResponse;
import com.dboyko.rococo.grpc.Userdata;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.StatusRuntimeException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import qa.dboyko.rococo.entity.UserEntity;
import qa.dboyko.rococo.model.UserJson;
import qa.dboyko.rococo.repository.UserRepository;
import qa.dboyko.rococo.service.UserDataService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DisplayName("UserDataService Integration Tests")
class UserDataServiceIntegrationTest {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------- getUser ----------------------

    @Test
    @DisplayName("Should return user when user exists")
    void shouldReturnUserWhenUserExists() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setUsername("john");
        user.setFirstname("John");
        user.setLastname("Doe");
        userRepository.saveAndFlush(user);

        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername("john")
                .build();

        var observer = mock(io.grpc.stub.StreamObserver.class);

        // Act
        userDataService.getUser(request, observer);

        // Assert
        verify(observer).onNext(any(GetUserResponse.class));
        verify(observer).onCompleted();
        verify(observer, never()).onError(any());
    }

    // ---------------------- updateUser ----------------------

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setUsername("jane");
        user = userRepository.saveAndFlush(user);

        Userdata userdata = Userdata.newBuilder()
                .setUserId(user.getId().toString())
                .setFirstname("Jane")
                .setLastname("Doe")
                .build();

        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUserdata(userdata)
                .build();

        var observer = mock(io.grpc.stub.StreamObserver.class);

        // Act
        userDataService.updateUser(request, observer);

        // Assert
        UserEntity updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getFirstname()).isEqualTo("Jane");
        assertThat(updated.getLastname()).isEqualTo("Doe");

        verify(observer).onNext(any(UpdateUserResponse.class));
        verify(observer).onCompleted();
    }

    @Test
    @DisplayName("should throw StatusRuntimeException when updateUser called with non-existing id")
    void shouldThrowExceptionOnUpdateUserNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();

        @SuppressWarnings("unchecked")
        io.grpc.stub.StreamObserver<UpdateUserResponse> observer = mock(io.grpc.stub.StreamObserver.class);

        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUserdata(com.dboyko.rococo.grpc.Userdata.newBuilder()
                        .setUserId(id.toString())
                        .setFirstname("Ghost")
                        .build())
                .build();

        // Act & Assert
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () ->
                userDataService.updateUser(request, observer));

        assertThat(ex.getStatus().getCode()).isEqualTo(io.grpc.Status.Code.NOT_FOUND);
        assertThat(ex.getStatus().getDescription()).contains("User not found");
    }


    // ---------------------- Kafka listener ----------------------

    @Test
    @DisplayName("Should save user from kafka event when user does not exist")
    void shouldSaveUserFromKafkaEventWhenUserDoesNotExist() throws Exception {
        // Arrange
        UserJson userJson = new UserJson("kafkaUser");
        String json = objectMapper.writeValueAsString(userJson);

        ConsumerRecord<String, UserJson> record =
                new ConsumerRecord<>("users", 0, 0L, "key", userJson);

        // Act
        userDataService.listener(json, record);

        // Assert
        Optional<UserEntity> user = userRepository.findByUsername("kafkaUser");
        assertThat(user).isPresent();
    }

    @Test
    @DisplayName("Should skip kafka event when user already exists")
    void shouldSkipKafkaEventWhenUserAlreadyExists() throws Exception {
        // Arrange
        UserEntity user = new UserEntity();
        user.setUsername("existing");
        userRepository.saveAndFlush(user);

        UserJson userJson = new UserJson("existing");
        String json = objectMapper.writeValueAsString(userJson);

        ConsumerRecord<String, UserJson> record =
                new ConsumerRecord<>("users", 0, 0L, "key", userJson);

        // Act
        userDataService.listener(json, record);

        // Assert
        assertThat(userRepository.findAll()).hasSize(1);
    }
}

