package qa.dboyko.rococo.unittests;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import qa.dboyko.rococo.ex.GrpcErrorHandler;
import qa.dboyko.rococo.ex.MuseumAlreadyExistsException;
import qa.dboyko.rococo.ex.MuseumNotFoundException;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("GrpcErrorHandler Unit Tests")
class GrpcErrorHandlerTest {

    private GrpcErrorHandler grpcErrorHandler;

    @BeforeEach
    void setUp() {
        grpcErrorHandler = new GrpcErrorHandler();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void shouldHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleIllegalArgumentException(exception);

        // Assert
        assertThat(result).isNonnull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(result.getStatus().getDescription()).isEqualTo("Invalid argument");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle MuseumAlreadyExistsException")
    void shouldHandleMuseumAlreadyExistsException() {
        // Arrange
        MuseumAlreadyExistsException exception = new MuseumAlreadyExistsException("Museum exists");

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleAlreadyExistsException(exception);

        // Assert
        assertThat(result).isNonnull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(result.getStatus().getDescription()).isEqualTo("Museum with name Museum exists already exists");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle NotFoundException")
    void shouldHandleNotFoundException() throws Exception {
        // Arrange
        MuseumNotFoundException exception = new MuseumNotFoundException();

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleNotFoundException(exception);

        // Assert
        assertThat(result).isNonnull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException with duplicate key message")
    void shouldHandleDataIntegrityViolationExceptionDuplicate() {
        // Arrange
        Throwable cause = mock(Throwable.class);
        when(cause.getMessage()).thenReturn(
                "ERROR: duplicate key value violates unique constraint " +
                        "\"uq_museum_title_city_country_ci\" " +
                        "(lower(title::text), lower(city::text), country_id)=(Louvre, Paris)"
        );
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Duplicate key", cause);

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleDataIntegrity(exception);

        // Assert
        assertThat(result).isNonnull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(result.getStatus().getDescription())
                .contains("Louvre")
                .contains("Paris");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException with generic message")
    void shouldHandleDataIntegrityViolationExceptionGeneric() {
        // Arrange
        Throwable cause = mock(Throwable.class);
        when(cause.getMessage()).thenReturn("Some other DB error");
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Error", cause);

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleDataIntegrity(exception);

        // Assert
        assertThat(result).isNonnull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(result.getStatus().getDescription()).contains("Some other DB error");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException")
    void shouldHandleConstraintViolationException() {
        // Arrange
        SQLException sqlException = new SQLException("Constraint failed");
        ConstraintViolationException exception = new ConstraintViolationException("Constraint failed", sqlException, "constraint_name");

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleConstraint(exception);

        // Assert
        assertThat(result).isNonnull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(result.getStatus().getDescription()).contains("Constraint failed");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle generic Exception with defaultHandler")
    void shouldHandleGenericExceptionWithDefaultHandler() {
        // Arrange
        Exception exception = new Exception("Generic error");

        // Act
        StatusRuntimeException result = grpcErrorHandler.defaultHandler(exception);

        // Assert
        assertThat(result).isNonnull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.INTERNAL);
        assertThat(result.getStatus().getDescription()).contains("Generic error");
        assertThat(result.getCause()).isEqualTo(exception);
    }
}
