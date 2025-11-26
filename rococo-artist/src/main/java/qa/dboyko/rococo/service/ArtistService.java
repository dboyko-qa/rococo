package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.protobuf.ByteString;
import qa.dboyko.rococo.entity.ArtistEntity;
import qa.dboyko.rococo.repository.ArtistRepository;
import qa.dboyko.rococo.util.GrpcPagination;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@GrpcService
public class ArtistService extends ArtistServiceGrpc.ArtistServiceImplBase {

    private static Logger LOG = LoggerFactory.getLogger(ArtistService.class);

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void getArtist(GetArtistRequest request, StreamObserver<GetArtistResponse> responseObserver) {
        LOG.info("Received getArtist request for artist: {}", request.getName());
        String name = request.getName();
        Optional<ArtistEntity> artistOpt = artistRepository.findByName(name);
        ByteString avatar = ByteString.EMPTY;

        if (artistOpt.isPresent()) {
            ArtistEntity artist = artistOpt.get();
            GetArtistResponse response = GetArtistResponse.newBuilder()
                    .setArtist(artist.toGrpcArtist())
                    .build();
            responseObserver.onNext(response);
        } else {
            responseObserver.onError(new Exception("Artist not found"));
        }
        responseObserver.onCompleted();
    }

    @Override
    public void allArtists(AllArtistsRequest request, StreamObserver<ArtistsResponse> responseObserver) {
        final PageInfo pageInfo = request.getPageInfo();
        final Page<ArtistEntity> allArtistsPage = artistRepository.findAll(
                new GrpcPagination(
                        pageInfo.getPage(),
                        pageInfo.getSize()
                ).pageable()
        );

        responseObserver.onNext(
                ArtistsResponse.newBuilder()
                        .addAllArtists(allArtistsPage.getContent().stream().map(ArtistEntity::toGrpcArtist).toList())
                        .setTotalElements(allArtistsPage.getTotalElements())
                        .setTotalPages(allArtistsPage.getTotalPages())
                        .build()
        );
        responseObserver.onCompleted();
    }


}
