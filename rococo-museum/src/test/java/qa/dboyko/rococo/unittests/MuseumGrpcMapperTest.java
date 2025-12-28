package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.Museum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.entity.MuseumEntity;
import qa.dboyko.rococo.mapper.MuseumGrpcMapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MuseumGrpcMapper Unit Tests")
class MuseumGrpcMapperTest {

    // ================= toGrpcMuseum =================

    @Test
    @DisplayName("Should map full MuseumEntity to gRPC Museum")
    void shouldMapEntityToGrpc() {
        // Arrange
        MuseumEntity entity = new MuseumEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Louvre");
        entity.setDescription("Paris museum");
        entity.setCity("Paris");
        entity.setCountryId(UUID.randomUUID());
        entity.setPhoto("photo".getBytes(StandardCharsets.UTF_8));

        // Act
        Museum grpc = MuseumGrpcMapper.toGrpcMuseum(entity);

        // Assert
        assertThat(grpc.getId()).isEqualTo(entity.getId().toString());
        assertThat(grpc.getTitle()).isEqualTo("Louvre");
        assertThat(grpc.getDescription()).isEqualTo("Paris museum");
        assertThat(grpc.getCity()).isEqualTo("Paris");
        assertThat(grpc.getPhoto()).isEqualTo("photo");
    }

    @Test
    @DisplayName("Should not include optional fields when empty or null")
    void shouldSkipEmptyOptionalFields() {
        // Arrange
        MuseumEntity entity = new MuseumEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Hermitage");
        entity.setDescription(null);
        entity.setCity("");
        entity.setPhoto(null);
        entity.setCountryId(null);

        // Act
        Museum grpc = MuseumGrpcMapper.toGrpcMuseum(entity);

        // Assert
        assertThat(grpc.getDescription()).isEmpty();
        assertThat(grpc.getCity()).isEmpty();
        assertThat(grpc.getPhoto()).isEmpty();
        assertThat(grpc.getCountryId()).isEmpty();
    }

    @Test
    @DisplayName("Should throw when id is null")
    void shouldThrowWhenIdNull() {
        MuseumEntity entity = new MuseumEntity();
        entity.setTitle("Test");

        assertThatThrownBy(() -> MuseumGrpcMapper.toGrpcMuseum(entity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("id");
    }

    @Test
    @DisplayName("Should throw when title is blank")
    void shouldThrowWhenTitleBlank() {
        MuseumEntity entity = new MuseumEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("");

        assertThatThrownBy(() -> MuseumGrpcMapper.toGrpcMuseum(entity))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("title");
    }

    // ================= fromGrpcMuseum =================

    @Test
    @DisplayName("Should map full gRPC Museum to entity")
    void shouldMapGrpcToEntity() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String countryId = UUID.randomUUID().toString();

        Museum grpc = Museum.newBuilder()
                .setId(id)
                .setTitle("Louvre")
                .setDescription("Paris museum")
                .setCity("Paris")
                .setCountryId(countryId)
                .setPhoto("photo")
                .build();

        // Act
        MuseumEntity entity = MuseumGrpcMapper.fromGrpcMuseum(grpc);

        // Assert
        assertThat(entity.getId().toString()).isEqualTo(id);
        assertThat(entity.getTitle()).isEqualTo("Louvre");
        assertThat(entity.getDescription()).isEqualTo("Paris museum");
        assertThat(entity.getCity()).isEqualTo("Paris");
        assertThat(new String(entity.getPhoto(), StandardCharsets.UTF_8)).isEqualTo("photo");
        assertThat(entity.getCountryId().toString()).isEqualTo(countryId);
    }

    @Test
    @DisplayName("Should set null photo when empty photo in grpc")
    void shouldSetNullPhotoWhenEmptyGrpc() {
        Museum grpc = Museum.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Test")
                .setPhoto("")
                .build();

        MuseumEntity entity = MuseumGrpcMapper.fromGrpcMuseum(grpc);

        assertThat(entity.getPhoto()).isNull();
    }

    @Test
    @DisplayName("Should throw when grpc id empty")
    void shouldThrowWhenGrpcIdEmpty() {
        Museum grpc = Museum.newBuilder()
                .setId("")
                .setTitle("Test")
                .build();

        assertThatThrownBy(() -> MuseumGrpcMapper.fromGrpcMuseum(grpc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    @DisplayName("Should throw when grpc title empty")
    void shouldThrowWhenGrpcTitleEmpty() {
        Museum grpc = Museum.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("")
                .build();

        assertThatThrownBy(() -> MuseumGrpcMapper.fromGrpcMuseum(grpc))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title");
    }
}

