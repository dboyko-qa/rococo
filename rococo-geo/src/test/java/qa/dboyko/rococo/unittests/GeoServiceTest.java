package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import qa.dboyko.rococo.entity.CountryEntity;
import qa.dboyko.rococo.ex.CountryNotFoundException;
import qa.dboyko.rococo.repository.CountryRepository;
import qa.dboyko.rococo.service.GeoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeoServiceTest {

    private CountryRepository repository;
    private GeoService service;

    @BeforeEach
    void setup() {
        repository = mock(CountryRepository.class);
        service = new GeoService(repository);
    }

    @Test
    void getCountryShouldReturnCountryWhenExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        CountryEntity entity = new CountryEntity();
        entity.setId(id);
        entity.setName("France");

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        GetCountryRequest request = GetCountryRequest.newBuilder()
                .setId(id.toString())
                .build();

        StreamObserver<GetCountryResponse> observer = mock(StreamObserver.class);

        // Act
        service.getCountry(request, observer);

        // Assert
        ArgumentCaptor<GetCountryResponse> captor = ArgumentCaptor.forClass(GetCountryResponse.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        GetCountryResponse response = captor.getValue();
        assertEquals(id.toString(), response.getCountry().getId());
        assertEquals("France", response.getCountry().getName());
    }

    @Test
    void getCountryShouldThrowExceptionWhenCountryNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        GetCountryRequest request = GetCountryRequest.newBuilder()
                .setId(id.toString())
                .build();

        StreamObserver<GetCountryResponse> observer = mock(StreamObserver.class);

        // Act + Assert
        assertThrows(
                CountryNotFoundException.class,
                () -> service.getCountry(request, observer)
        );

        verify(observer, never()).onNext(any());
        verify(observer, never()).onCompleted();
    }

    @Test
    void getAllCountriesShouldReturnPagedResult() {
        // Arrange
        List<CountryEntity> allCountries = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            CountryEntity entity = new CountryEntity();
            entity.setId(UUID.randomUUID());
            entity.setName("Country_" + i);
            allCountries.add(entity);
        }

        PageRequest pageable = PageRequest.of(0, 2);
        Page<CountryEntity> page = new PageImpl<>(
                allCountries.subList(0, 2),
                pageable,
                allCountries.size()
        );

        when(repository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(page);

        GetAllCountriesRequest request = GetAllCountriesRequest.newBuilder()
                .setPageInfo(
                        PageInfo.newBuilder()
                                .setPage(0)
                                .setSize(2)
                                .build()
                )
                .build();

        StreamObserver<GetAllCountriesResponse> observer = mock(StreamObserver.class);

        // Act
        service.getAllCountries(request, observer);

        // Assert
        ArgumentCaptor<GetAllCountriesResponse> captor =
                ArgumentCaptor.forClass(GetAllCountriesResponse.class);

        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        GetAllCountriesResponse response = captor.getValue();

        assertEquals(2, response.getCountriesCount());
        assertEquals(10, response.getTotalElements());
        assertEquals(5, response.getTotalPages());
    }

}
