package qa.dboyko.rococo.integrationtests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import qa.dboyko.rococo.entity.PaintingEntity;
import qa.dboyko.rococo.repository.PaintingRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("Integration tests for PaintingRepository")
class PaintingRepositoryIntegrationTest {

    @Autowired
    private PaintingRepository paintingRepository;

    @Test
    @DisplayName("should save and retrieve painting by id")
    void shouldSaveAndRetrievePaintingById() {
        PaintingEntity painting = new PaintingEntity();
        painting.setTitle("Mona Lisa");
        painting.setDescription("Famous painting");
        painting.setArtistId(UUID.randomUUID());
        painting.setMuseumId(UUID.randomUUID());

        PaintingEntity saved = paintingRepository.save(painting);

        PaintingEntity found = paintingRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Mona Lisa", found.getTitle());
        assertEquals("Famous painting", found.getDescription());
    }

    @Test
    @DisplayName("should find paintings by title ignoring case")
    void shouldFindPaintingsByTitleIgnoringCase() {
        PaintingEntity p1 = new PaintingEntity();
        p1.setTitle("Starry Night");
        p1.setArtistId(UUID.randomUUID());
        p1.setMuseumId(UUID.randomUUID());

        PaintingEntity p2 = new PaintingEntity();
        p2.setTitle("starry dawn");
        p2.setArtistId(UUID.randomUUID());
        p2.setMuseumId(UUID.randomUUID());

        paintingRepository.saveAll(List.of(p1, p2));

        Page<PaintingEntity> result = paintingRepository.findAllByTitleContainsIgnoreCase(
                "starry", PageRequest.of(0, 10)
        );

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(p -> p.getTitle().equals("Starry Night")));
        assertTrue(result.getContent().stream().anyMatch(p -> p.getTitle().equals("starry dawn")));
    }

    @Test
    @DisplayName("should find paintings by artist id")
    void shouldFindPaintingsByArtistId() {
        UUID artistId = UUID.randomUUID();

        PaintingEntity p1 = new PaintingEntity();
        p1.setTitle("The Scream");
        p1.setArtistId(artistId);
        p1.setMuseumId(UUID.randomUUID());

        PaintingEntity p2 = new PaintingEntity();
        p2.setTitle("Madonna");
        p2.setArtistId(artistId);
        p2.setMuseumId(UUID.randomUUID());

        paintingRepository.saveAll(List.of(p1, p2));

        Page<PaintingEntity> result = paintingRepository.findAllByArtistId(
                artistId, PageRequest.of(0, 10)
        );

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(p -> p.getArtistId().equals(artistId)));
    }

    @Test
    @DisplayName("should return empty page if no paintings match title")
    void shouldReturnEmptyPageForNonMatchingTitle() {
        Page<PaintingEntity> result = paintingRepository.findAllByTitleContainsIgnoreCase(
                "nonexistent", PageRequest.of(0, 10)
        );
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should return empty page if no paintings match artist id")
    void shouldReturnEmptyPageForNonMatchingArtistId() {
        Page<PaintingEntity> result = paintingRepository.findAllByArtistId(
                UUID.randomUUID(), PageRequest.of(0, 10)
        );
        assertTrue(result.isEmpty());
    }
}
