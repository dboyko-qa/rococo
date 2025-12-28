package qa.dboyko.rococo.entity;

import com.dboyko.rococo.grpc.Country;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "country")
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public Country toGrpcCountry() {
        Country.Builder grpcCountryBuilder = Country.newBuilder();
        grpcCountryBuilder.setId(this.getId().toString());
        grpcCountryBuilder.setName(this.getName());
        return grpcCountryBuilder.build();
    }


}
