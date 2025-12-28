package qa.dboyko.rococo.unittests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.ex.ArtistAlreadyExistsException;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ArtistAlreadyExistsException Unit Tests")
class ArtistAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create exception with default constructor")
    void shouldCreateExceptionWithDefaultConstructor() {
        // Act
        ArtistAlreadyExistsException exception = new ArtistAlreadyExistsException();

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should create exception with artist name")
    void shouldCreateExceptionWithArtistName() {
        // Arrange
        String artistName = "Vincent van Gogh";

        // Act
        ArtistAlreadyExistsException exception = new ArtistAlreadyExistsException(artistName);

        // Assert
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage())
                .isEqualTo("Artist with name Vincent van Gogh already exists");
    }

    @Test
    @DisplayName("Should format message correctly")
    void shouldFormatMessageCorrectly() {
        // Arrange
        String artistName = "Pablo Picasso";

        // Act
        ArtistAlreadyExistsException exception = new ArtistAlreadyExistsException(artistName);

        // Assert
        assertThat(exception.getMessage())
                .contains(artistName)
                .contains("already exists")
                .startsWith("Artist with name");
    }

    @Test
    @DisplayName("Should handle null artist name")
    void shouldHandleNullArtistName() {
        // Act
        ArtistAlreadyExistsException exception = new ArtistAlreadyExistsException(null);

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Artist with name null already exists");
    }

    @Test
    @DisplayName("Should handle empty artist name")
    void shouldHandleEmptyArtistName() {
        // Act
        ArtistAlreadyExistsException exception = new ArtistAlreadyExistsException("");

        // Assert
        assertThat(exception.getMessage()).isEqualTo("Artist with name  already exists");
    }

    @Test
    @DisplayName("Should handle long artist name")
    void shouldHandleLongArtistName() {
        // Arrange
        String longName = "A".repeat(255);

        // Act
        ArtistAlreadyExistsException exception = new ArtistAlreadyExistsException(longName);

        // Assert
        assertThat(exception.getMessage())
                .contains(longName)
                .hasSize(longName.length() + "Artist with name  already exists".length());
    }

    @Test
    @DisplayName("Should handle Unicode characters in artist name")
    void shouldHandleUnicodeCharactersInArtistName() {
        // Arrange
        String unicodeName = "葛飾北斎 🎨";

        // Act
        ArtistAlreadyExistsException exception = new ArtistAlreadyExistsException(unicodeName);

        // Assert
        assertThat(exception.getMessage())
                .contains(unicodeName)
                .isEqualTo("Artist with name 葛飾北斎 🎨 already exists");
    }

    @Test
    @DisplayName("Should handle special characters in artist name")
    void shouldHandleSpecialCharactersInArtistName() {
        // Arrange
        String specialName = "Artist & Co. (1880-1890)";

        // Act
        ArtistAlreadyExistsException exception = new ArtistAlreadyExistsException(specialName);

        // Assert
        assertThat(exception.getMessage())
                .contains(specialName)
                .isEqualTo("Artist with name Artist & Co. (1880-1890) already exists");
    }
}