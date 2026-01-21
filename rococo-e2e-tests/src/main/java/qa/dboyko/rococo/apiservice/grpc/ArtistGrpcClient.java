package qa.dboyko.rococo.apiservice.grpc;

import com.dboyko.rococo.grpc.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.api.grpc.ArtistGrpc;
import qa.dboyko.rococo.model.ArtistJson;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static qa.dboyko.rococo.apiservice.utils.ApiUtils.toGrpcPageInfo;
import static qa.dboyko.rococo.model.ArtistJson.fromGrpcMessage;

public class ArtistGrpcClient {

    private ArtistGrpc artistGrpc = new ArtistGrpc();

    public static final PageInfo DEFAULT_PAGE_INFO = PageInfo.newBuilder()
            .setPage(0)
            .setSize(4)
            .build();

    public ArtistJson createArtist(@Nonnull ArtistJson artistJson) {
        Artist artist = artistGrpc.artistStub.createArtist(
                        CreateArtistRequest.newBuilder()
                                .setName(artistJson.name())
                                .setBiography(artistJson.biography())
                                .setPhoto(artistJson.photo())
                                .build())
                .getArtist();
        return fromGrpcMessage(artist);
    }

    public ArtistJson getArtist(@Nonnull String id) {
        Artist artist = artistGrpc.artistStub.getArtist(GetArtistRequest.newBuilder().setId(id).build()).getArtist();
        return ArtistJson.fromGrpcMessage(
                artist);
    }

    public ArtistJson getRandomArtist() {
        List<ArtistJson> allArtistsList = allArtists(null, null).stream().toList();
        return allArtistsList.get(ThreadLocalRandom.current().nextInt(allArtistsList.size()));
    }

    public List<String> allArtistsNames() {
        return allArtists(null, null).map(ArtistJson::name).stream().toList();
    }

    public Page<ArtistJson> allArtists(@Nullable Pageable pageable,
                                       @Nullable String nameFilter) {

        AllArtistsRequest.Builder requestBuilder = AllArtistsRequest.newBuilder();
        if (nameFilter != null) {
            requestBuilder.setNameFilter(nameFilter);
        }

        if (pageable != null) {
            requestBuilder.setPageInfo(toGrpcPageInfo(pageable));
        }

        ArtistsResponse response =
                artistGrpc.artistStub.allArtists(requestBuilder.build());

        List<ArtistJson> content = response.getArtistsList().stream()
                .map(a -> fromGrpcMessage(a))
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

}
