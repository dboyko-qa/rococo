package qa.dboyko.rococo.unittests;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import qa.dboyko.rococo.ex.ArtistAlreadyExistsException;
import qa.dboyko.rococo.ex.ArtistNotFoundException;
import qa.dboyko.rococo.ex.GrpcErrorHandler;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GrpcErrorHandler Unit Tests")
class GrpcErrorHandlerTest {

    private GrpcErrorHandler grpcErrorHandler;

    @BeforeEach
    void setUp() {
        grpcErrorHandler = new GrpcErrorHandler();
    }

    @Test
    @DisplayName("Should handle ArtistNotFoundException through default handler")
    void shouldHandleArtistNotFoundException() {
        // Arrange
        String artistId = "123e4567-e89b-12d3-a456-426614174000";
        ArtistNotFoundException exception = new ArtistNotFoundException(artistId);

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleNotFoundException(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
        assertThat(result.getStatus().getDescription())
                .contains(artistId)
                .contains("not found");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle ArtistAlreadyExistsException through default handler")
    void shouldHandleArtistAlreadyExistsException() {
        // Arrange
        String artistName = "Vincent van Gogh";
        ArtistAlreadyExistsException exception = new ArtistAlreadyExistsException(artistName);

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleAlreadyExistsException(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(result.getStatus().getDescription())
                .contains(artistName)
                .contains("already exists");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException with duplicate key")
    void shouldHandleDataIntegrityViolationExceptionWithDuplicateKey() {
        // Arrange
        String errorMessage = "ERROR: duplicate key value violates unique constraint \"artist_name_key\"\n" +
                "  Detail: Key (name)=(Vincent van Gogh) already exists.";
        SQLException sqlException = new SQLException(errorMessage);
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "could not execute statement", sqlException);

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleDataIntegrity(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(result.getStatus().getDescription())
                .contains("Vincent van Gogh")
                .contains("already exists");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException without duplicate key pattern")
    void shouldHandleDataIntegrityViolationExceptionWithoutDuplicateKey() {
        // Arrange
        String errorMessage = "NULL not allowed for column NAME";
        SQLException sqlException = new SQLException(errorMessage);
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "could not execute statement", sqlException);

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleDataIntegrity(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(result.getStatus().getDescription()).contains(errorMessage);
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException with null cause")
    void shouldHandleDataIntegrityViolationExceptionWithNullCause() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation");

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleDataIntegrity(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(result.getStatus().getDescription()).isEqualTo("Constraint violation");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void shouldHandleGenericException() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error occurred");

        // Act
        StatusRuntimeException result = grpcErrorHandler.defaultHandler(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.INTERNAL);
        assertThat(result.getStatus().getDescription()).isEqualTo("Unexpected error occurred");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle exception with null message")
    void shouldHandleExceptionWithNullMessage() {
        // Arrange
        Exception exception = new RuntimeException();

        // Act
        StatusRuntimeException result = grpcErrorHandler.defaultHandler(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.INTERNAL);
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException")
    void shouldHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleIllegalArgumentException(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
        assertThat(result.getStatus().getDescription()).isEqualTo("Invalid argument");
        assertThat(result.getCause()).isEqualTo(exception);
    }

    @Test
    @DisplayName("Should handle NullPointerException")
    void shouldHandleNullPointerException() {
        // Arrange
        NullPointerException exception = new NullPointerException("Null pointer occurred");

        // Act
        StatusRuntimeException result = grpcErrorHandler.defaultHandler(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.INTERNAL);
        assertThat(result.getStatus().getDescription()).isEqualTo("Null pointer occurred");
    }

    @Test
    @DisplayName("Should preserve exception cause chain")
    void shouldPreserveExceptionCauseChain() {
        // Arrange
        Throwable rootCause = new IllegalStateException("Root cause");
        Exception exception = new RuntimeException("Wrapper exception", rootCause);

        // Act
        StatusRuntimeException result = grpcErrorHandler.defaultHandler(exception);

        // Assert
        assertThat(result.getCause()).isEqualTo(exception);
        assertThat(result.getCause().getCause()).isEqualTo(rootCause);
    }

    @Test
    @DisplayName("Should handle exception with special characters in message")
    void shouldHandleExceptionWithSpecialCharacters() {
        // Arrange
        Exception exception = new RuntimeException("Error with special chars: @#$%^&*()");

        // Act
        StatusRuntimeException result = grpcErrorHandler.defaultHandler(exception);

        // Assert
        assertThat(result.getStatus().getDescription())
                .contains("@#$%^&*()");
    }

    @Test
    @DisplayName("Should handle exception with Unicode characters in message")
    void shouldHandleExceptionWithUnicodeCharacters() {
        // Arrange
        Exception exception = new RuntimeException("エラーが発生しました");

        // Act
        StatusRuntimeException result = grpcErrorHandler.defaultHandler(exception);

        // Assert
        assertThat(result.getStatus().getDescription())
                .isEqualTo("エラーが発生しました");
    }

    @Test
    @DisplayName("Should extract artist name from PostgreSQL duplicate key error")
    void shouldExtractArtistNameFromPostgreSqlDuplicateKeyError() {
        // Arrange
        String pgError = "ERROR: duplicate key value violates unique constraint \"artist_name_key\"\n" +
                "  Detail: Key (name)=(Pablo Picasso) already exists.";
        SQLException sqlException = new SQLException(pgError);
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "could not execute statement", sqlException);

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleDataIntegrity(exception);

        // Assert
        assertThat(result.getStatus().getDescription())
                .isEqualTo("Artist Pablo Picasso already exists");
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException when name extraction fails")
    void shouldHandleDataIntegrityViolationExceptionWhenNameExtractionFails() {
        // Arrange
        String errorMessage = "ERROR: duplicate key value violates unique constraint";
        SQLException sqlException = new SQLException(errorMessage);
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "could not execute statement", sqlException);

        // Act
        StatusRuntimeException result = grpcErrorHandler.handleDataIntegrity(exception);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus().getCode()).isEqualTo(Status.Code.ALREADY_EXISTS);
        assertThat(result.getStatus().getDescription()).contains("Artist  already exists");
    }
}