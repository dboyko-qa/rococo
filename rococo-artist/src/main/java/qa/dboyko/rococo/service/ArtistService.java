package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import qa.dboyko.rococo.entity.ArtistEntity;
import qa.dboyko.rococo.ex.ArtistNotFoundException;
import qa.dboyko.rococo.mapper.ArtistGrpcMapper;
import qa.dboyko.rococo.repository.ArtistRepository;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
                .setArtist(ArtistGrpcMapper.toGrpc(artist))
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void allArtists(AllArtistsRequest request, StreamObserver<ArtistsResponse> responseObserver) {
        Page<ArtistEntity> allArtistsPage;

        Pageable pageable;
        PageInfo pageInfo = request.getPageInfo();

        if (pageInfo.getSize() > 0) {
            pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getSize());
        } else {
            pageable = Pageable.unpaged();
        }

        if (request.hasNameFilter()) {
            allArtistsPage = artistRepository.findAllByNameContainsIgnoreCase(
                    request.getNameFilter(),
                    pageable
            );
        } else {
            allArtistsPage = artistRepository.findAll(pageable);
        }

        responseObserver.onNext(
                ArtistsResponse.newBuilder()
                        .addAllArtists(allArtistsPage.getContent().stream().map(ArtistGrpcMapper::toGrpc).toList())
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
                        .setArtist(ArtistGrpcMapper.toGrpc(newArtist))
                        .build()
        );
        responseObserver.onCompleted();
    }


    @Override
    @Transactional
    public void updateArtist(UpdateArtistRequest request, StreamObserver<UpdateArtistResponse> responseObserver) {
        ArtistEntity artistEntity = artistRepository.findById(UUID.fromString(request.getArtist().getId()))
                .orElseThrow(() -> new ArtistNotFoundException(request.getArtist().getId()));

        ArtistEntity newArtistEntity = ArtistGrpcMapper.fromGrpc(request.getArtist());
        ArtistEntity updated = artistRepository.save(newArtistEntity);
        responseObserver.onNext(
                UpdateArtistResponse.newBuilder()
                        .setArtist(ArtistGrpcMapper.toGrpc(updated))
                        .build()
        );
        responseObserver.onCompleted();
    }

}
