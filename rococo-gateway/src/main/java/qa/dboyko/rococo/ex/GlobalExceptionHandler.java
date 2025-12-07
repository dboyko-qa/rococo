package qa.dboyko.rococo.ex;

import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import qa.dboyko.rococo.model.ErrorJson;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${spring.application.name}")
    private String appName;

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ErrorJson> handleGrpcException(@Nonnull RuntimeException ex,
                                                         @Nonnull HttpServletRequest request) {
        LOG.warn("### Resolve Exception in @RestControllerAdvice ", ex);
        return withStatus("Bad request", HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    private @Nonnull ResponseEntity<ErrorJson> withStatus(@Nonnull String type,
                                                          @Nonnull HttpStatus status,
                                                          @Nonnull String message,
                                                          @Nonnull HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(new ErrorJson(
                        appName + ": " + type,
                        status.getReasonPhrase(),
                        status.value(),
                        message,
                        request.getRequestURI()
                ));
    }

}
