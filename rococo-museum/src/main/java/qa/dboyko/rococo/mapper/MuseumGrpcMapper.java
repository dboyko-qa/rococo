package qa.dboyko.rococo.mapper;

import com.dboyko.rococo.grpc.Museum;
import jakarta.annotation.Nonnull;
import qa.dboyko.rococo.entity.MuseumEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MuseumGrpcMapper {

    public static Museum toGrpcMuseum(MuseumEntity entity) {
        if (entity == null) {
            return Museum.getDefaultInstance();
        }

        Museum.Builder grpc = Museum.newBuilder();

        if (entity.getId() == null) {
            throw new IllegalStateException("Museum id must not be null");
        }
        grpc.setId(entity.getId().toString());

        if (isBlank(entity.getTitle())) {
            throw new IllegalStateException("Museum title must not be null or empty");
        }
        grpc.setTitle(entity.getTitle());

        // необязательные
        if (notBlank(entity.getDescription())) {
            grpc.setDescription(entity.getDescription());
        }

        if (notBlank(entity.getCity())) {
            grpc.setCity(entity.getCity());
        }

        if (entity.getCountryId() != null) {
            grpc.setCountryId(entity.getCountryId().toString());
        }

        if (entity.getPhoto() != null && entity.getPhoto().length > 0) {
            grpc.setPhoto(new String(entity.getPhoto(), StandardCharsets.UTF_8));
        }

        return grpc.build();
    }

    public static MuseumEntity fromGrpcMuseum(@Nonnull Museum grpc) {
        MuseumEntity entity = new MuseumEntity();

        // обязательные
        if (grpc.getId().isBlank()) {
            throw new IllegalArgumentException("Museum id is required");
        }
        entity.setId(UUID.fromString(grpc.getId()));

        if (grpc.getTitle().isBlank()) {
            throw new IllegalArgumentException("Museum title is required");
        }
        entity.setTitle(grpc.getTitle());

        // необязательные
        if (!grpc.getDescription().isBlank()) {
            entity.setDescription(grpc.getDescription());
        }

        if (!grpc.getCity().isBlank()) {
            entity.setCity(grpc.getCity());
        }

        if (!grpc.getCountryId().isBlank()) {
            entity.setCountryId(UUID.fromString(grpc.getCountryId()));
        }

        if (!grpc.getPhoto().isEmpty()) {
            entity.setPhoto(grpc.getPhoto().getBytes(StandardCharsets.UTF_8));
        } else {
            entity.setPhoto(null);
        }

        return entity;
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
