package qa.dboyko.rococo.ex;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcAdvice
public class GrpcErrorHandler {
    private final static Logger LOG = LoggerFactory.getLogger(GrpcErrorHandler.class);

    @GrpcExceptionHandler
    public StatusRuntimeException defaultHandler(Exception e) {
        LOG.error("!!! Internal error");
        return Status.INTERNAL
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

}
