package qa.dboyko.rococo.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "painting")
public class PaintingEntity {

    @Id
    @Nonnull
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotBlank
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


}
