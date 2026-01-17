package qa.dboyko.rococo.integrationtests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import qa.dboyko.rococo.entity.UserEntity;
import qa.dboyko.rococo.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DisplayName("UserRepository Integration Tests")
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve user by id")
    void shouldSaveAndRetrieveUserById() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setUsername("john");

        // Act
        UserEntity savedUser = userRepository.saveAndFlush(user);
        Optional<UserEntity> retrieved = userRepository.findById(savedUser.getId());

        // Assert
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getUsername()).isEqualTo("john");
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setUsername("alice");
        userRepository.saveAndFlush(user);

        // Act
        Optional<UserEntity> found = userRepository.findByUsername("alice");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("alice");
    }

    @Test
    @DisplayName("Should return empty when username does not exist")
    void shouldReturnEmptyWhenUsernameDoesNotExist() {
        // Act
        Optional<UserEntity> found = userRepository.findByUsername("unknown");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should enforce unique username constraint")
    void shouldEnforceUniqueUsernameConstraint() {
        // Arrange
        UserEntity user1 = new UserEntity();
        user1.setUsername("uniqueUser");
        userRepository.saveAndFlush(user1);

        UserEntity user2 = new UserEntity();
        user2.setUsername("uniqueUser");

        // Act & Assert
        assertThatThrownBy(() -> userRepository.saveAndFlush(user2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Should generate UUID as id on save")
    void shouldGenerateUUIDOnSave() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setUsername("bob");

        // Act
        UserEntity saved = userRepository.saveAndFlush(user);

        // Assert
        assertThat(saved.getId()).isNonnull();
    }
}
