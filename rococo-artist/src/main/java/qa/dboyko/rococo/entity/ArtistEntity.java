package qa.dboyko.rococo.entity;

import com.dboyko.rococo.grpc.Artist;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"artist\"")
public class ArtistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "biography")
    private String biography;

    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;

    public Artist toGrpcArtist() {
        Artist.Builder grpcArtistBuilder = Artist.newBuilder();
        grpcArtistBuilder.setId(this.getId().toString());
        grpcArtistBuilder.setName(this.getName());
        if (!this.biography.isEmpty()) {
            grpcArtistBuilder.setBiography(this.getBiography());
        }
        if (this.photo != null && this.photo.length > 0) {
            grpcArtistBuilder.setPhoto(new String(this.photo, StandardCharsets.UTF_8));
        }
        return grpcArtistBuilder.build();
    }

    public static ArtistEntity fromGrpcArtist(@Nonnull Artist grpcArtist) {
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setId(UUID.fromString(grpcArtist.getId()));
        artistEntity.setName(grpcArtist.getName());
        artistEntity.setBiography(grpcArtist.getBiography());
        artistEntity.setPhoto(grpcArtist.getPhoto().getBytes(StandardCharsets.UTF_8));
        return artistEntity;
    }

}
