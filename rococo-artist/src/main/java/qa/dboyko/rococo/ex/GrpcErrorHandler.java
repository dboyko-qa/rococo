package qa.dboyko.rococo.ex;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.regex.Pattern;

@GrpcAdvice
public class GrpcErrorHandler {
    private final static Logger LOG = LoggerFactory.getLogger(GrpcErrorHandler.class);

    @GrpcExceptionHandler
    public StatusRuntimeException handleIllegalArgumentException(IllegalArgumentException e) {
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleAlreadyExistsException(ArtistAlreadyExistsException e) {
        return Status.ALREADY_EXISTS
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleNotFoundException(ArtistNotFoundException e) {
        return Status.NOT_FOUND
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleDataIntegrity(DataIntegrityViolationException e) {
        LOG.warn("Handling DataIntegrityViolationException ");

        Throwable cause = e.getMostSpecificCause();
        String description = cause != null ? cause.getMessage() : e.getMessage();
        if (cause.getMessage().contains("ERROR: duplicate key value violates unique constraint")) {
            description = "Artist %s already exists".formatted(
                    Pattern.compile("\\(name\\)=\\(([^)]+)\\)")
                            .matcher(cause.getMessage()).results()
                            .findFirst()
                            .map(m -> m.group(1))
                            .orElse("")
            );
        }
        return Status.ALREADY_EXISTS
                .withDescription(description)
                .withCause(e)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleConstraint(ConstraintViolationException e) {
        return Status.ALREADY_EXISTS
                .withDescription(e.getSQLException().getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException defaultHandler(Exception e) {
        return Status.INTERNAL
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

}
