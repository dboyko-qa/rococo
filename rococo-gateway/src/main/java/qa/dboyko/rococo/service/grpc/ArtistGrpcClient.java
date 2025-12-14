package qa.dboyko.rococo.service.grpc;

import com.dboyko.rococo.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.service.ArtistClient;
import qa.dboyko.rococo.util.GrpcPagination;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service
@ConditionalOnProperty(prefix = "rococo-artist", name = "client", havingValue = "grpc")
public class ArtistGrpcClient implements ArtistClient {

    @GrpcClient("grpcArtistClient")
    private ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    @Override
    public ArtistJson getArtist(@Nonnull String id) {
        GetArtistRequest request = GetArtistRequest.newBuilder()
                .setId(id)
                .build();

        return ArtistJson.fromGrpcMessage(artistStub.getArtist(request).getArtist());
    }

    @Nonnull
    @Override
    public Page<ArtistJson> allArtists(@Nullable Pageable pageable, @Nullable String nameFilter) {
        AllArtistsRequest allArtistsRequest = AllArtistsRequest.newBuilder()
                .setPageInfo(new GrpcPagination(pageable).pageInfo())
                .build();
        if (nameFilter != null && !nameFilter.isBlank()) {
            allArtistsRequest = allArtistsRequest.toBuilder().setNameFilter(nameFilter).build();
        }
        final ArtistsResponse response = artistStub.allArtists(allArtistsRequest);
        return new PageImpl<>(
                response.getArtistsList().stream().map(ArtistJson::fromGrpcMessage).toList(),
                pageable,
                response.getTotalElements()
        );
    }

    @Override
    public ArtistJson createArtist(@Nonnull ArtistJson artistJson) {
        return ArtistJson.fromGrpcMessage(
                artistStub.createArtist(CreateArtistRequest.newBuilder()
                    .setName(artistJson.name())
                    .setBiography(artistJson.biography())
                    .setPhoto(artistJson.photo())
                    .build())
                .getArtist());
    }

    @Override
    public ArtistJson updateArtist(@Nonnull ArtistJson artistJson) {
        return ArtistJson.fromGrpcMessage(
                artistStub.updateArtist(UpdateArtistRequest.newBuilder()
                                .setArtist(artistJson.toGrpcMessage())
                        .build())
                        .getArtist());

    }
}

