package qa.dboyko.rococo.integrationtests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import qa.dboyko.rococo.entity.ArtistEntity;
import qa.dboyko.rococo.repository.ArtistRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("ArtistRepository Integration Tests")
class ArtistRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ========== Basic CRUD Tests ==========

    @Test
    @DisplayName("Should save and find artist by id")
    void shouldSaveAndFindArtistById() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Vincent van Gogh");
        artist.setBiography("Dutch post-impressionist painter.");

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> foundArtist = artistRepository.findById(savedArtist.getId());

        // Assert
        assertThat(foundArtist).isPresent();
        assertThat(foundArtist.get().getId()).isEqualTo(savedArtist.getId());
        assertThat(foundArtist.get().getName()).isEqualTo("Vincent van Gogh");
        assertThat(foundArtist.get().getBiography()).isEqualTo("Dutch post-impressionist painter.");
    }

    @Test
    @DisplayName("Should save artist with all fields including photo")
    void shouldSaveArtistWithAllFields() {
        // Arrange
        byte[] photoData = "test_photo_content".getBytes(StandardCharsets.UTF_8);
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Claude Monet");
        artist.setBiography("French impressionist painter.");
        artist.setPhoto(photoData);

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> foundArtist = artistRepository.findById(savedArtist.getId());

        // Assert
        assertThat(foundArtist).isPresent();
        assertThat(foundArtist.get().getPhoto()).isEqualTo(photoData);
    }

    @Test
    @DisplayName("Should generate UUID automatically")
    void shouldGenerateUuidAutomatically() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Leonardo da Vinci");
        artist.setBiography("Italian Renaissance polymath.");

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();

        // Assert
        assertThat(savedArtist.getId()).isNonnull();
        assertThat(savedArtist.getId()).isInstanceOf(UUID.class);
    }

    @Test
    @DisplayName("Should delete artist by id")
    void shouldDeleteArtistById() {
        // Arrange
        ArtistEntity artist = createArtist("Test Artist", "Test Biography");
        UUID artistId = artist.getId();

        // Act
        artistRepository.deleteById(artistId);
        entityManager.flush();

        Optional<ArtistEntity> deletedArtist = artistRepository.findById(artistId);

        // Assert
        assertThat(deletedArtist).isEmpty();
    }

    @Test
    @DisplayName("Should update artist")
    void shouldUpdateArtist() {
        // Arrange
        ArtistEntity artist = createArtist("Original Name", "Original Biography");
        UUID artistId = artist.getId();

        // Act
        artist.setName("Updated Name");
        artist.setBiography("Updated Biography");
        artist.setPhoto("new_photo".getBytes(StandardCharsets.UTF_8));
        artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> updatedArtist = artistRepository.findById(artistId);

        // Assert
        assertThat(updatedArtist).isPresent();
        assertThat(updatedArtist.get().getName()).isEqualTo("Updated Name");
        assertThat(updatedArtist.get().getBiography()).isEqualTo("Updated Biography");
        assertThat(updatedArtist.get().getPhoto()).isNotEmpty();
    }

    @Test
    @DisplayName("Should count all artists")
    void shouldCountAllArtists() {
        // Arrange
        createArtist("Artist 1", "Bio 1");
        createArtist("Artist 2", "Bio 2");
        createArtist("Artist 3", "Bio 3");

        // Act
        long count = artistRepository.count();

        // Assert
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should check if artist exists by id")
    void shouldCheckIfArtistExistsById() {
        // Arrange
        ArtistEntity artist = createArtist("Existing Artist", "Biography");

        // Act
        boolean exists = artistRepository.existsById(artist.getId());
        boolean notExists = artistRepository.existsById(UUID.randomUUID());

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find all artists")
    void shouldFindAllArtists() {
        // Arrange
        createArtist("Artist 1", "Bio 1");
        createArtist("Artist 2", "Bio 2");

        // Act
        List<ArtistEntity> allArtists = artistRepository.findAll();

        // Assert
        assertThat(allArtists).hasSize(2);
    }

    // ========== Constraint Violation Tests ==========

    @Test
    @DisplayName("Should throw exception when saving artist with duplicate name")
    void shouldThrowExceptionWhenSavingArtistWithDuplicateName() {
        // Arrange
        createArtist("Unique Artist", "First Biography");

        ArtistEntity duplicateArtist = new ArtistEntity();
        duplicateArtist.setName("Unique Artist");
        duplicateArtist.setBiography("Second Biography");

        // Act & Assert
        assertThatThrownBy(() -> {
            artistRepository.save(duplicateArtist);
            entityManager.flush();
        }).satisfiesAnyOf(
                ex -> assertThat(ex).isInstanceOf(DataIntegrityViolationException.class),
                ex -> assertThat(ex).isInstanceOf(org.hibernate.exception.ConstraintViolationException.class)
        );
    }

    @Test
    @DisplayName("Should throw exception when saving artist without name")
    void shouldThrowExceptionWhenSavingArtistWithoutName() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setBiography("Biography without name");

        // Act & Assert
        assertThatThrownBy(() -> {
            artistRepository.save(artist);
            entityManager.flush();
        }).satisfiesAnyOf(
                ex -> assertThat(ex).isInstanceOf(DataIntegrityViolationException.class),
                ex -> assertThat(ex).isInstanceOf(org.hibernate.exception.ConstraintViolationException.class)
        );
    }

    @Test
    @DisplayName("Should throw exception when saving artist with null name")
    void shouldThrowExceptionWhenSavingArtistWithNullName() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName(null);
        artist.setBiography("Biography");

        // Act & Assert
        assertThatThrownBy(() -> {
            artistRepository.save(artist);
            entityManager.flush();
        }).satisfiesAnyOf(
                ex -> assertThat(ex).isInstanceOf(DataIntegrityViolationException.class),
                ex -> assertThat(ex).isInstanceOf(org.hibernate.exception.ConstraintViolationException.class)
        );
    }

    @Test
    @DisplayName("Should allow saving artist with null photo")
    void shouldAllowSavingArtistWithNullPhoto() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist Without Photo");
        artist.setBiography("Biography");
        artist.setPhoto(null);

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();

        // Assert
        assertThat(savedArtist.getPhoto()).isNull();
    }

    @Test
    @DisplayName("Should allow saving artist with empty photo")
    void shouldAllowSavingArtistWithEmptyPhoto() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist With Empty Photo");
        artist.setBiography("Biography");
        artist.setPhoto(new byte[0]);

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();

        // Assert
        assertThat(savedArtist.getPhoto()).isEmpty();
    }

    // ========== Biography Field Tests ==========

    @Test
    @DisplayName("Should allow saving artist with null biography")
    void shouldAllowSavingArtistWithNullBiography() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist Without Biography");
        artist.setBiography(null);

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> foundArtist = artistRepository.findById(savedArtist.getId());

        // Assert
        assertThat(foundArtist).isPresent();
        assertThat(foundArtist.get().getBiography()).isNull();
    }

    @Test
    @DisplayName("Should allow saving artist with empty biography")
    void shouldAllowSavingArtistWithEmptyBiography() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist With Empty Biography");
        artist.setBiography("");

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> foundArtist = artistRepository.findById(savedArtist.getId());

        // Assert
        assertThat(foundArtist).isPresent();
        assertThat(foundArtist.get().getBiography()).isEmpty();
    }

    @Test
    @DisplayName("Should allow saving artist with blank biography")
    void shouldAllowSavingArtistWithBlankBiography() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist With Blank Biography");
        artist.setBiography("   ");

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> foundArtist = artistRepository.findById(savedArtist.getId());

        // Assert
        assertThat(foundArtist).isPresent();
        assertThat(foundArtist.get().getBiography()).isEqualTo("   ");
    }

    @Test
    @DisplayName("Should update biography from non-null to null")
    void shouldUpdateBiographyFromNonNullToNull() {
        // Arrange
        ArtistEntity artist = createArtist("Test Artist", "Original Biography");
        UUID artistId = artist.getId();

        // Act
        artist.setBiography(null);
        artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> updatedArtist = artistRepository.findById(artistId);

        // Assert
        assertThat(updatedArtist).isPresent();
        assertThat(updatedArtist.get().getBiography()).isNull();
    }

    @Test
    @DisplayName("Should update biography from null to non-null")
    void shouldUpdateBiographyFromNullToNonNull() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist Initially Without Biography");
        artist.setBiography(null);
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();
        UUID artistId = savedArtist.getId();

        // Act
        savedArtist.setBiography("New Biography Added");
        artistRepository.save(savedArtist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> updatedArtist = artistRepository.findById(artistId);

        // Assert
        assertThat(updatedArtist).isPresent();
        assertThat(updatedArtist.get().getBiography()).isEqualTo("New Biography Added");
    }

    @Test
    @DisplayName("Should handle biography with special characters")
    void shouldHandleBiographyWithSpecialCharacters() {
        // Arrange
        String specialBiography = "Artist's biography with \"quotes\", newlines\nand tabs\t!";
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist With Special Bio");
        artist.setBiography(specialBiography);

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> foundArtist = artistRepository.findById(savedArtist.getId());

        // Assert
        assertThat(foundArtist).isPresent();
        assertThat(foundArtist.get().getBiography()).isEqualTo(specialBiography);
    }

    @Test
    @DisplayName("Should handle biography with Unicode characters")
    void shouldHandleBiographyWithUnicodeCharacters() {
        // Arrange
        String unicodeBiography = "葛飾北斎は日本の画家です。Он был великим художником. 🎨";
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist With Unicode Bio");
        artist.setBiography(unicodeBiography);

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        Optional<ArtistEntity> foundArtist = artistRepository.findById(savedArtist.getId());

        // Assert
        assertThat(foundArtist).isPresent();
        assertThat(foundArtist.get().getBiography()).isEqualTo(unicodeBiography);
    }

    @Test
    @DisplayName("Should handle biography with minimum length (1 character)")
    void shouldHandleBiographyWithMinimumLength() {
        // Arrange
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist With Min Bio");
        artist.setBiography("A");

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();

        // Assert
        assertThat(savedArtist.getBiography()).hasSize(1).isEqualTo("A");
    }

    // ========== Search Tests ==========

    @Test
    @DisplayName("Should find artists by name containing ignore case")
    void shouldFindArtistsByNameContainingIgnoreCase() {
        // Arrange
        createArtist("Vincent van Gogh", "Dutch painter");
        createArtist("Pablo Picasso", "Spanish painter");
        createArtist("Claude Monet", "French painter");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ArtistEntity> result = artistRepository.findAllByNameContainsIgnoreCase("van", pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Vincent van Gogh");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find artists with case insensitive search")
    void shouldFindArtistsWithCaseInsensitiveSearch() {
        // Arrange
        createArtist("Leonardo da Vinci", "Italian painter");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ArtistEntity> resultLowerCase = artistRepository.findAllByNameContainsIgnoreCase("leonardo", pageable);
        Page<ArtistEntity> resultUpperCase = artistRepository.findAllByNameContainsIgnoreCase("LEONARDO", pageable);
        Page<ArtistEntity> resultMixedCase = artistRepository.findAllByNameContainsIgnoreCase("LeOnArDo", pageable);

        // Assert
        assertThat(resultLowerCase.getContent()).hasSize(1);
        assertThat(resultUpperCase.getContent()).hasSize(1);
        assertThat(resultMixedCase.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should return empty page when no artists match search")
    void shouldReturnEmptyPageWhenNoMatch() {
        // Arrange
        createArtist("Vincent van Gogh", "Dutch painter");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ArtistEntity> result = artistRepository.findAllByNameContainsIgnoreCase("Picasso", pageable);

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getTotalPages()).isZero();
    }

    @Test
    @DisplayName("Should find multiple artists matching search criteria")
    void shouldFindMultipleArtistsMatchingSearch() {
        // Arrange
        createArtist("Vincent van Gogh", "Dutch painter");
        createArtist("Rembrandt van Rijn", "Dutch painter");
        createArtist("Johannes Vermeer", "Dutch painter");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ArtistEntity> result = artistRepository.findAllByNameContainsIgnoreCase("van", pageable);

        // Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(ArtistEntity::getName)
                .containsExactlyInAnyOrder("Vincent van Gogh", "Rembrandt van Rijn");
    }

    @Test
    @DisplayName("Should find artist by partial name match")
    void shouldFindArtistByPartialNameMatch() {
        // Arrange
        createArtist("Pablo Diego José Francisco de Paula Juan Nepomuceno", "Spanish painter");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ArtistEntity> result = artistRepository.findAllByNameContainsIgnoreCase("José", pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).contains("José");
    }

    @Test
    @DisplayName("Should handle special characters in search")
    void shouldHandleSpecialCharactersInSearch() {
        // Arrange
        createArtist("Artist & Co.", "Biography");
        createArtist("Artist-Painter", "Biography");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ArtistEntity> resultAmpersand = artistRepository.findAllByNameContainsIgnoreCase("&", pageable);
        Page<ArtistEntity> resultHyphen = artistRepository.findAllByNameContainsIgnoreCase("-", pageable);

        // Assert
        assertThat(resultAmpersand.getContent()).hasSize(1);
        assertThat(resultHyphen.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should handle Unicode characters in search")
    void shouldHandleUnicodeCharactersInSearch() {
        // Arrange
        createArtist("葛飾北斎", "Japanese painter");
        createArtist("Василий Кандинский", "Russian painter");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ArtistEntity> resultJapanese = artistRepository.findAllByNameContainsIgnoreCase("葛飾", pageable);
        Page<ArtistEntity> resultRussian = artistRepository.findAllByNameContainsIgnoreCase("Кандинский", pageable);

        // Assert
        assertThat(resultJapanese.getContent()).hasSize(1);
        assertThat(resultRussian.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Should return all artists when searching with empty string")
    void shouldReturnAllArtistsWhenSearchingWithEmptyString() {
        // Arrange
        createArtist("Artist 1", "Bio 1");
        createArtist("Artist 2", "Bio 2");
        createArtist("Artist 3", "Bio 3");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<ArtistEntity> result = artistRepository.findAllByNameContainsIgnoreCase("", pageable);

        // Assert
        assertThat(result.getContent()).hasSize(3);
    }

    // ========== Pagination Tests ==========

    @Test
    @DisplayName("Should support pagination")
    void shouldSupportPagination() {
        // Arrange
        for (int i = 0; i < 15; i++) {
            createArtist("Artist " + i, "Biography " + i);
        }

        Pageable firstPage = PageRequest.of(0, 5);
        Pageable secondPage = PageRequest.of(1, 5);
        Pageable thirdPage = PageRequest.of(2, 5);

        // Act
        Page<ArtistEntity> page1 = artistRepository.findAllByNameContainsIgnoreCase("Artist", firstPage);
        Page<ArtistEntity> page2 = artistRepository.findAllByNameContainsIgnoreCase("Artist", secondPage);
        Page<ArtistEntity> page3 = artistRepository.findAllByNameContainsIgnoreCase("Artist", thirdPage);

        // Assert
        assertThat(page1.getContent()).hasSize(5);
        assertThat(page2.getContent()).hasSize(5);
        assertThat(page3.getContent()).hasSize(5);
        assertThat(page1.getTotalElements()).isEqualTo(15);
        assertThat(page1.getTotalPages()).isEqualTo(3);
        assertThat(page1.isFirst()).isTrue();
        assertThat(page3.isLast()).isTrue();
    }

    @Test
    @DisplayName("Should handle last page with fewer elements")
    void shouldHandleLastPageWithFewerElements() {
        // Arrange
        for (int i = 0; i < 7; i++) {
            createArtist("Artist " + i, "Biography " + i);
        }

        Pageable lastPage = PageRequest.of(1, 5);

        // Act
        Page<ArtistEntity> result = artistRepository.findAllByNameContainsIgnoreCase("Artist", lastPage);

        // Assert
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(7);
        assertThat(result.isLast()).isTrue();
    }

    @Test
    @DisplayName("Should return empty page when requesting page beyond available")
    void shouldReturnEmptyPageWhenBeyondAvailable() {
        // Arrange
        createArtist("Artist 1", "Bio 1");

        Pageable beyondPage = PageRequest.of(10, 5);

        // Act
        Page<ArtistEntity> result = artistRepository.findAllByNameContainsIgnoreCase("Artist", beyondPage);

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // ========== Boundary Tests ==========

    @Test
    @DisplayName("Should handle maximum length name (255 characters)")
    void shouldHandleMaximumLengthName() {
        // Arrange
        String maxLengthName = "A".repeat(255);
        ArtistEntity artist = new ArtistEntity();
        artist.setName(maxLengthName);
        artist.setBiography("Biography");

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();

        // Assert
        assertThat(savedArtist.getName()).hasSize(255);
    }

    @Test
    @DisplayName("Should handle maximum length biography (2000 characters)")
    void shouldHandleMaximumLengthBiography() {
        // Arrange
        String maxLengthBiography = "B".repeat(2000);
        ArtistEntity artist = new ArtistEntity();
        artist.setName("Test Artist");
        artist.setBiography(maxLengthBiography);

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();

        // Assert
        assertThat(savedArtist.getBiography()).hasSize(2000);
    }

    @Test
    @DisplayName("Should handle large photo data")
    void shouldHandleLargePhotoData() {
        // Arrange
        byte[] largePhoto = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largePhoto.length; i++) {
            largePhoto[i] = (byte) (i % 256);
        }

        ArtistEntity artist = new ArtistEntity();
        artist.setName("Artist With Large Photo");
        artist.setBiography("Biography");
        artist.setPhoto(largePhoto);

        // Act
        ArtistEntity savedArtist = artistRepository.save(artist);
        entityManager.flush();
        entityManager.clear();

        ArtistEntity foundArtist = artistRepository.findById(savedArtist.getId()).orElseThrow();

        // Assert
        assertThat(foundArtist.getPhoto()).hasSize(1024 * 1024);
    }

    // ========== Helper Methods ==========

    private ArtistEntity createArtist(String name, String biography) {
        ArtistEntity artist = new ArtistEntity();
        artist.setName(name);
        artist.setBiography(biography);
        ArtistEntity saved = artistRepository.save(artist);
        entityManager.flush();
        return saved;
    }
}