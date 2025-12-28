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

import java.util.regex.Matcher;
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
    public StatusRuntimeException handleAlreadyExistsException(MuseumAlreadyExistsException e) {
        return Status.ALREADY_EXISTS
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleNotFoundException(MuseumNotFoundException e) {
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
            Pattern pattern = Pattern.compile(
                    "\\(lower\\(title::text\\), lower\\(city::text\\), country_id\\)=\\(\\s*([^,]+)\\s*,\\s*([^,]+)"
            );

            Matcher matcher = pattern.matcher(cause.getMessage());

            description = matcher.find()
                    ? "Museum %s in city %s already exists".formatted(
                    matcher.group(1), // title
                    matcher.group(2)  // city
            )
                    : "Museum already exists";

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
