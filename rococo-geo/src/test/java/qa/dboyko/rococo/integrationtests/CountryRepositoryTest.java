package qa.dboyko.rococo.integrationtests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import qa.dboyko.rococo.entity.CountryEntity;
import qa.dboyko.rococo.repository.CountryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("CountryRepository Integration Tests")
class CountryRepositoryTest {

    @Autowired
    CountryRepository repository;

    @Test
    void findByNameShouldReturnEntityWhenExists() {
        // Arrange
        CountryEntity entity = new CountryEntity();
        entity.setName("Spain");
        repository.save(entity);

        // Act
        Optional<CountryEntity> result = repository.findByName("Spain");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Spain", result.get().getName());
    }

    @Test
    void findByNameShouldReturnEmptyWhenNotExists() {
        // Arrange — empty DB

        // Act
        Optional<CountryEntity> result = repository.findByName("Unknown");

        // Assert
        assertTrue(result.isEmpty());
    }
}
