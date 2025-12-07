package qa.dboyko.rococo.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import qa.dboyko.rococo.entity.ArtistEntity;

import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {

    Optional<ArtistEntity> findByName(@Nonnull String name);

}
