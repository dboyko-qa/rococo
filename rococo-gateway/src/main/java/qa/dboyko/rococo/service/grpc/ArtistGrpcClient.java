package qa.dboyko.rococo.service.grpc;

import com.dboyko.rococo.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import qa.dboyko.rococo.model.ArtistJson;
import qa.dboyko.rococo.service.ArtistClient;
import qa.dboyko.rococo.util.GrpcImpl;
import qa.dboyko.rococo.util.GrpcPagination;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service
@GrpcImpl
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
    public Page<ArtistJson> allArtists(@Nullable Pageable pageable) {
        final ArtistsResponse response = artistStub.allArtists(
                AllArtistsRequest.newBuilder()
                        .setPageInfo(new GrpcPagination(pageable).pageInfo())
                        .build()
        );
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

