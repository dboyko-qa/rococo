package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import qa.dboyko.rococo.model.CountryJson;
import qa.dboyko.rococo.service.grpc.GeoGrpcClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeoGrpcClientTest {

    @Mock
    private GeoServiceGrpc.GeoServiceBlockingStub geoStub;

    @InjectMocks
    private GeoGrpcClient geoClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("shouldReturnCountryWhenIdExists")
    void shouldReturnCountryWhenIdExists() {
        // Arrange
        String countryId = "123";
        Country grpcCountry = Country.newBuilder().setId(countryId).setName("Testland").build();
        GetCountryResponse response = GetCountryResponse.newBuilder().setCountry(grpcCountry).build();
        when(geoStub.getCountry(any())).thenReturn(response);

        // Act
        CountryJson result = geoClient.getCountry(countryId);

        // Assert
        assertNonnull(result);
        assertEquals(countryId, result.id());
        assertEquals("Testland", result.name());

        ArgumentCaptor<GetCountryRequest> captor = ArgumentCaptor.forClass(GetCountryRequest.class);
        verify(geoStub).getCountry(captor.capture());
        assertEquals(countryId, captor.getValue().getId());
    }

    @Test
    @DisplayName("shouldReturnPagedCountries")
    void shouldReturnPagedCountries() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        Country grpcCountry1 = Country.newBuilder().setId("1").setName("Country1").build();
        Country grpcCountry2 = Country.newBuilder().setId("2").setName("Country2").build();
        GetAllCountriesResponse response = GetAllCountriesResponse.newBuilder()
                .addCountries(grpcCountry1)
                .addCountries(grpcCountry2)
                .setTotalElements(2)
                .build();
        when(geoStub.getAllCountries(any())).thenReturn(response);

        // Act
        Page<CountryJson> page = geoClient.allCountries(pageable);

        // Assert
        assertNonnull(page);
        assertEquals(2, page.getContent().size());
        assertEquals("Country1", page.getContent().get(0).name());
        assertEquals("Country2", page.getContent().get(1).name());
        assertEquals(2, page.getTotalElements());
    }

    @Test
    @DisplayName("shouldReturnEmptyPageWhenNoCountries")
    void shouldReturnEmptyPageWhenNoCountries() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        GetAllCountriesResponse response = GetAllCountriesResponse.newBuilder()
                .setTotalElements(0)
                .build();
        when(geoStub.getAllCountries(any())).thenReturn(response);

        // Act
        Page<CountryJson> page = geoClient.allCountries(pageable);

        // Assert
        assertNonnull(page);
        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenGrpcFailsOnGetCountry")
    void shouldThrowExceptionWhenGrpcFailsOnGetCountry() {
        // Arrange
        when(geoStub.getCountry(any())).thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> geoClient.getCountry("123"));
        assertEquals("gRPC error", ex.getMessage());
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenGrpcFailsOnAllCountries")
    void shouldThrowExceptionWhenGrpcFailsOnAllCountries() {
        // Arrange
        when(geoStub.getAllCountries(any())).thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> geoClient.allCountries(PageRequest.of(0, 10)));
        assertEquals("gRPC error", ex.getMessage());
    }
}
