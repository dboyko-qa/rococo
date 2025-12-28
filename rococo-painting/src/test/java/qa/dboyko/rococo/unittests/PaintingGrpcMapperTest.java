package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.Painting;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.entity.PaintingEntity;
import qa.dboyko.rococo.mapper.PaintingGrpcMapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaintingGrpcMapper Unit Tests")
class PaintingGrpcMapperTest {

    @Test
    @DisplayName("should return default instance when entity is null")
    void shouldReturnDefaultInstanceWhenEntityIsNull() {
        // Arrange
        PaintingEntity entity = null;

        // Act
        Painting grpc = PaintingGrpcMapper.toGrpcPainting(entity);

        // Assert
        assertNotNull(grpc);
        assertEquals("", grpc.getId());
        assertEquals("", grpc.getTitle());
    }

    @Test
    @DisplayName("should throw IllegalStateException when entity id is missing")
    void shouldThrowExceptionWhenEntityIdIsMissing() {
        // Arrange
        PaintingEntity entity = new PaintingEntity();
        entity.setTitle("Title");

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> PaintingGrpcMapper.toGrpcPainting(entity));
        assertEquals("Painting id must not be null", ex.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalStateException when entity title is missing")
    void shouldThrowExceptionWhenEntityTitleIsMissing() {
        // Arrange
        PaintingEntity entity = new PaintingEntity();
        entity.setId(UUID.randomUUID());

        // Act & Assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> PaintingGrpcMapper.toGrpcPainting(entity));
        assertEquals("Painting title must not be null or empty", ex.getMessage());
    }

    @Test
    @DisplayName("should map all fields correctly from entity to gRPC")
    void shouldMapAllFieldsWhenEntityHasAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();
        UUID museumId = UUID.randomUUID();
        PaintingEntity entity = new PaintingEntity();
        entity.setId(id);
        entity.setTitle("Mona Lisa");
        entity.setDescription("Description");
        entity.setContent("Content".getBytes(StandardCharsets.UTF_8));
        entity.setArtistId(artistId);
        entity.setMuseumId(museumId);

        // Act
        Painting grpc = PaintingGrpcMapper.toGrpcPainting(entity);

        // Assert
        assertEquals(id.toString(), grpc.getId());
        assertEquals("Mona Lisa", grpc.getTitle());
        assertEquals("Description", grpc.getDescription());
        assertEquals("Content", grpc.getContent());
        assertEquals(artistId.toString(), grpc.getArtistId());
        assertEquals(museumId.toString(), grpc.getMuseumId());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when gRPC id is missing")
    void shouldThrowExceptionWhenGrpcIdIsMissing() {
        // Arrange
        Painting grpc = Painting.newBuilder()
                .setTitle("Title")
                .build();

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PaintingGrpcMapper.fromGrpcPainting(grpc));
        assertEquals("Painting id is required", ex.getMessage());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when gRPC title is missing")
    void shouldThrowExceptionWhenGrpcTitleIsMissing() {
        // Arrange
        Painting grpc = Painting.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> PaintingGrpcMapper.fromGrpcPainting(grpc));
        assertEquals("Painting title is required", ex.getMessage());
    }

    @Test
    @DisplayName("should map all fields correctly from gRPC to entity")
    void shouldMapAllFieldsWhenGrpcHasAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();
        UUID museumId = UUID.randomUUID();
        Painting grpc = Painting.newBuilder()
                .setId(id.toString())
                .setTitle("Starry Night")
                .setDescription("Beautiful painting")
                .setContent("ImageBytes")
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .build();

        // Act
        PaintingEntity entity = PaintingGrpcMapper.fromGrpcPainting(grpc);

        // Assert
        assertEquals(id, entity.getId());
        assertEquals("Starry Night", entity.getTitle());
        assertEquals("Beautiful painting", entity.getDescription());
        assertArrayEquals("ImageBytes".getBytes(StandardCharsets.UTF_8), entity.getContent());
        assertEquals(artistId, entity.getArtistId());
        assertEquals(museumId, entity.getMuseumId());
    }

    @Test
    @DisplayName("should ignore invalid UUIDs for artistId and museumId")
    void shouldIgnoreInvalidUuidWhenArtistIdOrMuseumIdIsInvalid() {
        // Arrange
        Painting grpc = Painting.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Test")
                .setArtistId("invalid-uuid")
                .setMuseumId("another-invalid")
                .build();

        // Act
        PaintingEntity entity = PaintingGrpcMapper.fromGrpcPainting(grpc);

        // Assert
        assertEquals("Test", entity.getTitle());
        assertNull(entity.getArtistId());
        assertNull(entity.getMuseumId());
    }

    @Test
    @DisplayName("should set null for empty optional fields in gRPC to entity mapping")
    void shouldSetNullWhenOptionalFieldsAreEmpty() {
        // Arrange
        Painting grpc = Painting.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Test")
                .setDescription("")
                .setContent("")
                .setArtistId("")
                .setMuseumId("")
                .build();

        // Act
        PaintingEntity entity = PaintingGrpcMapper.fromGrpcPainting(grpc);

        // Assert
        assertEquals("Test", entity.getTitle());
        assertNull(entity.getDescription());
        assertNull(entity.getContent());
        assertNull(entity.getArtistId());
        assertNull(entity.getMuseumId());
    }
}

