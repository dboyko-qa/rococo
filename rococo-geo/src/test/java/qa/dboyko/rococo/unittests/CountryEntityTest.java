package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.Country;
import org.junit.jupiter.api.Test;
import qa.dboyko.rococo.entity.CountryEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountryEntityTest {
    @Test
    void toGrpcCountryShouldConvertEntityToGrpc() {
        // Arrange
        CountryEntity entity = new CountryEntity();
        UUID id = UUID.randomUUID();
        entity.setId(id);
        entity.setName("Germany");

        // Act
        Country grpc = entity.toGrpcCountry();

        // Assert
        assertEquals(id.toString(), grpc.getId());
        assertEquals("Germany", grpc.getName());
    }
}
