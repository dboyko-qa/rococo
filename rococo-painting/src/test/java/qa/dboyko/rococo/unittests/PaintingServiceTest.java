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
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.entity.PaintingEntity;
import qa.dboyko.rococo.ex.PaintingNotFoundException;
import qa.dboyko.rococo.repository.PaintingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import qa.dboyko.rococo.service.PaintingService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for PaintingService")
class PaintingServiceTest {

    @Mock
    private PaintingRepository paintingRepository;

    @InjectMocks
    private PaintingService paintingService;

    @Test
    @DisplayName("should return painting when getPainting is called with existing id")
    void shouldReturnPaintingOnGetPainting() {
        String id = UUID.randomUUID().toString();
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setId(UUID.fromString(id));
        paintingEntity.setTitle("Mona Lisa");
        paintingEntity.setDescription("Famous painting");
        paintingEntity.setContent("content".getBytes(StandardCharsets.UTF_8));

        when(paintingRepository.findById(UUID.fromString(id))).thenReturn(Optional.of(paintingEntity));

        @SuppressWarnings("unchecked")
        StreamObserver<GetPaintingResponse> observer = mock(StreamObserver.class);

        GetPaintingRequest request = GetPaintingRequest.newBuilder().setId(id).build();
        paintingService.getPainting(request, observer);

        ArgumentCaptor<GetPaintingResponse> captor = ArgumentCaptor.forClass(GetPaintingResponse.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        GetPaintingResponse response = captor.getValue();
        assertEquals("Mona Lisa", response.getPainting().getTitle());
        assertEquals("Famous painting", response.getPainting().getDescription());
    }

    @Test
    @DisplayName("should throw PaintingNotFoundException when getPainting called with non-existing id")
    void shouldThrowExceptionOnGetPaintingNotFound() {
        String id = UUID.randomUUID().toString();
        when(paintingRepository.findById(UUID.fromString(id))).thenReturn(Optional.empty());

        @SuppressWarnings("unchecked")
        StreamObserver<GetPaintingResponse> observer = mock(StreamObserver.class);

        GetPaintingRequest request = GetPaintingRequest.newBuilder().setId(id).build();
        PaintingNotFoundException ex = assertThrows(PaintingNotFoundException.class, () ->
                paintingService.getPainting(request, observer));
        assertTrue(ex.getMessage().contains(id));
    }

    @Test
    @DisplayName("should return all paintings when allPaintings is called")
    void shouldReturnAllPaintings() {
        PaintingEntity painting = new PaintingEntity();
        painting.setId(UUID.randomUUID());
        painting.setTitle("Starry Night");

        Page<PaintingEntity> page = new PageImpl<>(List.of(painting));
        when(paintingRepository.findAll(any(Pageable.class))).thenReturn(page);

        @SuppressWarnings("unchecked")
        StreamObserver<PaintingsResponse> observer = mock(StreamObserver.class);

        AllPaintingsRequest request = AllPaintingsRequest.newBuilder()
                .setPageInfo(PageInfo.newBuilder().setPage(0).setSize(10).build())
                .build();

        paintingService.allPaintings(request, observer);

        ArgumentCaptor<PaintingsResponse> captor = ArgumentCaptor.forClass(PaintingsResponse.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        PaintingsResponse response = captor.getValue();
        assertEquals(1, response.getPaintingsCount());
        assertEquals("Starry Night", response.getPaintings(0).getTitle());
    }

    @Test
    @DisplayName("should return paintings for specific artist when getPaintingsForArtist is called")
    void shouldReturnPaintingsForArtist() {
        UUID artistId = UUID.randomUUID();
        PaintingEntity painting = new PaintingEntity();
        painting.setId(UUID.randomUUID());
        painting.setTitle("The Scream");
        painting.setArtistId(artistId);

        Page<PaintingEntity> page = new PageImpl<>(List.of(painting));
        when(paintingRepository.findAllByArtistId(eq(artistId), any())).thenReturn(page);

        @SuppressWarnings("unchecked")
        StreamObserver<PaintingsResponse> observer = mock(StreamObserver.class);

        GetPaintingsForArtistRequest request = GetPaintingsForArtistRequest.newBuilder()
                .setArtistId(artistId.toString())
                .setPageInfo(PageInfo.newBuilder().setPage(0).setSize(10).build())
                .build();

        paintingService.getPaintingsForArtist(request, observer);

        ArgumentCaptor<PaintingsResponse> captor = ArgumentCaptor.forClass(PaintingsResponse.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        PaintingsResponse response = captor.getValue();
        assertEquals(1, response.getPaintingsCount());
        assertEquals("The Scream", response.getPaintings(0).getTitle());
    }

    @Test
    @DisplayName("should create new painting when createPainting is called")
    void shouldCreatePainting() {
        PaintingEntity savedPainting = new PaintingEntity();
        savedPainting.setId(UUID.randomUUID());
        savedPainting.setTitle("Guernica");
        savedPainting.setDescription("Description");

        when(paintingRepository.save(any(PaintingEntity.class))).thenReturn(savedPainting);

        @SuppressWarnings("unchecked")
        StreamObserver<CreatePaintingResponse> observer = mock(StreamObserver.class);

        CreatePaintingRequest request = CreatePaintingRequest.newBuilder()
                .setTitle("Guernica")
                .setDescription("Description")
                .setContent("data")
                .setArtistId(UUID.randomUUID().toString())
                .setMuseumId(UUID.randomUUID().toString())
                .build();

        paintingService.createPainting(request, observer);

        ArgumentCaptor<CreatePaintingResponse> captor = ArgumentCaptor.forClass(CreatePaintingResponse.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        CreatePaintingResponse response = captor.getValue();
        assertEquals("Guernica", response.getPainting().getTitle());
    }

    @Test
    @DisplayName("should update existing painting when updatePainting is called")
    void shouldUpdatePainting() {
        UUID paintingId = UUID.randomUUID();
        PaintingEntity existing = new PaintingEntity();
        existing.setId(paintingId);

        PaintingEntity updatedEntity = new PaintingEntity();
        updatedEntity.setId(paintingId);
        updatedEntity.setTitle("Updated");

        when(paintingRepository.findById(paintingId)).thenReturn(Optional.of(existing));
        when(paintingRepository.save(any(PaintingEntity.class))).thenReturn(updatedEntity);

        @SuppressWarnings("unchecked")
        StreamObserver<UpdatePaintingResponse> observer = mock(StreamObserver.class);

        Painting grpcPainting = Painting.newBuilder()
                .setId(paintingId.toString())
                .setTitle("Updated")
                .build();

        UpdatePaintingRequest request = UpdatePaintingRequest.newBuilder()
                .setPainting(grpcPainting)
                .build();

        paintingService.updatePainting(request, observer);

        ArgumentCaptor<UpdatePaintingResponse> captor = ArgumentCaptor.forClass(UpdatePaintingResponse.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        UpdatePaintingResponse response = captor.getValue();
        assertEquals("Updated", response.getPainting().getTitle());
    }
}
