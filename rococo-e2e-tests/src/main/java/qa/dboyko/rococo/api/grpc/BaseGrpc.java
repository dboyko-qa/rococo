package qa.dboyko.rococo.api.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.apache.commons.lang3.StringUtils;
import qa.dboyko.rococo.utils.GrpcConsoleInterceptor;

public abstract class BaseGrpc {

    private final Channel channel;

    protected BaseGrpc(String url) {
        HostPort hostPort = parseUrl(url);

        this.channel = ManagedChannelBuilder
                .forAddress(hostPort.host(), hostPort.port())
                .intercept(new GrpcConsoleInterceptor())
                .usePlaintext()
                .maxInboundMessageSize(10*1024*1024)
                .build();
    }

    protected Channel getChannel() {
        return channel;
    }

    private static HostPort parseUrl(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("gRPC url is blank");
        }

        String host = StringUtils.substringBetween(url, "//", ":");
        String portPart = StringUtils.substringAfterLast(url, ":");

        if (StringUtils.isBlank(host) || StringUtils.isBlank(portPart)) {
            throw new IllegalArgumentException(
                    "Invalid gRPC url format. Expected host:port, got: " + url
            );
        }

        try {
            int port = Integer.parseInt(portPart);
            return new HostPort(host, port);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid gRPC port in url: " + url, e
            );
        }
    }

    private record HostPort(String host, int port) {
    }
}

