package qa.dboyko.rococo.mapper;

import com.dboyko.rococo.grpc.Painting;
import qa.dboyko.rococo.entity.PaintingEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import jakarta.annotation.Nonnull;

public class PaintingGrpcMapper {

    public static Painting toGrpcPainting(PaintingEntity entity) {
        if (entity == null) {
            return Painting.getDefaultInstance();
        }

        Painting.Builder grpc = Painting.newBuilder();

        if (entity.getId() == null) {
            throw new IllegalStateException("Painting id must not be null");
        }
        grpc.setId(entity.getId().toString());

        if (isBlank(entity.getTitle())) {
            throw new IllegalStateException("Painting title must not be null or empty");
        }
        grpc.setTitle(entity.getTitle());

        if (notBlank(entity.getDescription())) {
            grpc.setDescription(entity.getDescription());
        }

        if (entity.getContent() != null && entity.getContent().length > 0) {
            grpc.setContent(new String(entity.getContent(), StandardCharsets.UTF_8));
        }

        if (entity.getArtistId() != null) {
            grpc.setArtistId(entity.getArtistId().toString());
        }

        if (entity.getMuseumId() != null) {
            grpc.setMuseumId(entity.getMuseumId().toString());
        }

        return grpc.build();
    }

    public static PaintingEntity fromGrpcPainting(@Nonnull Painting grpc) {
        PaintingEntity entity = new PaintingEntity();

        if (grpc.getId().isBlank()) {
            throw new IllegalArgumentException("Painting id is required");
        }
        entity.setId(UUID.fromString(grpc.getId()));

        if (grpc.getTitle().isBlank()) {
            throw new IllegalArgumentException("Painting title is required");
        }
        entity.setTitle(grpc.getTitle());

        if (notBlank(grpc.getDescription())) {
            entity.setDescription(grpc.getDescription());
        }

        if (!grpc.getContent().isEmpty()) {
            entity.setContent(grpc.getContent().getBytes(StandardCharsets.UTF_8));
        } else {
            entity.setContent(null);
        }

        if (!grpc.getArtistId().isBlank()) {
            try {
                entity.setArtistId(UUID.fromString(grpc.getArtistId()));
            } catch (IllegalArgumentException ignored) {
                // ignore
            }
        }

        if (!grpc.getMuseumId().isBlank()) {
            try {
                entity.setMuseumId(UUID.fromString(grpc.getMuseumId()));
            } catch (IllegalArgumentException ignored) {
                // ignore
            }
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
