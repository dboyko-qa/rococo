package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.entity.MuseumEntity;
import qa.dboyko.rococo.ex.MuseumNotFoundException;
import qa.dboyko.rococo.repository.MuseumRepository;
import qa.dboyko.rococo.service.MuseumService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MuseumServiceTest {

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private StreamObserver<GetMuseumResponse> getObserver;

    @Mock
    private StreamObserver<MuseumsResponse> allObserver;

    @Mock
    private StreamObserver<CreateMuseumResponse> createObserver;

    @Mock
    private StreamObserver<UpdateMuseumResponse> updateObserver;

    @InjectMocks
    private MuseumService museumService;


    // -----------------------------------------------
    // getMuseum
    // -----------------------------------------------
    @Test
    @DisplayName("getMuseum returns museum when found")
    void getMuseumReturnsMuseum() {
        // Arrange
        UUID id = UUID.randomUUID();
        MuseumEntity entity = new MuseumEntity();
        entity.setId(id);
        entity.setTitle("Hermitage");
        entity.setDescription("Great museum");
        entity.setCity("SPB");
        entity.setCountryId(UUID.randomUUID());
        entity.setPhoto("photo".getBytes(StandardCharsets.UTF_8));

        when(museumRepository.findById(id)).thenReturn(Optional.of(entity));

        GetMuseumRequest request = GetMuseumRequest.newBuilder()
                .setId(id.toString())
                .build();

        // Act
        museumService.getMuseum(request, getObserver);

        // Assert
        ArgumentCaptor<GetMuseumResponse> captor = ArgumentCaptor.forClass(GetMuseumResponse.class);
        verify(getObserver).onNext(captor.capture());
        verify(getObserver).onCompleted();

        assertThat(captor.getValue().getMuseum().getTitle()).isEqualTo("Hermitage");
    }

    @Test
    @DisplayName("getMuseum throws MuseumNotFoundException when museum not found")
    void getMuseumThrowsNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(museumRepository.findById(id)).thenReturn(Optional.empty());

        GetMuseumRequest request = GetMuseumRequest.newBuilder()
                .setId(id.toString())
                .build();

        // Act / Assert
        assertThrows(MuseumNotFoundException.class,
                () -> museumService.getMuseum(request, getObserver));
    }

    @Test
    @DisplayName("getMuseum throws IllegalArgumentException on invalid UUID")
    void getMuseumThrowsOnInvalidUUID() {
        // Arrange
        GetMuseumRequest request = GetMuseumRequest.newBuilder()
                .setId("NOT-A-UUID")
                .build();

        // Act / Assert
        assertThrows(IllegalArgumentException.class,
                () -> museumService.getMuseum(request, getObserver));
    }


    // -----------------------------------------------
    // allMuseums
    // -----------------------------------------------
    @Test
    @DisplayName("allMuseums returns all museums without filter")
    void allMuseumsReturnsAll() {
        // Arrange
        MuseumEntity e = new MuseumEntity();
        e.setId(UUID.randomUUID());
        e.setTitle("Louvre");

        Page<MuseumEntity> page =
                new PageImpl<>(List.of(e), PageRequest.of(0, 10), 1);

        when(museumRepository.findAll(any(Pageable.class))).thenReturn(page);

        AllMuseumsRequest request = AllMuseumsRequest.newBuilder()
                .setPageInfo(PageInfo.newBuilder().setPage(0).setSize(10))
                .build();

        // Act
        museumService.allMuseums(request, allObserver);

        // Assert
        ArgumentCaptor<MuseumsResponse> captor = ArgumentCaptor.forClass(MuseumsResponse.class);
        verify(allObserver).onNext(captor.capture());
        verify(allObserver).onCompleted();

        assertThat(captor.getValue().getMuseumsCount()).isEqualTo(1);
        assertThat(captor.getValue().getMuseums(0).getTitle()).isEqualTo("Louvre");
        assertThat(captor.getValue().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("allMuseums applies nameFilter when provided")
    void allMuseumsUsesNameFilter() {
        // Arrange
        MuseumEntity e = new MuseumEntity();
        e.setId(UUID.randomUUID());
        e.setTitle("Hermitage");

        Page<MuseumEntity> page = new PageImpl<>(List.of(e));

        when(museumRepository.findAllByTitleContainsIgnoreCase(eq("Her"), any()))
                .thenReturn(page);

        AllMuseumsRequest request = AllMuseumsRequest.newBuilder()
                .setPageInfo(PageInfo.newBuilder().setPage(0).setSize(10))
                .setNameFilter("Her")
                .build();

        // Act
        museumService.allMuseums(request, allObserver);

        // Assert
        ArgumentCaptor<MuseumsResponse> captor = ArgumentCaptor.forClass(MuseumsResponse.class);
        verify(allObserver).onNext(captor.capture());
        verify(allObserver).onCompleted();

        assertThat(captor.getValue().getMuseumsCount()).isEqualTo(1);
        assertThat(captor.getValue().getMuseums(0).getTitle()).isEqualTo("Hermitage");
    }

    @Test
    @DisplayName("allMuseums returns empty list when no museums exist")
    void allMuseumsReturnsEmpty() {
        // Arrange
        Page<MuseumEntity> page = new PageImpl<>(List.of());
        when(museumRepository.findAll(any(Pageable.class))).thenReturn(page);

        AllMuseumsRequest request = AllMuseumsRequest.newBuilder()
                .setPageInfo(PageInfo.newBuilder().setPage(0).setSize(5))
                .build();

        // Act
        museumService.allMuseums(request, allObserver);

        // Assert
        ArgumentCaptor<MuseumsResponse> captor = ArgumentCaptor.forClass(MuseumsResponse.class);
        verify(allObserver).onNext(captor.capture());
        verify(allObserver).onCompleted();

        assertThat(captor.getValue().getMuseumsCount()).isZero();
    }


    // -----------------------------------------------
    // createMuseum
    // -----------------------------------------------
    @Test
    @DisplayName("createMuseum successfully saves museum")
    void createMuseumCreatesMuseum() {
        // Arrange
        UUID country = UUID.randomUUID();

        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle("Van Gogh")
                .setDescription("desc")
                .setCity("AMS")
                .setCountryId(country.toString())
                .setPhoto("photo")
                .build();

        MuseumEntity saved = new MuseumEntity();
        saved.setId(UUID.randomUUID());
        saved.setTitle("Van Gogh");

        when(museumRepository.save(any())).thenReturn(saved);

        // Act
        museumService.createMuseum(request, createObserver);

        // Assert
        ArgumentCaptor<CreateMuseumResponse> captor = ArgumentCaptor.forClass(CreateMuseumResponse.class);
        verify(createObserver).onNext(captor.capture());
        verify(createObserver).onCompleted();

        assertThat(captor.getValue().getMuseum().getTitle()).isEqualTo("Van Gogh");
    }

    @Test
    @DisplayName("createMuseum correctly handles empty photo")
    void createMuseumHandlesEmptyPhoto() {
        // Arrange
        UUID country = UUID.randomUUID();

        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle("Empty")
                .setDescription("desc")
                .setCity("NY")
                .setCountryId(country.toString())
                .setPhoto("")   // important case
                .build();

        MuseumEntity saved = new MuseumEntity();
        saved.setId(UUID.randomUUID());
        saved.setTitle("Empty");

        when(museumRepository.save(any())).thenReturn(saved);

        // Act
        museumService.createMuseum(request, createObserver);

        // Assert
        verify(createObserver).onNext(any());
        verify(createObserver).onCompleted();
    }

    @Test
    @DisplayName("createMuseum throws IllegalArgumentException on invalid countryId UUID")
    void createMuseumBadUUID() {
        // Arrange
        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle("Bad")
                .setDescription("bad")
                .setCity("NY")
                .setCountryId("WRONG-UUID")
                .build();

        // Act / Assert
        assertThrows(IllegalArgumentException.class,
                () -> museumService.createMuseum(request, createObserver));
    }


    // -----------------------------------------------
    // updateMuseum
    // -----------------------------------------------
    @Test
    @DisplayName("updateMuseum updates museum when found")
    void updateMuseumUpdates() {
        // Arrange
        UUID id = UUID.randomUUID();

        MuseumEntity existing = new MuseumEntity();
        existing.setId(id);

        when(museumRepository.findById(id)).thenReturn(Optional.of(existing));

        MuseumEntity updated = new MuseumEntity();
        updated.setId(id);
        updated.setTitle("Updated");

        when(museumRepository.save(any())).thenReturn(updated);

        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setMuseum(
                        Museum.newBuilder()
                                .setId(id.toString())
                                .setTitle("Updated")
                )
                .build();

        // Act
        museumService.updateMuseum(request, updateObserver);

        // Assert
        ArgumentCaptor<UpdateMuseumResponse> captor = ArgumentCaptor.forClass(UpdateMuseumResponse.class);
        verify(updateObserver).onNext(captor.capture());
        verify(updateObserver).onCompleted();

        assertThat(captor.getValue().getMuseum().getTitle()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("updateMuseum throws MuseumNotFoundException when museum not found")
    void updateMuseumNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();

        when(museumRepository.findById(id)).thenReturn(Optional.empty());

        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setMuseum(
                        Museum.newBuilder()
                                .setId(id.toString())
                                .setTitle("AAA")
                )
                .build();

        // Act / Assert
        assertThrows(MuseumNotFoundException.class,
                () -> museumService.updateMuseum(request, updateObserver));
    }

    @Test
    @DisplayName("updateMuseum throws IllegalArgumentException on invalid UUID")
    void updateMuseumInvalidUUID() {
        // Arrange
        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setMuseum(
                        Museum.newBuilder()
                                .setId("BAD-UUID")
                                .setTitle("AAA")
                )
                .build();

        // Act / Assert
        assertThrows(IllegalArgumentException.class,
                () -> museumService.updateMuseum(request, updateObserver));
    }

    @Test
    @DisplayName("updateMuseum propagates repository exception")
    void updateMuseumRepositoryException() {
        // Arrange
        UUID id = UUID.randomUUID();

        MuseumEntity existing = new MuseumEntity();
        existing.setId(id);

        when(museumRepository.findById(id)).thenReturn(Optional.of(existing));
        when(museumRepository.save(any())).thenThrow(new RuntimeException("DB failure"));

        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setMuseum(Museum.newBuilder().setId(id.toString()).setTitle("AAA"))
                .build();

        // Act / Assert
        assertThrows(RuntimeException.class,
                () -> museumService.updateMuseum(request, updateObserver));
    }
}
