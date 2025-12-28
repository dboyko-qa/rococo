package qa.dboyko.rococo.integrationtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import qa.dboyko.rococo.entity.MuseumEntity;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import qa.dboyko.rococo.repository.MuseumRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("MuseumRepository Integration Tests")
class MuseumRepositoryTest {

    @Autowired
    private MuseumRepository museumRepository;

    @Autowired
    private TestEntityManager entityManager;

    private MuseumEntity museum1;
    private MuseumEntity museum2;

    @BeforeEach
    void setUp() {
        museumRepository.deleteAll();

        museum1 = new MuseumEntity();
        museum1.setTitle("Louvre");
        museum1.setCity("Paris");
        museum1.setCountryId(UUID.randomUUID());
        museum1.setDescription("Famous museum in Paris");
        museum1.setPhoto("photo1".getBytes());

        museum2 = new MuseumEntity();
        museum2.setTitle("British Museum");
        museum2.setCity("London");
        museum2.setCountryId(UUID.randomUUID());
        museum2.setDescription("Famous museum in London");
        museum2.setPhoto("photo2".getBytes());

        museumRepository.save(museum1);
        museumRepository.save(museum2);
    }

    // ========== Basic CRUD Tests ==========

    @Test
    @DisplayName("Should save and find museum by id")
    void shouldSaveAndFindMuseumById() {
        MuseumEntity museum = new MuseumEntity();
        museum.setTitle("New Museum");
        museum.setCity("Berlin");
        museum.setCountryId(UUID.randomUUID());
        museum.setDescription("New museum in Berlin");

        MuseumEntity saved = museumRepository.save(museum);
        entityManager.flush();
        entityManager.clear();

        Optional<MuseumEntity> found = museumRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("New Museum");
        assertThat(found.get().getCity()).isEqualTo("Berlin");
        assertThat(found.get().getDescription()).isEqualTo("New museum in Berlin");
    }

    @Test
    @DisplayName("Should update museum")
    void shouldUpdateMuseum() {
        museum1.setDescription("Updated description");
        MuseumEntity updated = museumRepository.save(museum1);
        entityManager.flush();
        entityManager.clear();

        Optional<MuseumEntity> found = museumRepository.findById(museum1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Should delete museum")
    void shouldDeleteMuseum() {
        museumRepository.delete(museum1);
        entityManager.flush();
        entityManager.clear();

        Optional<MuseumEntity> found = museumRepository.findById(museum1.getId());
        assertThat(found).isNotPresent();
    }

    // ========== Custom Query Tests ==========

    @Test
    @DisplayName("Should find museums by title containing ignoring case")
    void shouldFindByTitleContainsIgnoreCase() {
        Page<MuseumEntity> result = museumRepository.findAllByTitleContainsIgnoreCase(
                "museum",
                PageRequest.of(0, 10)
        );

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("British Museum");
    }

    // ========== Constraint and Nullable Tests ==========

    @Test
    @DisplayName("Should throw exception when saving duplicate title+city+countryId")
    void shouldFailOnDuplicateTitleCityCountry() {
        MuseumEntity duplicate = new MuseumEntity();
        duplicate.setTitle(museum1.getTitle());
        duplicate.setCity(museum1.getCity());
        duplicate.setCountryId(museum1.getCountryId());

        assertThrows(DataIntegrityViolationException.class, () -> {
            museumRepository.saveAndFlush(duplicate);
        });
    }

    @Test
    @DisplayName("Should allow nullable description and photo")
    void shouldAllowNullableFields() {
        MuseumEntity emptyMuseum = new MuseumEntity();
        emptyMuseum.setTitle("Empty Museum");
        emptyMuseum.setCity(null);
        emptyMuseum.setCountryId(null);
        emptyMuseum.setDescription(null);
        emptyMuseum.setPhoto(null);

        MuseumEntity saved = museumRepository.save(emptyMuseum);
        entityManager.flush();
        entityManager.clear();

        Optional<MuseumEntity> found = museumRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isNull();
        assertThat(found.get().getPhoto()).isNull();
    }

    // ========== Pagination Tests ==========

    @Test
    @DisplayName("Should paginate results")
    void shouldPaginateResults() {
        for (int i = 1; i <= 5; i++) {
            MuseumEntity m = new MuseumEntity();
            m.setTitle("Museum " + i);
            m.setCity("City " + i);
            m.setCountryId(UUID.randomUUID());
            museumRepository.save(m);
        }
        entityManager.flush();
        entityManager.clear();

        Page<MuseumEntity> page1 = museumRepository.findAll(PageRequest.of(0, 3));
        Page<MuseumEntity> page2 = museumRepository.findAll(PageRequest.of(1, 3));

        assertThat(page1.getContent().size()).isEqualTo(3);
        assertThat(page2.getContent().size()).isEqualTo(3);
        assertThat(page1.getTotalElements()).isEqualTo(7);
    }
}
