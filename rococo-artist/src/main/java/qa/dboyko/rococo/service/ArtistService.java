package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import qa.boyko.rococo.util.GrpcPagination;
import qa.dboyko.rococo.entity.ArtistEntity;
import qa.dboyko.rococo.ex.ArtistNotFoundException;
import qa.dboyko.rococo.repository.ArtistRepository;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import static qa.dboyko.rococo.entity.ArtistEntity.fromGrpcArtist;

@GrpcService
public class ArtistService extends ArtistServiceGrpc.ArtistServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(ArtistService.class);

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void getArtist(GetArtistRequest request, StreamObserver<GetArtistResponse> responseObserver) {
        LOG.info("Received getArtist request for artist: {}", request.getId());
        String id = request.getId();
        ArtistEntity artist = artistRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ArtistNotFoundException(id));

        GetArtistResponse response = GetArtistResponse.newBuilder()
                .setArtist(artist.toGrpcArtist())
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void allArtists(AllArtistsRequest request, StreamObserver<ArtistsResponse> responseObserver) {
        Page<ArtistEntity> allArtistsPage;
        final PageInfo pageInfo = request.getPageInfo();
        if (request.hasNameFilter()) {
            allArtistsPage = artistRepository.findAllByNameContainsIgnoreCase(
                    request.getNameFilter(),
                    new GrpcPagination(
                            pageInfo.getPage(),
                            pageInfo.getSize()
                    ).pageable()
            );
        }
        else {
            allArtistsPage = artistRepository.findAll(
                    new GrpcPagination(
                            pageInfo.getPage(),
                            pageInfo.getSize()
                    ).pageable()
            );
        }

        responseObserver.onNext(
                ArtistsResponse.newBuilder()
                        .addAllArtists(allArtistsPage.getContent().stream().map(ArtistEntity::toGrpcArtist).toList())
                        .setTotalElements(allArtistsPage.getTotalElements())
                        .setTotalPages(allArtistsPage.getTotalPages())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void createArtist(CreateArtistRequest request, StreamObserver<CreateArtistResponse> responseObserver) {
        ArtistEntity artistEntity = new ArtistEntity();
                artistEntity.setName(request.getName());
                artistEntity.setBiography(request.getBiography());
                artistEntity.setPhoto(!request.getPhoto().isEmpty()
                        ? request.getPhoto().getBytes(StandardCharsets.UTF_8)
                        : null);

        ArtistEntity newArtist;

        newArtist = artistRepository.save(artistEntity);

        responseObserver.onNext(
                CreateArtistResponse.newBuilder()
                        .setArtist(newArtist.toGrpcArtist())
                        .build()
        );
        responseObserver.onCompleted();
    }


    @Override
    @Transactional
    public void updateArtist(UpdateArtistRequest request, StreamObserver<UpdateArtistResponse> responseObserver) {
        ArtistEntity artistEntity = artistRepository.findById(UUID.fromString(request.getArtist().getId()))
                .orElseThrow(() -> new ArtistNotFoundException(request.getArtist().getId()));

        ArtistEntity newArtistEntity = fromGrpcArtist(request.getArtist());
        ArtistEntity updated = artistRepository.save(newArtistEntity);
        responseObserver.onNext(
                UpdateArtistResponse.newBuilder()
                        .setArtist(updated.toGrpcArtist())
                        .build()
        );
        responseObserver.onCompleted();
    }

}
