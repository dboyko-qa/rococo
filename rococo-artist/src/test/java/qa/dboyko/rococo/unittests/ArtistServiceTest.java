package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.entity.ArtistEntity;
import qa.dboyko.rococo.ex.ArtistNotFoundException;
import qa.dboyko.rococo.repository.ArtistRepository;
import qa.dboyko.rococo.service.ArtistService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArtistService Unit Tests")
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private StreamObserver<GetArtistResponse> getArtistResponseObserver;

    @Mock
    private StreamObserver<ArtistsResponse> artistsResponseObserver;

    @Mock
    private StreamObserver<CreateArtistResponse> createArtistResponseObserver;

    @Mock
    private StreamObserver<UpdateArtistResponse> updateArtistResponseObserver;

    @InjectMocks
    private ArtistService artistService;

    @Captor
    private ArgumentCaptor<GetArtistResponse> getArtistResponseCaptor;

    @Captor
    private ArgumentCaptor<ArtistsResponse> artistsResponseCaptor;

    @Captor
    private ArgumentCaptor<CreateArtistResponse> createArtistResponseCaptor;

    @Captor
    private ArgumentCaptor<UpdateArtistResponse> updateArtistResponseCaptor;

    @Captor
    private ArgumentCaptor<ArtistEntity> artistEntityCaptor;

    private UUID testArtistId;
    private ArtistEntity testArtistEntity;

    @BeforeEach
    void setUp() {
        testArtistId = UUID.randomUUID();
        testArtistEntity = new ArtistEntity();
        testArtistEntity.setId(testArtistId);
        testArtistEntity.setName("Vincent van Gogh");
        testArtistEntity.setBiography("Dutch post-impressionist painter.");
        testArtistEntity.setPhoto("photo_data".getBytes(StandardCharsets.UTF_8));
    }

    // ========== getArtist Tests ==========

    @Test
    @DisplayName("Should successfully get artist by id")
    void shouldSuccessfullyGetArtistById() {
        // Arrange
        GetArtistRequest request = GetArtistRequest.newBuilder()
                .setId(testArtistId.toString())
                .build();

        when(artistRepository.findById(testArtistId))
                .thenReturn(Optional.of(testArtistEntity));

        // Act
        artistService.getArtist(request, getArtistResponseObserver);

        // Assert
        verify(artistRepository).findById(testArtistId);
        verify(getArtistResponseObserver).onNext(getArtistResponseCaptor.capture());
        verify(getArtistResponseObserver).onCompleted();
        verify(getArtistResponseObserver, never()).onError(any());

        GetArtistResponse response = getArtistResponseCaptor.getValue();
        assertThat(response.getArtist().getId()).isEqualTo(testArtistId.toString());
        assertThat(response.getArtist().getName()).isEqualTo("Vincent van Gogh");
        assertThat(response.getArtist().getBiography()).isEqualTo("Dutch post-impressionist painter.");
    }

    @Test
    @DisplayName("Should throw ArtistNotFoundException when artist not found")
    void shouldThrowExceptionWhenArtistNotFound() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        GetArtistRequest request = GetArtistRequest.newBuilder()
                .setId(nonExistentId)
                .build();

        when(artistRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> artistService.getArtist(request, getArtistResponseObserver))
                .isInstanceOf(ArtistNotFoundException.class);

        verify(artistRepository).findById(UUID.fromString(nonExistentId));
        verify(getArtistResponseObserver, never()).onNext(any());
        verify(getArtistResponseObserver, never()).onCompleted();
    }

    @Test
    @DisplayName("Should handle artist with null optional fields")
    void shouldHandleArtistWithNullOptionalFields() {
        // Arrange
        ArtistEntity minimalArtist = new ArtistEntity();
        minimalArtist.setId(testArtistId);
        minimalArtist.setName("Artist Name");

        GetArtistRequest request = GetArtistRequest.newBuilder()
                .setId(testArtistId.toString())
                .build();

        when(artistRepository.findById(testArtistId))
                .thenReturn(Optional.of(minimalArtist));

        // Act
        artistService.getArtist(request, getArtistResponseObserver);

        // Assert
        verify(getArtistResponseObserver).onNext(getArtistResponseCaptor.capture());

        GetArtistResponse response = getArtistResponseCaptor.getValue();
        assertThat(response.getArtist().getName()).isEqualTo("Artist Name");
        assertThat(response.getArtist().getBiography()).isEmpty();
        assertThat(response.getArtist().getPhoto()).isEmpty();
    }

    // ========== allArtists Tests ==========

    @Test
    @DisplayName("Should return all artists without filter")
    void shouldReturnAllArtistsWithoutFilter() {
        // Arrange
        List<ArtistEntity> artists = List.of(testArtistEntity, createAnotherArtist());
        Page<ArtistEntity> artistPage = new PageImpl<>(artists, Pageable.ofSize(10), 2);

        AllArtistsRequest request = AllArtistsRequest.newBuilder()
                .setPageInfo(PageInfo.newBuilder().setPage(0).setSize(10).build())
                .build();

        when(artistRepository.findAll(any(Pageable.class)))
                .thenReturn(artistPage);

        // Act
        artistService.allArtists(request, artistsResponseObserver);

        // Assert
        verify(artistRepository).findAll(any(Pageable.class));
        verify(artistRepository, never()).findAllByNameContainsIgnoreCase(anyString(), any(Pageable.class));
        verify(artistsResponseObserver).onNext(artistsResponseCaptor.capture());
        verify(artistsResponseObserver).onCompleted();

        ArtistsResponse response = artistsResponseCaptor.getValue();
        assertThat(response.getArtistsList()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return filtered artists by name")
    void shouldReturnFilteredArtistsByName() {
        // Arrange
        String nameFilter = "van Gogh";
        List<ArtistEntity> filteredArtists = List.of(testArtistEntity);
        Page<ArtistEntity> artistPage = new PageImpl<>(filteredArtists, Pageable.ofSize(10), 1);

        AllArtistsRequest request = AllArtistsRequest.newBuilder()
                .setPageInfo(PageInfo.newBuilder().setPage(0).setSize(10).build())
                .setNameFilter(nameFilter)
                .build();

        when(artistRepository.findAllByNameContainsIgnoreCase(eq(nameFilter), any(Pageable.class)))
                .thenReturn(artistPage);

        // Act
        artistService.allArtists(request, artistsResponseObserver);

        // Assert
        verify(artistRepository).findAllByNameContainsIgnoreCase(eq(nameFilter), any(Pageable.class));
        verify(artistRepository, never()).findAll(any(Pageable.class));
        verify(artistsResponseObserver).onNext(artistsResponseCaptor.capture());

        ArtistsResponse response = artistsResponseCaptor.getValue();
        assertThat(response.getArtistsList()).hasSize(1);
        assertThat(response.getArtistsList().get(0).getName()).contains("van Gogh");
    }

    @Test
    @DisplayName("Should return empty list when no artists found")
    void shouldReturnEmptyListWhenNoArtistsFound() {
        // Arrange
        Page<ArtistEntity> emptyPage = new PageImpl<>(Collections.emptyList(), Pageable.ofSize(10), 0);

        AllArtistsRequest request = AllArtistsRequest.newBuilder()
                .setPageInfo(PageInfo.newBuilder().setPage(0).setSize(10).build())
                .build();

        when(artistRepository.findAll(any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        artistService.allArtists(request, artistsResponseObserver);

        // Assert
        verify(artistsResponseObserver).onNext(artistsResponseCaptor.capture());

        ArtistsResponse response = artistsResponseCaptor.getValue();
        assertThat(response.getArtistsList()).isEmpty();
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getTotalPages()).isZero();
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void shouldHandlePaginationCorrectly() {
        // Arrange
        List<ArtistEntity> artists = List.of(testArtistEntity);
        Page<ArtistEntity> artistPage = new PageImpl<>(artists, Pageable.ofSize(5).withPage(1), 15);

        AllArtistsRequest request = AllArtistsRequest.newBuilder()
                .setPageInfo(PageInfo.newBuilder().setPage(1).setSize(5).build())
                .build();

        when(artistRepository.findAll(any(Pageable.class)))
                .thenReturn(artistPage);

        // Act
        artistService.allArtists(request, artistsResponseObserver);

        // Assert
        verify(artistsResponseObserver).onNext(artistsResponseCaptor.capture());

        ArtistsResponse response = artistsResponseCaptor.getValue();
        assertThat(response.getTotalElements()).isEqualTo(15);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getArtistsList()).hasSize(1);
    }

    // ========== createArtist Tests ==========

    @Test
    @DisplayName("Should successfully create artist with all fields")
    void shouldSuccessfullyCreateArtistWithAllFields() {
        // Arrange
        String name = "Pablo Picasso";
        String biography = "Spanish painter and sculptor.";
        String photo = "photo_content";

        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName(name)
                .setBiography(biography)
                .setPhoto(photo)
                .build();

        ArtistEntity savedArtist = new ArtistEntity();
        savedArtist.setId(UUID.randomUUID());
        savedArtist.setName(name);
        savedArtist.setBiography(biography);
        savedArtist.setPhoto(photo.getBytes(StandardCharsets.UTF_8));

        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(savedArtist);

        // Act
        artistService.createArtist(request, createArtistResponseObserver);

        // Assert
        verify(artistRepository).save(artistEntityCaptor.capture());
        verify(createArtistResponseObserver).onNext(createArtistResponseCaptor.capture());
        verify(createArtistResponseObserver).onCompleted();

        ArtistEntity capturedEntity = artistEntityCaptor.getValue();
        assertThat(capturedEntity.getName()).isEqualTo(name);
        assertThat(capturedEntity.getBiography()).isEqualTo(biography);
        assertThat(capturedEntity.getPhoto()).isEqualTo(photo.getBytes(StandardCharsets.UTF_8));

        CreateArtistResponse response = createArtistResponseCaptor.getValue();
        assertThat(response.getArtist().getName()).isEqualTo(name);
        assertThat(response.getArtist().getBiography()).isEqualTo(biography);
    }

    @Test
    @DisplayName("Should create artist with empty photo")
    void shouldCreateArtistWithEmptyPhoto() {
        // Arrange
        String name = "Claude Monet";
        String biography = "French impressionist painter.";

        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName(name)
                .setBiography(biography)
                .setPhoto("")
                .build();

        ArtistEntity savedArtist = new ArtistEntity();
        savedArtist.setId(UUID.randomUUID());
        savedArtist.setName(name);
        savedArtist.setBiography(biography);
        savedArtist.setPhoto(null);

        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(savedArtist);

        // Act
        artistService.createArtist(request, createArtistResponseObserver);

        // Assert
        verify(artistRepository).save(artistEntityCaptor.capture());

        ArtistEntity capturedEntity = artistEntityCaptor.getValue();
        assertThat(capturedEntity.getPhoto()).isNull();
    }

    @Test
    @DisplayName("Should create artist with required fields only")
    void shouldCreateArtistWithRequiredFieldsOnly() {
        // Arrange
        String name = "Leonardo da Vinci";

        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName(name)
                .build();

        ArtistEntity savedArtist = new ArtistEntity();
        savedArtist.setId(UUID.randomUUID());
        savedArtist.setName(name);

        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(savedArtist);

        // Act
        artistService.createArtist(request, createArtistResponseObserver);

        // Assert
        verify(artistRepository).save(artistEntityCaptor.capture());

        ArtistEntity capturedEntity = artistEntityCaptor.getValue();
        assertThat(capturedEntity.getName()).isEqualTo(name);
        assertThat(capturedEntity.getPhoto()).isNull();
    }

    @Test
    @DisplayName("Should handle large photo when creating artist")
    void shouldHandleLargePhotoWhenCreatingArtist() {
        // Arrange
        byte[] largePhotoBytes = new byte[1024 * 1024]; // 1MB
        String largePhoto = new String(largePhotoBytes, StandardCharsets.UTF_8);

        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName("Artist Name")
                .setPhoto(largePhoto)
                .build();

        ArtistEntity savedArtist = new ArtistEntity();
        savedArtist.setId(UUID.randomUUID());
        savedArtist.setName("Artist Name");
        savedArtist.setPhoto(largePhotoBytes);

        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(savedArtist);

        // Act
        artistService.createArtist(request, createArtistResponseObserver);

        // Assert
        verify(artistRepository).save(artistEntityCaptor.capture());

        ArtistEntity capturedEntity = artistEntityCaptor.getValue();
        assertThat(capturedEntity.getPhoto()).hasSize(1024 * 1024);
    }

    // ========== updateArtist Tests ==========

    @Test
    @DisplayName("Should successfully update existing artist")
    void shouldSuccessfullyUpdateExistingArtist() {
        // Arrange
        Artist updatedArtistData = Artist.newBuilder()
                .setId(testArtistId.toString())
                .setName("Updated Name")
                .setBiography("Updated Biography")
                .setPhoto("updated_photo")
                .build();

        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setArtist(updatedArtistData)
                .build();

        ArtistEntity updatedEntity = new ArtistEntity();
        updatedEntity.setId(testArtistId);
        updatedEntity.setName("Updated Name");
        updatedEntity.setBiography("Updated Biography");
        updatedEntity.setPhoto("updated_photo".getBytes(StandardCharsets.UTF_8));

        when(artistRepository.findById(testArtistId))
                .thenReturn(Optional.of(testArtistEntity));
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(updatedEntity);

        // Act
        artistService.updateArtist(request, updateArtistResponseObserver);

        // Assert
        verify(artistRepository).findById(testArtistId);
        verify(artistRepository).save(any(ArtistEntity.class));
        verify(updateArtistResponseObserver).onNext(updateArtistResponseCaptor.capture());
        verify(updateArtistResponseObserver).onCompleted();

        UpdateArtistResponse response = updateArtistResponseCaptor.getValue();
        assertThat(response.getArtist().getName()).isEqualTo("Updated Name");
        assertThat(response.getArtist().getBiography()).isEqualTo("Updated Biography");
    }

    @Test
    @DisplayName("Should throw ArtistNotFoundException when updating non-existent artist")
    void shouldThrowExceptionWhenUpdatingNonExistentArtist() {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        Artist artistData = Artist.newBuilder()
                .setId(nonExistentId)
                .setName("Name")
                .build();

        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setArtist(artistData)
                .build();

        when(artistRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> artistService.updateArtist(request, updateArtistResponseObserver))
                .isInstanceOf(ArtistNotFoundException.class);

        verify(artistRepository).findById(UUID.fromString(nonExistentId));
        verify(artistRepository, never()).save(any());
        verify(updateArtistResponseObserver, never()).onNext(any());
    }

    @Test
    @DisplayName("Should update artist with partial data")
    void shouldUpdateArtistWithPartialData() {
        // Arrange
        Artist partialUpdate = Artist.newBuilder()
                .setId(testArtistId.toString())
                .setName("Only Name Updated")
                .build();

        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setArtist(partialUpdate)
                .build();

        ArtistEntity updatedEntity = new ArtistEntity();
        updatedEntity.setId(testArtistId);
        updatedEntity.setName("Only Name Updated");

        when(artistRepository.findById(testArtistId))
                .thenReturn(Optional.of(testArtistEntity));
        when(artistRepository.save(any(ArtistEntity.class)))
                .thenReturn(updatedEntity);

        // Act
        artistService.updateArtist(request, updateArtistResponseObserver);

        // Assert
        verify(artistRepository).save(any(ArtistEntity.class));
        verify(updateArtistResponseObserver).onNext(updateArtistResponseCaptor.capture());

        UpdateArtistResponse response = updateArtistResponseCaptor.getValue();
        assertThat(response.getArtist().getName()).isEqualTo("Only Name Updated");
    }

    // ========== Helper Methods ==========

    private ArtistEntity createAnotherArtist() {
        ArtistEntity artist = new ArtistEntity();
        artist.setId(UUID.randomUUID());
        artist.setName("Claude Monet");
        artist.setBiography("French impressionist painter.");
        return artist;
    }
}