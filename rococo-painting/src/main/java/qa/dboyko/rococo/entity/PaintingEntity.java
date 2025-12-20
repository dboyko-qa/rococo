package qa.dboyko.rococo.entity;

import com.dboyko.rococo.grpc.Painting;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"painting\"")
public class PaintingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(name = "title", unique = true, nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "content", columnDefinition = "bytea")
    private byte[] content;

    @Column(name = "artist_id")
    private UUID artistId;

    @Column(name = "museum_id")
    private UUID museumId;

    public static PaintingEntity fromGrpcPainting(Painting grpcPainting) {
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setId(UUID.fromString(grpcPainting.getId()));
        paintingEntity.setTitle(grpcPainting.getTitle());
        paintingEntity.setDescription(grpcPainting.getDescription());
        paintingEntity.setContent(grpcPainting.getContent().getBytes(StandardCharsets.UTF_8));
        paintingEntity.setArtistId(UUID.fromString(grpcPainting.getArtistId()));
        paintingEntity.setMuseumId(UUID.fromString(grpcPainting.getMuseumId()));
        return paintingEntity;
    }

    public Painting toGrpcPainting() {
        Painting.Builder grpcPaintingBuilder = Painting.newBuilder();
        grpcPaintingBuilder.setId(this.getId().toString());
        grpcPaintingBuilder.setTitle(this.getTitle());
        grpcPaintingBuilder.setDescription(this.getDescription());
        if (!this.description.isEmpty()) {
            grpcPaintingBuilder.setDescription(this.getDescription());
        }
        if (this.content != null && this.content.length > 0) {
            grpcPaintingBuilder.setContent(new String(this.content, StandardCharsets.UTF_8));
        }
        if (this.artistId != null) {
            grpcPaintingBuilder.setArtistId(this.artistId.toString());
        }
        if (this.museumId != null) {
            grpcPaintingBuilder.setMuseumId(this.museumId.toString());
        }
        return grpcPaintingBuilder.build();
    }

}
