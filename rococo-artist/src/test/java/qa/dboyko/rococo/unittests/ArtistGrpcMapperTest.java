package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.Artist;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import qa.dboyko.rococo.entity.ArtistEntity;
import qa.dboyko.rococo.mapper.ArtistGrpcMapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ArtistGrpcMapper Unit Tests")
class ArtistGrpcMapperTest {

    // ========== toGrpc Tests ==========

    @Test
    @DisplayName("Should convert entity to gRPC with all fields")
    void shouldConvertEntityToGrpcWithAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Vincent van Gogh";
        String biography = "Dutch post-impressionist painter.";
        byte[] photo = "photo_content".getBytes(StandardCharsets.UTF_8);

        ArtistEntity entity = new ArtistEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setBiography(biography);
        entity.setPhoto(photo);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(entity);

        // Assert
        assertThat(grpcArtist).isNonnull();
        assertThat(grpcArtist.getId()).isEqualTo(id.toString());
        assertThat(grpcArtist.getName()).isEqualTo(name);
        assertThat(grpcArtist.getBiography()).isEqualTo(biography);
        assertThat(grpcArtist.getPhoto()).isEqualTo(new String(photo, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should convert entity to gRPC with required fields only")
    void shouldConvertEntityToGrpcWithRequiredFieldsOnly() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Pablo Picasso";

        ArtistEntity entity = new ArtistEntity();
        entity.setId(id);
        entity.setName(name);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(entity);

        // Assert
        assertThat(grpcArtist).isNonnull();
        assertThat(grpcArtist.getId()).isEqualTo(id.toString());
        assertThat(grpcArtist.getName()).isEqualTo(name);
        assertThat(grpcArtist.getBiography()).isEmpty();
        assertThat(grpcArtist.getPhoto()).isEmpty();
    }

    @Test
    @DisplayName("Should return null when converting null entity to gRPC")
    void shouldReturnNullWhenConvertingNullEntityToGrpc() {
        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(null);

        // Assert
        assertThat(grpcArtist).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("Should not include blank biography in gRPC")
    void shouldNotIncludeBlankBiographyInGrpc(String biography) {
        // Arrange
        ArtistEntity entity = new ArtistEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Artist Name");
        entity.setBiography(biography);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(entity);

        // Assert
        assertThat(grpcArtist.getBiography()).isEmpty();
    }

    @Test
    @DisplayName("Should not include null photo in gRPC")
    void shouldNotIncludeNullPhotoInGrpc() {
        // Arrange
        ArtistEntity entity = new ArtistEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Artist Name");
        entity.setPhoto(null);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(entity);

        // Assert
        assertThat(grpcArtist.getPhoto()).isEmpty();
    }

    @Test
    @DisplayName("Should not include empty photo in gRPC")
    void shouldNotIncludeEmptyPhotoInGrpc() {
        // Arrange
        ArtistEntity entity = new ArtistEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Artist Name");
        entity.setPhoto(new byte[0]);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(entity);

        // Assert
        assertThat(grpcArtist.getPhoto()).isEmpty();
    }

    @Test
    @DisplayName("Should handle large photo when converting to gRPC")
    void shouldHandleLargePhotoWhenConvertingToGrpc() {
        // Arrange
        byte[] largePhoto = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largePhoto.length; i++) {
            largePhoto[i] = (byte) (i % 256);
        }

        ArtistEntity entity = new ArtistEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Artist Name");
        entity.setPhoto(largePhoto);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(entity);

        // Assert
        assertThat(grpcArtist.getPhoto()).isEqualTo(new String(largePhoto, StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should handle Unicode characters when converting to gRPC")
    void shouldHandleUnicodeCharactersWhenConvertingToGrpc() {
        // Arrange
        String unicodeName = "北斎 葛飾 🎨";
        String unicodeBiography = "日本の画家 with émotions";

        ArtistEntity entity = new ArtistEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(unicodeName);
        entity.setBiography(unicodeBiography);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(entity);

        // Assert
        assertThat(grpcArtist.getName()).isEqualTo(unicodeName);
        assertThat(grpcArtist.getBiography()).isEqualTo(unicodeBiography);
    }

    // ========== fromGrpc Tests ==========

    @Test
    @DisplayName("Should convert gRPC to entity with all fields")
    void shouldConvertGrpcToEntityWithAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Claude Monet";
        String biography = "French impressionist painter.";
        String photoString = "photo_content";

        Artist grpcArtist = Artist.newBuilder()
                .setId(id.toString())
                .setName(name)
                .setBiography(biography)
                .setPhoto(photoString)
                .build();

        // Act
        ArtistEntity entity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(entity).isNonnull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getBiography()).isEqualTo(biography);
        assertThat(entity.getPhoto()).isEqualTo(photoString.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should convert gRPC to entity with required fields only")
    void shouldConvertGrpcToEntityWithRequiredFieldsOnly() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Leonardo da Vinci";

        Artist grpcArtist = Artist.newBuilder()
                .setId(id.toString())
                .setName(name)
                .build();

        // Act
        ArtistEntity entity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(entity).isNonnull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getBiography()).isNull();
        assertThat(entity.getPhoto()).isNull();
    }

    @Test
    @DisplayName("Should return null when converting null gRPC to entity")
    void shouldReturnNullWhenConvertingNullGrpcToEntity() {
        // Act
        ArtistEntity entity = ArtistGrpcMapper.fromGrpc(null);

        // Assert
        assertThat(entity).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("Should not set blank biography from gRPC")
    void shouldNotSetBlankBiographyFromGrpc(String biography) {
        // Arrange
        Artist grpcArtist = Artist.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Artist Name")
                .setBiography(biography == null ? "" : biography)
                .build();

        // Act
        ArtistEntity entity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(entity.getBiography()).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("Should not set blank photo from gRPC")
    void shouldNotSetBlankPhotoFromGrpc(String photo) {
        // Arrange
        Artist grpcArtist = Artist.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Artist Name")
                .setPhoto(photo == null ? "" : photo)
                .build();

        // Act
        ArtistEntity entity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(entity.getPhoto()).isNull();
    }

    @Test
    @DisplayName("Should handle large photo when converting from gRPC")
    void shouldHandleLargePhotoWhenConvertingFromGrpc() {
        // Arrange
        byte[] largePhotoBytes = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largePhotoBytes.length; i++) {
            largePhotoBytes[i] = (byte) (i % 256);
        }
        String largePhoto = new String(largePhotoBytes, StandardCharsets.UTF_8);

        Artist grpcArtist = Artist.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Artist Name")
                .setPhoto(largePhoto)
                .build();

        // Act
        ArtistEntity entity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(entity.getPhoto()).isEqualTo(largePhoto.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should handle Unicode characters when converting from gRPC")
    void shouldHandleUnicodeCharactersWhenConvertingFromGrpc() {
        // Arrange
        String unicodeName = "フィンセント・ファン・ゴッホ 🎨";
        String unicodeBiography = "オランダの画家 with special chars: é, ñ, ü";

        Artist grpcArtist = Artist.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName(unicodeName)
                .setBiography(unicodeBiography)
                .build();

        // Act
        ArtistEntity entity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(entity.getName()).isEqualTo(unicodeName);
        assertThat(entity.getBiography()).isEqualTo(unicodeBiography);
    }

    @Test
    @DisplayName("Should correctly parse UUID from gRPC")
    void shouldCorrectlyParseUuidFromGrpc() {
        // Arrange
        UUID expectedId = UUID.randomUUID();

        Artist grpcArtist = Artist.newBuilder()
                .setId(expectedId.toString())
                .setName("Artist Name")
                .build();

        // Act
        ArtistEntity entity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(entity.getId()).isEqualTo(expectedId);
    }

    // ========== Round-trip Tests ==========

    @Test
    @DisplayName("Should maintain data integrity in entity -> gRPC -> entity conversion")
    void shouldMaintainDataIntegrityInRoundTripConversion() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Rembrandt van Rijn";
        String biography = "Dutch Golden Age painter.";
        byte[] photo = "test_photo_content".getBytes(StandardCharsets.UTF_8);

        ArtistEntity originalEntity = new ArtistEntity();
        originalEntity.setId(id);
        originalEntity.setName(name);
        originalEntity.setBiography(biography);
        originalEntity.setPhoto(photo);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(originalEntity);
        ArtistEntity convertedEntity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(convertedEntity.getId()).isEqualTo(originalEntity.getId());
        assertThat(convertedEntity.getName()).isEqualTo(originalEntity.getName());
        assertThat(convertedEntity.getBiography()).isEqualTo(originalEntity.getBiography());
        assertThat(convertedEntity.getPhoto()).isEqualTo(originalEntity.getPhoto());
    }

    @Test
    @DisplayName("Should maintain data integrity in gRPC -> entity -> gRPC conversion")
    void shouldMaintainDataIntegrityInReverseRoundTripConversion() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Michelangelo";
        String biography = "Italian Renaissance artist.";
        String photo = "photo_data";

        Artist originalGrpc = Artist.newBuilder()
                .setId(id.toString())
                .setName(name)
                .setBiography(biography)
                .setPhoto(photo)
                .build();

        // Act
        ArtistEntity entity = ArtistGrpcMapper.fromGrpc(originalGrpc);
        Artist convertedGrpc = ArtistGrpcMapper.toGrpc(entity);

        // Assert
        assertThat(convertedGrpc.getId()).isEqualTo(originalGrpc.getId());
        assertThat(convertedGrpc.getName()).isEqualTo(originalGrpc.getName());
        assertThat(convertedGrpc.getBiography()).isEqualTo(originalGrpc.getBiography());
        assertThat(convertedGrpc.getPhoto()).isEqualTo(originalGrpc.getPhoto());
    }

    @Test
    @DisplayName("Should handle minimum valid data in round-trip conversion")
    void shouldHandleMinimumValidDataInRoundTripConversion() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Artist";

        ArtistEntity originalEntity = new ArtistEntity();
        originalEntity.setId(id);
        originalEntity.setName(name);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(originalEntity);
        ArtistEntity convertedEntity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(convertedEntity.getId()).isEqualTo(originalEntity.getId());
        assertThat(convertedEntity.getName()).isEqualTo(originalEntity.getName());
        assertThat(convertedEntity.getBiography()).isNull();
        assertThat(convertedEntity.getPhoto()).isNull();
    }

    // ========== Edge Cases Tests ==========

    @Test
    @DisplayName("Should handle maximum length strings in conversion")
    void shouldHandleMaximumLengthStringsInConversion() {
        // Arrange
        String maxLengthName = "N".repeat(255);
        String maxLengthBiography = "B".repeat(2000);

        ArtistEntity entity = new ArtistEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(maxLengthName);
        entity.setBiography(maxLengthBiography);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(entity);
        ArtistEntity convertedEntity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(convertedEntity.getName()).hasSize(255);
        assertThat(convertedEntity.getBiography()).hasSize(2000);
        assertThat(convertedEntity.getName()).isEqualTo(maxLengthName);
        assertThat(convertedEntity.getBiography()).isEqualTo(maxLengthBiography);
    }

    @Test
    @DisplayName("Should handle special characters in all fields")
    void shouldHandleSpecialCharactersInAllFields() {
        // Arrange
        String specialName = "Artist™ & Co. (1881-1973)";
        String specialBiography = "Bio with \"quotes\", 'apostrophes', and\nnewlines\ttabs";
        byte[] specialPhoto = "Photo with\0null\rbytes".getBytes(StandardCharsets.UTF_8);

        ArtistEntity entity = new ArtistEntity();
        entity.setId(UUID.randomUUID());
        entity.setName(specialName);
        entity.setBiography(specialBiography);
        entity.setPhoto(specialPhoto);

        // Act
        Artist grpcArtist = ArtistGrpcMapper.toGrpc(entity);
        ArtistEntity convertedEntity = ArtistGrpcMapper.fromGrpc(grpcArtist);

        // Assert
        assertThat(convertedEntity.getName()).isEqualTo(specialName);
        assertThat(convertedEntity.getBiography()).isEqualTo(specialBiography);
        assertThat(convertedEntity.getPhoto()).isEqualTo(specialPhoto);
    }
}