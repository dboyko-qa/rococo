package qa.dboyko.rococo.unittests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.ex.ArtistNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ArtistNotFoundException Unit Tests")
class ArtistNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with default constructor")
    void shouldCreateExceptionWithDefaultConstructor() {
        // Act
        ArtistNotFoundException exception = new ArtistNotFoundException();

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should create exception with artist code")
    void shouldCreateExceptionWithArtistCode() {
        // Arrange
        String artistCode = "123e4567-e89b-12d3-a456-426614174000";

        // Act
        ArtistNotFoundException exception = new ArtistNotFoundException(artistCode);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage())
                .isEqualTo("Artist code 123e4567-e89b-12d3-a456-426614174000 not found.");
    }

    @Test
    @DisplayName("Should format message correctly with UUID")
    void shouldFormatMessageCorrectlyWithUuid() {
        // Arrange
        String uuid = "550e8400-e29b-41d4-a716-446655440000";

        // Act
        ArtistNotFoundException exception = new ArtistNotFoundException(uuid);

        // Assert
        assertThat(exception.getMessage())
                .contains(uuid)
                .contains("not found");
    }

    @Test
    @DisplayName("Should handle null artist code")
    void shouldHandleNullArtistCode() {
        // Act
        ArtistNotFoundException exception = new ArtistNotFoundException(null);

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Artist code null not found.");
    }

    @Test
    @DisplayName("Should handle empty artist code")
    void shouldHandleEmptyArtistCode() {
        // Act
        ArtistNotFoundException exception = new ArtistNotFoundException("");

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Artist code  not found.");
    }

    @Test
    @DisplayName("Should handle special characters in artist code")
    void shouldHandleSpecialCharactersInArtistCode() {
        // Arrange
        String specialCode = "artist-123!@#$%^&*()";

        // Act
        ArtistNotFoundException exception = new ArtistNotFoundException(specialCode);

        // Assert
        assertThat(exception.getMessage())
                .contains(specialCode)
                .isEqualTo("Artist code artist-123!@#$%^&*() not found.");
    }
}