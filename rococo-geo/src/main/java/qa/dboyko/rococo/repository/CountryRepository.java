package qa.dboyko.rococo.repository;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import qa.dboyko.rococo.entity.CountryEntity;

import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

    Optional<CountryEntity> findByName(@Nonnull String name);

}
