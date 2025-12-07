package qa.dboyko.rococo.config;

import com.dboyko.rococo.grpc.UserDataServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    private static final String USERDATA_HOST = "localhost";
    private static final int USERDATA_PORT = 8072;

    @Bean
    public ManagedChannel userDataChannel() {
        return ManagedChannelBuilder
                .forAddress(USERDATA_HOST, USERDATA_PORT)
                .usePlaintext()
                .build();
    }

    @Bean
    public UserDataServiceGrpc.UserDataServiceBlockingStub userDataServiceStub(ManagedChannel userDataChannel) {
        return UserDataServiceGrpc.newBlockingStub(userDataChannel);
    }
}

