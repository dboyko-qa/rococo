package qa.dboyko.rococo.mapper;

import com.dboyko.rococo.grpc.Artist;
import qa.dboyko.rococo.entity.ArtistEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class ArtistGrpcMapper {

    private ArtistGrpcMapper() {
        // utility class
    }

    public static Artist toGrpc(ArtistEntity entity) {
        if (entity == null) {
            return null;
        }

        Artist.Builder builder = Artist.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName());

        if (entity.getBiography() != null && !entity.getBiography().isBlank()) {
            builder.setBiography(entity.getBiography());
        }

        if (entity.getPhoto() != null && entity.getPhoto().length > 0) {
            builder.setPhoto(new String(entity.getPhoto(), StandardCharsets.UTF_8));
        }

        return builder.build();
    }

    public static ArtistEntity fromGrpc(Artist grpcArtist) {
        if (grpcArtist == null) {
            return null;
        }

        ArtistEntity entity = new ArtistEntity();

        entity.setId(UUID.fromString(grpcArtist.getId()));
        entity.setName(grpcArtist.getName());

        if (!grpcArtist.getBiography().isBlank()) {
            entity.setBiography(grpcArtist.getBiography());
        }

        if (!grpcArtist.getPhoto().isBlank()) {
            entity.setPhoto(
                    grpcArtist.getPhoto().getBytes(StandardCharsets.UTF_8)
            );
        }

        return entity;
    }
}
