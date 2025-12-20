package qa.dboyko.rococo.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import qa.dboyko.rococo.entity.PaintingEntity;

import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {

    @Nonnull
    Page<PaintingEntity> findAllByTitleContainsIgnoreCase(
            @Nonnull String title,
            @Nonnull Pageable pageable
    );

    @Nonnull
    Page<PaintingEntity> findAllByArtistId(
            @Nonnull UUID artistId,
            @Nonnull Pageable pageable
    );
}
