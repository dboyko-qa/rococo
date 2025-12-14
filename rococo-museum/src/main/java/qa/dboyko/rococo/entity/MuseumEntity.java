package qa.dboyko.rococo.entity;

import com.dboyko.rococo.grpc.Museum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"museum\"")
public class MuseumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(name = "title", unique = true, nullable = false, length = 255)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;

    @Column(name = "city")
    private String city;

    @Column(name = "country_id")
    private UUID country_id;

    public Museum toGrpcMuseum() {
        Museum.Builder grpcMuseumBuilder = Museum.newBuilder();
        grpcMuseumBuilder.setId(this.getId().toString());
        grpcMuseumBuilder.setTitle(this.getTitle());
        if (!this.description.isEmpty()) {
            grpcMuseumBuilder.setDescription(this.getDescription());
        }
        if (!this.city.isEmpty()) {
            grpcMuseumBuilder.setCity(this.getCity());
        }        
        if (this.country_id != null) {
            grpcMuseumBuilder.setCountryId(this.getCountry_id().toString());
        }
        if (this.photo != null && this.photo.length > 0) {
            grpcMuseumBuilder.setPhoto(new String(this.photo, StandardCharsets.UTF_8));
        }
        return grpcMuseumBuilder.build();
    }

    public static MuseumEntity fromGrpcMuseum(@Nonnull Museum grpcMuseum) {
        MuseumEntity museumEntity = new MuseumEntity();
        museumEntity.setId(UUID.fromString(grpcMuseum.getId()));
        museumEntity.setTitle(grpcMuseum.getTitle());
        museumEntity.setDescription(grpcMuseum.getDescription());
        museumEntity.setCity(grpcMuseum.getCity());
        museumEntity.setCountry_id(UUID.fromString(grpcMuseum.getCountryId()));
        museumEntity.setPhoto(grpcMuseum.getPhoto().getBytes(StandardCharsets.UTF_8));
        return museumEntity;
    }

}
