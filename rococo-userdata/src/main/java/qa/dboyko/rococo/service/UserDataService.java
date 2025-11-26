package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;
import qa.dboyko.rococo.entity.UserEntity;
import qa.dboyko.rococo.model.UserJson;
import qa.dboyko.rococo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.protobuf.ByteString;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@GrpcService
public class UserDataService extends UserDataServiceGrpc.UserDataServiceImplBase {

    private static Logger LOG = LoggerFactory.getLogger(UserDataService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserDataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @KafkaListener(topics = "users", groupId = "userdata")
    public void listener(@Payload String userJson,
                         ConsumerRecord<String, UserJson> cr) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        UserJson user = objectMapper.readValue(userJson, UserJson.class);

        userRepository.findByUsername(user.username())
                .ifPresentOrElse(
                        u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
                        () -> {
                            LOG.info("### Kafka consumer record: {}", cr.toString());
                            LOG.info("username: {}", user.username());

                            UserEntity userDataEntity = new UserEntity();
                            userDataEntity.setUsername(user.username());
                            UserEntity userEntity = userRepository.save(userDataEntity);

                            LOG.info(
                                    "### User '{}' successfully saved to database with id: {}",
                                    user.username(),
                                    userEntity.getId()
                            );
                        }
                );
    }

    @Override
    @Transactional(readOnly = true)
    public void getUser(GetUserRequest request, StreamObserver<GetUserResponse> responseObserver) {
        LOG.info("Received getUser request for user: {}", request.getUsername());
        String username = request.getUsername();
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        ByteString avatar = ByteString.EMPTY;

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            GetUserResponse response = GetUserResponse.newBuilder()
                    .setUserId(user.getId().toString())
                    .setUsername(user.getUsername())
                    .setFirstname(user.getFirstname() != null ? user.getFirstname() : "")
                    .setLastname(user.getLastname() != null ? user.getLastname() : "")
                    .setAvatar(user.getAvatar() != null && user.getAvatar().length > 0 ? new String(user.getAvatar(), StandardCharsets.UTF_8) : "")
                    .build();
            responseObserver.onNext(response);
        } else {
            responseObserver.onError(new Exception("User not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        UUID userId = UUID.fromString(request.getUserId());
        Optional<UserEntity> userOpt = userRepository.findById(userId);

        boolean success = false;
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setAvatar(!request.getAvatar().isEmpty()
                    ? request.getAvatar().getBytes(StandardCharsets.UTF_8)
                    : null);
            user.setUsername(request.getUsername());
            user.setFirstname(request.getFirstname());
            user.setLastname(request.getLastname());
            userRepository.save(user);
            success = true;
        }

        UpdateUserResponse response = UpdateUserResponse.newBuilder()
                .setSuccess(success)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
