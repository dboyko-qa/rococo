package qa.dboyko.rococo.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import qa.dboyko.rococo.entity.ArtistEntity;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {

    @Nonnull
    Page<ArtistEntity> findAllByNameContainsIgnoreCase(
            @Nonnull String name,
            @Nonnull Pageable pageable
    );
}
