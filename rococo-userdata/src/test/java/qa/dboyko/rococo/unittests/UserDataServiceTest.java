package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qa.dboyko.rococo.entity.UserEntity;
import qa.dboyko.rococo.model.UserJson;
import qa.dboyko.rococo.repository.UserRepository;
import qa.dboyko.rococo.service.UserDataService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDataService Unit Tests")
class UserDataServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserDataService userDataService;

    @Mock
    private StreamObserver<GetUserResponse> getUserObserver;

    @Mock
    private StreamObserver<UpdateUserResponse> updateUserObserver;

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(userDataService, "objectMapper", objectMapper);
    }

    @Test
    @DisplayName("Should return user when getUser is called and user exists")
    void shouldReturnUserWhenGetUserIsCalledAndUserExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername("john")
                .build();

        // Act
        userDataService.getUser(request, getUserObserver);

        // Assert
        verify(getUserObserver).onNext(any(GetUserResponse.class));
        verify(getUserObserver).onCompleted();
        verify(getUserObserver, never()).onError(any());
    }

    @Test
    @DisplayName("Should return not found error when getUser is called and user does not exist")
    void shouldReturnNotFoundErrorWhenGetUserIsCalledAndUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername("john")
                .build();

        // Act
        userDataService.getUser(request, getUserObserver);

        // Assert
        verify(getUserObserver).onError(any());
        verify(getUserObserver, never()).onNext(any());
        verify(getUserObserver, never()).onCompleted();
    }

    @Test
    @DisplayName("Should update user when updateUser is called and user exists")
    void shouldUpdateUserWhenUpdateUserIsCalledAndUserExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("john");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Userdata userdata = Userdata.newBuilder()
                .setUserId(userId.toString())
                .setUsername("john")
                .setFirstname("John")
                .setLastname("Doe")
                .build();

        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUserdata(userdata)
                .build();

        // Act
        userDataService.updateUser(request, updateUserObserver);

        // Assert
        verify(userRepository).save(any(UserEntity.class));
        verify(updateUserObserver).onNext(any(UpdateUserResponse.class));
        verify(updateUserObserver).onCompleted();
        verify(updateUserObserver, never()).onError(any());
    }

    @Test
    @DisplayName("Should throw not found error when updateUser is called and user does not exist")
    void shouldThrowNotFoundErrorWhenUpdateUserIsCalledAndUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Userdata userdata = Userdata.newBuilder()
                .setUserId(userId.toString())
                .setUsername("john")
                .build();

        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUserdata(userdata)
                .build();

        // Act & Assert
        assertThatThrownBy(() ->
                userDataService.updateUser(request, updateUserObserver)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should save user when Kafka listener receives new user event")
    void shouldSaveUserWhenKafkaListenerReceivesNewUserEvent() throws Exception {
        // Arrange
        String json = "{\"username\":\"john\"}";
        UserJson userJson = new UserJson("john");

        when(objectMapper.readValue(json, UserJson.class))
                .thenReturn(userJson);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        // мок save, чтобы возвращать объект с id
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity u = invocation.getArgument(0);
            u.setId(UUID.randomUUID()); // генерируем id, как real save
            return u;
        });

        ConsumerRecord<String, UserJson> record =
                new ConsumerRecord<>("users", 0, 0L, "key", userJson);

        // Act
        userDataService.listener(json, record);

        // Assert
        verify(userRepository).save(any(UserEntity.class));
    }


    @Test
    @DisplayName("Should skip saving user when Kafka listener receives existing user event")
    void shouldSkipSavingUserWhenKafkaListenerReceivesExistingUserEvent() throws Exception {
        // Arrange
        String json = "{\"username\":\"john\"}";
        UserJson userJson = new UserJson("john");

        when(objectMapper.readValue(json, UserJson.class))
                .thenReturn(userJson);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(new UserEntity()));

        ConsumerRecord<String, UserJson> record =
                new ConsumerRecord<>("users", 0, 0L, "key", userJson);

        // Act
        userDataService.listener(json, record);

        // Assert
        verify(userRepository, never()).save(any());
    }
}
