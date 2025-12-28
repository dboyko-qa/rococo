package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.model.CountryJson;
import qa.dboyko.rococo.model.GeoJson;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.service.GeoClient;
import qa.dboyko.rococo.service.grpc.MuseumGrpcClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MuseumGrpcClientTest {

    @Mock
    private MuseumServiceGrpc.MuseumServiceBlockingStub museumStub;

    @Mock
    private GeoClient geoClient;

    @InjectMocks
    private MuseumGrpcClient museumGrpcClient;

    @Test
    @DisplayName("Should return museum when museum exists")
    void shouldReturnMuseumWhenMuseumExists() {
        // Arrange
        Museum grpcMuseum = Museum.newBuilder()
                .setId("1")
                .setTitle("Louvre")
                .setDescription("Famous museum")
                .setCity("Paris")
                .setCountryId("FR")
                .build();

        GetMuseumResponse response = GetMuseumResponse.newBuilder()
                .setMuseum(grpcMuseum)
                .build();

        when(museumStub.getMuseum(any())).thenReturn(response);
        when(geoClient.getCountry("FR"))
                .thenReturn(new CountryJson("FR", "France"));

        // Act
        MuseumJson result = museumGrpcClient.getMuseum("1");

        // Assert
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.title()).isEqualTo("Louvre");
        assertThat(result.geo().city()).isEqualTo("Paris");
        assertThat(result.geo().country().name()).isEqualTo("France");
    }

    @Test
    @DisplayName("Should return museum with empty country when GeoClient fails")
    void shouldReturnMuseumWithEmptyCountryWhenGeoClientFails() {
        // Arrange
        Museum grpcMuseum = Museum.newBuilder()
                .setId("1")
                .setTitle("Louvre")
                .setDescription("Famous museum")
                .setPhoto("photo")
                .setCity("Paris")
                .setCountryId("FR")
                .build();

        when(museumStub.getMuseum(any(GetMuseumRequest.class)))
                .thenReturn(GetMuseumResponse.newBuilder()
                        .setMuseum(grpcMuseum)
                        .build());

        when(geoClient.getCountry("FR"))
                .thenThrow(new RuntimeException("Geo service unavailable"));

        // Act
        MuseumJson result = museumGrpcClient.getMuseum("1");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.title()).isEqualTo("Louvre");
        assertThat(result.geo()).isNotNull();
        assertThat(result.geo().city()).isEqualTo("Paris");

        // Country fallback assertions
        assertThat(result.geo().country()).isNotNull();
        assertThat(result.geo().country().id()).isEqualTo("");
        assertThat(result.geo().country().name()).isEmpty();
    }


    @Test
    @DisplayName("Should return paged list of museums")
    void shouldReturnPagedMuseums() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        Museum museum = Museum.newBuilder()
                .setId("1")
                .setTitle("Louvre")
                .setCity("Paris")
                .setCountryId("FR")
                .build();

        MuseumsResponse response = MuseumsResponse.newBuilder()
                .addMuseums(museum)
                .setTotalElements(1)
                .build();

        when(museumStub.allMuseums(any())).thenReturn(response);
        when(geoClient.getCountry("FR"))
                .thenReturn(new CountryJson("FR", "France"));

        // Act
        Page<MuseumJson> page = museumGrpcClient.allMuseums(pageable, null);

        // Assert
        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).title()).isEqualTo("Louvre");
    }

    @Test
    @DisplayName("Should apply title filter when requesting museums")
    void shouldApplyTitleFilterWhenProvided() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);

        when(museumStub.allMuseums(any()))
                .thenReturn(MuseumsResponse.newBuilder().build());

        // Act
        Page<MuseumJson> result = museumGrpcClient.allMuseums(pageable, "Louvre");

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should create museum successfully")
    void shouldCreateMuseumSuccessfully() {
        // Arrange
        MuseumJson input = new MuseumJson(
                null,
                "Louvre",
                "Description",
                "",
                new GeoJson("Paris", new CountryJson("FR", "France"))
        );

        Museum grpcMuseum = Museum.newBuilder()
                .setId("1")
                .setTitle("Louvre")
                .setDescription("Description")
                .setCity("Paris")
                .setCountryId("FR")
                .build();

        when(museumStub.createMuseum(any()))
                .thenReturn(CreateMuseumResponse.newBuilder().setMuseum(grpcMuseum).build());

        when(geoClient.getCountry("FR"))
                .thenReturn(new CountryJson("FR", "France"));

        // Act
        MuseumJson result = museumGrpcClient.createMuseum(input);

        // Assert
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.title()).isEqualTo("Louvre");
    }

    @Test
    @DisplayName("Should update museum successfully")
    void shouldUpdateMuseumSuccessfully() {
        // Arrange
        MuseumJson input = new MuseumJson(
                "1",
                "Updated Museum",
                "Updated description",
                "",
                new GeoJson("Paris", new CountryJson("FR", "France"))
        );

        Museum grpcMuseum = Museum.newBuilder()
                .setId("1")
                .setTitle("Updated Museum")
                .setDescription("Updated description")
                .setCity("Paris")
                .setCountryId("FR")
                .build();

        when(museumStub.updateMuseum(any()))
                .thenReturn(UpdateMuseumResponse.newBuilder().setMuseum(grpcMuseum).build());

        when(geoClient.getCountry("FR"))
                .thenReturn(new CountryJson("FR", "France"));

        // Act
        MuseumJson result = museumGrpcClient.updateMuseum(input);

        // Assert
        assertThat(result.title()).isEqualTo("Updated Museum");
    }
}

