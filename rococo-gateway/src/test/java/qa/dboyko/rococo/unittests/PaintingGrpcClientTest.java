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
import qa.dboyko.rococo.model.*;
import qa.dboyko.rococo.service.ArtistClient;
import qa.dboyko.rococo.service.MuseumClient;
import qa.dboyko.rococo.service.grpc.PaintingGrpcClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaintingGrpcClientTest {

    @Mock
    private PaintingServiceGrpc.PaintingServiceBlockingStub paintingStub;

    @Mock
    private MuseumClient museumClient;

    @Mock
    private ArtistClient artistClient;

    @InjectMocks
    private PaintingGrpcClient paintingGrpcClient;

    // -----------------------
    // getPainting
    // -----------------------

    @Test
    @DisplayName("Should return painting with museum and artist when all services are available")
    void shouldReturnPaintingWhenAllServicesAvailable() {
        // arrange
        Painting grpcPainting = grpcPainting();

        when(paintingStub.getPainting(any()))
                .thenReturn(GetPaintingResponse.newBuilder()
                        .setPainting(grpcPainting)
                        .build());

        when(museumClient.getMuseum("museum-id"))
                .thenReturn(museum());

        when(artistClient.getArtist("artist-id"))
                .thenReturn(artist());

        // act
        PaintingJson result = paintingGrpcClient.getPainting("painting-id");

        // assert
        assertThat(result.id()).isEqualTo("painting-id");
        assertThat(result.museum().id()).isEqualTo("museum-id");
        assertThat(result.artist().id()).isEqualTo("artist-id");
    }

    @Test
    @DisplayName("Should return painting with empty museum when museum service is unavailable")
    void shouldReturnPaintingWithEmptyMuseumWhenMuseumServiceFails() {
        // arrange
        Painting grpcPainting = grpcPainting();

        when(paintingStub.getPainting(any()))
                .thenReturn(GetPaintingResponse.newBuilder()
                        .setPainting(grpcPainting)
                        .build());

        when(museumClient.getMuseum("museum-id"))
                .thenThrow(new RuntimeException("Museum service unavailable"));

        when(artistClient.getArtist("artist-id"))
                .thenReturn(artist());

        // act
        PaintingJson result = paintingGrpcClient.getPainting("painting-id");

        // assert
        assertThat(result.museum().id()).isEqualTo("");
        assertThat(result.artist().id()).isEqualTo("artist-id");
    }

    @Test
    @DisplayName("Should return painting with empty artist when artist service is unavailable")
    void shouldReturnPaintingWithEmptyArtistWhenArtistServiceFails() {
        // arrange
        Painting grpcPainting = grpcPainting();

        when(paintingStub.getPainting(any()))
                .thenReturn(GetPaintingResponse.newBuilder()
                        .setPainting(grpcPainting)
                        .build());

        when(museumClient.getMuseum("museum-id"))
                .thenReturn(museum());

        when(artistClient.getArtist("artist-id"))
                .thenThrow(new RuntimeException("Artist service unavailable"));

        // act
        PaintingJson result = paintingGrpcClient.getPainting("painting-id");

        // assert
        assertThat(result.artist().id()).isEqualTo("");
        assertThat(result.museum().id()).isEqualTo("museum-id");
    }

    @Test
    @DisplayName("Should return painting with empty museum and artist when both services are unavailable")
    void shouldReturnPaintingWithEmptyMuseumAndArtistWhenBothServicesFail() {
        // arrange
        Painting grpcPainting = grpcPainting();

        when(paintingStub.getPainting(any()))
                .thenReturn(GetPaintingResponse.newBuilder()
                        .setPainting(grpcPainting)
                        .build());

        when(museumClient.getMuseum(any()))
                .thenThrow(new RuntimeException("Museum service unavailable"));

        when(artistClient.getArtist(any()))
                .thenThrow(new RuntimeException("Artist service unavailable"));

        // act
        PaintingJson result = paintingGrpcClient.getPainting("painting-id");

        // assert
        assertThat(result.museum().id()).isEqualTo("");
        assertThat(result.artist().id()).isEqualTo("");
    }

    // -----------------------
    // createPainting
    // -----------------------

    @Test
    @DisplayName("Should create painting and return mapped PaintingJson")
    void shouldCreatePaintingSuccessfully() {
        // arrange
        Painting grpcPainting = grpcPainting();

        when(paintingStub.createPainting(any()))
                .thenReturn(CreatePaintingResponse.newBuilder()
                        .setPainting(grpcPainting)
                        .build());

        when(museumClient.getMuseum("museum-id")).thenReturn(museum());
        when(artistClient.getArtist("artist-id")).thenReturn(artist());

        // act
        PaintingJson result = paintingGrpcClient.createPainting(validPaintingJson());

        // assert
        assertThat(result.title()).isEqualTo("Mona Lisa");
        assertThat(result.museum().title()).isEqualTo("Louvre");
    }

    // -----------------------
    // allPaintings
    // -----------------------

    @Test
    @DisplayName("Should return page of paintings")
    void shouldReturnAllPaintings() {
        // arrange
        when(paintingStub.allPaintings(any()))
                .thenReturn(PaintingsResponse.newBuilder()
                        .addPaintings(grpcPainting())
                        .setTotalElements(1)
                        .build());

        when(museumClient.getMuseum(any())).thenReturn(museum());
        when(artistClient.getArtist(any())).thenReturn(artist());

        // act
        Page<PaintingJson> result =
                paintingGrpcClient.allPaintings(PageRequest.of(0, 10), null);

        // assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // -----------------------
    // Test data
    // -----------------------

    private Painting grpcPainting() {
        return Painting.newBuilder()
                .setId("painting-id")
                .setTitle("Mona Lisa")
                .setDescription("Famous painting")
                .setContent("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA")
                .setMuseumId("museum-id")
                .setArtistId("artist-id")
                .build();
    }

    private MuseumJson museum() {
        return new MuseumJson(
                "museum-id",
                "Louvre",
                "museum in Paris",
                null,
                new GeoJson("Paris",
                        new CountryJson("1", "France"))
        );
    }

    private ArtistJson artist() {
        return new ArtistJson(
                "artist-id",
                "Leonardo da Vinci",
                "Famous artist",
                null
        );
    }

    private PaintingJson validPaintingJson() {
        return new PaintingJson(
                null,
                "Mona Lisa",
                "Famous painting",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA",
                museum(),
                artist()
        );
    }
}
