package qa.dboyko.rococo.service.grpc;

import com.dboyko.rococo.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.model.PaintingJson;
import qa.dboyko.rococo.service.ArtistClient;
import qa.dboyko.rococo.service.MuseumClient;
import qa.dboyko.rococo.service.PaintingClient;
import qa.dboyko.rococo.util.GrpcPagination;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static qa.dboyko.rococo.model.PaintingJson.fromGrpcMessage;

@Service
@ConditionalOnProperty(prefix = "rococo-painting", name = "client", havingValue = "grpc")
public class PaintingGrpcClient implements PaintingClient {

    @GrpcClient("grpcPaintingClient")
    private PaintingServiceGrpc.PaintingServiceBlockingStub paintingStub;

    @Autowired
    private MuseumClient museumClient;

    @Autowired
    private ArtistClient artistClient;

    @Override
    public PaintingJson getPainting(@Nonnull String id) {
        Painting painting = paintingStub.getPainting(GetPaintingRequest.newBuilder().setId(id).build()).getPainting();
        return fromGrpcMessage(
                painting,
                safeGetMuseum(painting.getMuseumId()),
                safeGetArtist(painting.getArtistId()));
    }

    @Nonnull
    @Override
    public Page<PaintingJson> allPaintings(@Nullable Pageable pageable, @Nullable String titleFilter) {
        AllPaintingsRequest allPaintingsRequest = AllPaintingsRequest.newBuilder()
                .setPageInfo(new GrpcPagination(pageable).pageInfo())
                .build();
        if (titleFilter != null && !titleFilter.isBlank()) {
            allPaintingsRequest = allPaintingsRequest.toBuilder().setTitleFilter(titleFilter).build();
        }
        final PaintingsResponse response = paintingStub.allPaintings(allPaintingsRequest);
        return new PageImpl<>(
                response.getPaintingsList().stream().map(
                                m -> fromGrpcMessage(m,
                                        safeGetMuseum(m.getMuseumId()),
                                        safeGetArtist(m.getArtistId())))
                        .toList(),
                pageable,
                response.getTotalElements()
        );

    }

    @Override
    public Page<PaintingJson> getPaintingsForArtist(@Nullable Pageable pageable, @Nonnull String artistId) {
        GetPaintingsForArtistRequest paintingsByArtist = GetPaintingsForArtistRequest.newBuilder()
                .setPageInfo(new GrpcPagination(pageable).pageInfo())
                .setArtistId(artistId)
                .build();
        final PaintingsResponse response = paintingStub.getPaintingsForArtist(paintingsByArtist);
        return new PageImpl<>(
                response.getPaintingsList().stream().map(
                                m -> fromGrpcMessage(m,
                                        safeGetMuseum(m.getMuseumId()),
                                        safeGetArtist(m.getArtistId())))
                        .toList(),
                pageable,
                response.getTotalElements());
    }

    @Override
    public PaintingJson createPainting(@Nonnull PaintingJson paintingJson) {
        Painting painting = paintingStub.createPainting(
                        CreatePaintingRequest.newBuilder()
                                .setTitle(paintingJson.title())
                                .setDescription(paintingJson.description())
                                .setContent(paintingJson.content())
                                .setArtistId(paintingJson.artist().id())
                                .setMuseumId(paintingJson.museum().id())
                                .build())
                .getPainting();
        return fromGrpcMessage(painting,
                safeGetMuseum(painting.getMuseumId()),
                safeGetArtist(painting.getArtistId()));
    }

    @Override
    public PaintingJson updatePainting(@Nonnull PaintingJson paintingJson) {
        Painting painting = paintingStub.updatePainting(
                        UpdatePaintingRequest.newBuilder().setPainting(paintingJson.toGrpcMessage()).build())
                .getPainting();
        return fromGrpcMessage(painting,
                safeGetMuseum(painting.getMuseumId()),
                safeGetArtist(painting.getArtistId()));

    }

    private MuseumJson safeGetMuseum(String museumId) {
        try {
            return museumClient.getMuseum(museumId);
        } catch (Exception e) {
            return MuseumJson.empty();
        }
    }

    private ArtistJson safeGetArtist(String artistId) {
        try {
            return artistClient.getArtist(artistId);
        } catch (Exception e) {
            return ArtistJson.empty();
        }
    }
}

