package qa.dboyko.rococo.unittests;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import qa.dboyko.rococo.ex.GrpcErrorHandler;
import qa.dboyko.rococo.ex.PaintingAlreadyExistsException;
import qa.dboyko.rococo.ex.PaintingNotFoundException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GrpcErrorHandlerTest {

    private final GrpcErrorHandler handler = new GrpcErrorHandler();

    @Test
    @DisplayName("should handle IllegalArgumentException and return INVALID_ARGUMENT status")
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        StatusRuntimeException result = handler.handleIllegalArgumentException(ex);

        assertEquals(Status.INVALID_ARGUMENT.getCode(), result.getStatus().getCode());
        assertEquals("Invalid input", result.getStatus().getDescription());
        assertEquals(ex, result.getCause());
    }

    @Test
    @DisplayName("should handle PaintingAlreadyExistsException and return ALREADY_EXISTS status")
    void shouldHandlePaintingAlreadyExistsException() {
        PaintingAlreadyExistsException ex = new PaintingAlreadyExistsException("Mona Lisa");

        StatusRuntimeException result = handler.handleAlreadyExistsException(ex);

        assertEquals(Status.ALREADY_EXISTS.getCode(), result.getStatus().getCode());
        assertEquals("Artist with name Mona Lisa already exists", result.getStatus().getDescription());
        assertEquals(ex, result.getCause());
    }

    @Test
    @DisplayName("should handle PaintingNotFoundException and return NOT_FOUND status")
    void shouldHandlePaintingNotFoundException() {
        PaintingNotFoundException ex = new PaintingNotFoundException("123");

        StatusRuntimeException result = handler.handlePaintingNotFoundException(ex);

        assertEquals(Status.NOT_FOUND.getCode(), result.getStatus().getCode());
        assertEquals("Artist code 123 not found.", result.getStatus().getDescription());
        assertEquals(ex, result.getCause());
    }

    @Test
    @DisplayName("should handle DataIntegrityViolationException with duplicate key message")
    void shouldHandleDataIntegrityViolationExceptionWithDuplicateKey() {
        SQLException sqlEx = new SQLException("ERROR: duplicate key value violates unique constraint \"uq_painting_title_artist\" (name)=(Mona Lisa)");
        DataIntegrityViolationException ex = new DataIntegrityViolationException("DB error", sqlEx);

        StatusRuntimeException result = handler.handleDataIntegrity(ex);

        assertEquals(Status.ALREADY_EXISTS.getCode(), result.getStatus().getCode());
        assertEquals("Painting Mona Lisa already exists", result.getStatus().getDescription());
        assertEquals(ex, result.getCause());
    }

    @Test
    @DisplayName("should handle ConstraintViolationException and return ALREADY_EXISTS status")
    void shouldHandleConstraintViolationException() {
        SQLException sqlEx = new SQLException("Constraint violation");
        ConstraintViolationException ex = new ConstraintViolationException("Constraint error", sqlEx, "constraint_name");

        StatusRuntimeException result = handler.handleConstraint(ex);

        assertEquals(Status.ALREADY_EXISTS.getCode(), result.getStatus().getCode());
        assertEquals("Constraint violation", result.getStatus().getDescription());
        assertEquals(ex, result.getCause());
    }

    @Test
    @DisplayName("should handle generic Exception and return INTERNAL status")
    void shouldHandleGenericException() {
        Exception ex = new Exception("Something went wrong");

        StatusRuntimeException result = handler.defaultHandler(ex);

        assertEquals(Status.INTERNAL.getCode(), result.getStatus().getCode());
        assertEquals("Something went wrong", result.getStatus().getDescription());
        assertEquals(ex, result.getCause());
    }
}
