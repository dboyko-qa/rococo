package qa.dboyko.rococo.unittests;

import com.dboyko.rococo.grpc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.mockito.junit.jupiter.MockitoExtension;

import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.service.grpc.ArtistGrpcClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistGrpcClientTest {

    @InjectMocks
    private ArtistGrpcClient artistGrpcClient;

    @Mock
    private ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    private Artist artistProto;
    private ArtistJson artistJson;

    @BeforeEach
    void setUp() {
        artistProto = Artist.newBuilder()
                .setId("123")
                .setName("Van Gogh")
                .setBiography("Dutch painter")
                .setPhoto("base64string")
                .build();
        artistJson = ArtistJson.fromGrpcMessage(artistProto);
    }

    @Test
    @DisplayName("Should return ArtistJson when getArtist is called with valid ID")
    void shouldReturnArtistWhenArtistExists() {
        // Arrange
        GetArtistResponse response = GetArtistResponse.newBuilder().setArtist(artistProto).build();
        when(artistStub.getArtist(any(GetArtistRequest.class))).thenReturn(response);

        // Act
        ArtistJson result = artistGrpcClient.getArtist("123");

        // Assert
        assertNonnull(result);
        assertEquals("123", result.id());
        assertEquals("Van Gogh", result.name());
        verify(artistStub).getArtist(any(GetArtistRequest.class));
    }

    @Test
    @DisplayName("Should return paged ArtistJson list when allArtists is called without filter")
    void shouldReturnPagedArtistsWithoutFilter() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        ArtistsResponse response = ArtistsResponse.newBuilder()
                .addAllArtists(List.of(artistProto))
                .setTotalElements(1)
                .build();
        when(artistStub.allArtists(any(AllArtistsRequest.class))).thenReturn(response);

        // Act
        Page<ArtistJson> result = artistGrpcClient.allArtists(pageable, null);

        // Assert
        assertNonnull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Van Gogh", result.getContent().get(0).name());
        verify(artistStub).allArtists(any(AllArtistsRequest.class));
    }

    @Test
    @DisplayName("Should return paged ArtistJson list when allArtists is called with name filter")
    void shouldReturnPagedArtistsWithNameFilter() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        ArtistsResponse response = ArtistsResponse.newBuilder()
                .addAllArtists(List.of(artistProto))
                .setTotalElements(1)
                .build();
        when(artistStub.allArtists(argThat(req -> req.getNameFilter().equals("Van")))).thenReturn(response);

        // Act
        Page<ArtistJson> result = artistGrpcClient.allArtists(pageable, "Van");

        // Assert
        assertNonnull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Van Gogh", result.getContent().get(0).name());
    }

    @Test
    @DisplayName("Should create ArtistJson successfully when createArtist is called")
    void shouldCreateArtistSuccessfully() {
        // Arrange
        CreateArtistRequest expectedRequest = CreateArtistRequest.newBuilder()
                .setName(artistJson.name())
                .setBiography(artistJson.biography())
                .setPhoto(artistJson.photo())
                .build();
        when(artistStub.createArtist(any(CreateArtistRequest.class)))
                .thenReturn(CreateArtistResponse.newBuilder().setArtist(artistProto).build());

        // Act
        ArtistJson result = artistGrpcClient.createArtist(artistJson);

        // Assert
        assertNonnull(result);
        assertEquals("Van Gogh", result.name());
        verify(artistStub).createArtist(any(CreateArtistRequest.class));
    }

    @Test
    @DisplayName("Should update ArtistJson successfully when updateArtist is called")
    void shouldUpdateArtistSuccessfully() {
        // Arrange
        UpdateArtistRequest expectedRequest = UpdateArtistRequest.newBuilder()
                .setArtist(artistJson.toGrpcMessage())
                .build();
        when(artistStub.updateArtist(any(UpdateArtistRequest.class)))
                .thenReturn(UpdateArtistResponse.newBuilder().setArtist(artistProto).build());

        // Act
        ArtistJson result = artistGrpcClient.updateArtist(artistJson);

        // Assert
        assertNonnull(result);
        assertEquals("Van Gogh", result.name());
        verify(artistStub).updateArtist(any(UpdateArtistRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when getArtist returns null")
    void shouldThrowExceptionWhenGetArtistFails() {
        // Arrange
        when(artistStub.getArtist(any(GetArtistRequest.class))).thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> artistGrpcClient.getArtist("123"));
        assertEquals("gRPC error", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when allArtists returns null")
    void shouldThrowExceptionWhenAllArtistsFails() {
        // Arrange
        PageRequest pageable = PageRequest.of(0, 10);
        when(artistStub.allArtists(any(AllArtistsRequest.class))).thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> artistGrpcClient.allArtists(pageable, null));
        assertEquals("gRPC error", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when createArtist fails")
    void shouldThrowExceptionWhenCreateArtistFails() {
        // Arrange
        when(artistStub.createArtist(any(CreateArtistRequest.class))).thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> artistGrpcClient.createArtist(artistJson));
        assertEquals("gRPC error", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updateArtist fails")
    void shouldThrowExceptionWhenUpdateArtistFails() {
        // Arrange
        when(artistStub.updateArtist(any(UpdateArtistRequest.class))).thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> artistGrpcClient.updateArtist(artistJson));
        assertEquals("gRPC error", ex.getMessage());
    }
}
