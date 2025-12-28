package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
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
    private ObjectMapper objectMapper;

    @Autowired
    public UserDataService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @KafkaListener(topics = "users", groupId = "userdata")
    public void listener(@Payload String userJson,
                         ConsumerRecord<String, UserJson> cr) throws JsonProcessingException {

        UserJson user = objectMapper.readValue(userJson, UserJson.class);

        userRepository.findByUsername(user.username())
                .ifPresentOrElse(
                        u -> LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString()),
                        () -> {
                            LOG.info("Kafka event received. key={}, offset={}", cr.key(), cr.offset());
                            LOG.info("username: {}", user.username());

                            UserEntity userDataEntity = new UserEntity();
                            userDataEntity.setUsername(user.username());
                            UserEntity userEntity;
                            try {
                                userEntity = userRepository.save(userDataEntity);
                                userRepository.flush();
                                LOG.info(
                                        "### User '{}' successfully saved to database with id: {}",
                                        user.username(),
                                        userEntity.getId()
                                );
                            } catch (DataIntegrityViolationException e) {
                                LOG.info("User already exists, skipping");
                            }
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
                    .setUserdata(user.toUserdataGrpc())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("User not found")
                    .asRuntimeException());
        }

    }

    @Override
    @Transactional
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        LOG.info("!!!!Update user request received");
        LOG.info(request.getUserdata().toString());
        UUID userId = UUID.fromString(request.getUserdata().getUserId());
        Userdata userdata = request.getUserdata();
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        UserEntity userEntity = userOpt.orElseThrow(() ->
                Status.NOT_FOUND
                        .withDescription("User not found")
                        .asRuntimeException()
        );

        LOG.info("!!! user found");
        userEntity.setAvatar(!userdata.getAvatar().isEmpty()
                ? userdata.getAvatar().getBytes(StandardCharsets.UTF_8)
                : null);
        userEntity.setFirstname(userdata.getFirstname());
        userEntity.setLastname(userdata.getLastname());
        userRepository.save(userEntity);
        userRepository.flush();

        UpdateUserResponse response = UpdateUserResponse.newBuilder()
                .setUserdata(userEntity.toUserdataGrpc())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
