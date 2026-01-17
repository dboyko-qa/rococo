package qa.dboyko.rococo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
public class GrpcConfig {

    @EventListener(ApplicationReadyEvent.class)
    public void checkGrpcServer() {
        log.info("=== GRPC SERVER STATUS CHECK ===");
        try {
            java.net.Socket socket = new java.net.Socket("localhost", 8078);
            log.info("✅ gRPC port 8078 is ACCEPTING connections");
            socket.close();
        } catch (Exception e) {
            log.error("❌ gRPC port 8078 is NOT accepting connections: {}", e.getMessage());
        }
    }
}
