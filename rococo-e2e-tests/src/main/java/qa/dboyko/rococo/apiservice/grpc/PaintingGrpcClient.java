package qa.dboyko.rococo.apiservice.grpc;

import com.dboyko.rococo.grpc.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.api.grpc.PaintingGrpc;
import qa.dboyko.rococo.model.PaintingJson;

import java.util.List;

import static qa.dboyko.rococo.apiservice.utils.ApiUtils.toGrpcPageInfo;
import static qa.dboyko.rococo.model.PaintingJson.fromGrpcMessage;

public class PaintingGrpcClient {

    private PaintingGrpc paintingGrpc = new PaintingGrpc();
    private ArtistGrpcClient artistGrpcClient = new ArtistGrpcClient();
    private MuseumGrpcClient museumGrpcClient = new MuseumGrpcClient();

    public static final PageInfo DEFAULT_PAGE_INFO = PageInfo.newBuilder()
            .setPage(0)
            .setSize(4)
            .build();

    public PaintingJson createPainting(@Nonnull PaintingJson paintingJson) {
        Painting painting = paintingGrpc.paintingStub.createPainting(
                        CreatePaintingRequest.newBuilder()
                                .setTitle(paintingJson.title())
                                .setDescription(paintingJson.description())
                                .setContent(paintingJson.content())
                                .setArtistId(paintingJson.artist().id())
                                .setMuseumId(paintingJson.museum().id())
                                .build())
                .getPainting();
        return fromGrpcMessage(painting,
                museumGrpcClient.getMuseum(painting.getMuseumId()),
                artistGrpcClient.getArtist(painting.getArtistId()));
    }

    public Page<PaintingJson> allPaintings(@Nullable Pageable pageable,
                                       @Nullable String nameFilter) {

        AllPaintingsRequest.Builder requestBuilder = AllPaintingsRequest.newBuilder();
        if (nameFilter != null) {
            requestBuilder.setTitleFilter(nameFilter);
        }

        if (pageable != null) {
            requestBuilder.setPageInfo(toGrpcPageInfo(pageable));
        }

        PaintingsResponse response =
                paintingGrpc.paintingStub.allPaintings(requestBuilder.build());

        List<PaintingJson> content = response.getPaintingsList().stream()
                .map(p -> PaintingJson.fromGrpcMessage(
                        p,
                        museumGrpcClient.getMuseum(p.getMuseumId()),
                        artistGrpcClient.getArtist(p.getArtistId())))
                .toList();

        Pageable resultPageable = pageable != null
                ? pageable
                : Pageable.unpaged();

        return new PageImpl<>(
                content,
                resultPageable,
                response.getTotalElements()
        );
    }

    public Page<PaintingJson> getPaintingsForArtist(@Nullable Pageable pageable,
                                       @Nonnull String artistId) {

        GetPaintingsForArtistRequest.Builder requestBuilder = GetPaintingsForArtistRequest.newBuilder();
        requestBuilder.setArtistId(artistId);

        if (pageable != null) {
            requestBuilder.setPageInfo(toGrpcPageInfo(pageable));
        }

        PaintingsResponse response =
                paintingGrpc.paintingStub.getPaintingsForArtist(requestBuilder.build());

        List<PaintingJson> content = response.getPaintingsList().stream()
                .map(p -> PaintingJson.fromGrpcMessage(
                        p,
                        museumGrpcClient.getMuseum(p.getMuseumId()),
                        artistGrpcClient.getArtist(p.getArtistId())))
                .toList();

        Pageable resultPageable = pageable != null
                ? pageable
                : Pageable.unpaged();

        return new PageImpl<>(
                content,
                resultPageable,
                response.getTotalElements()
        );
    }

    public PaintingJson getRandomPainting() {
        return allPaintings(null, null).stream().findAny().get();
    }

}
