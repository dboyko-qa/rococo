package qa.dboyko.rococo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "artist")
public class ArtistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, length = 255)
    private String name;

    @Column(name = "biography", length = 2000)
    private String biography;

    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;

}
