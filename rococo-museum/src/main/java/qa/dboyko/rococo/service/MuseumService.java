package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import qa.boyko.rococo.util.GrpcPagination;
import qa.dboyko.rococo.entity.MuseumEntity;
import qa.dboyko.rococo.ex.MuseumNotFoundException;
import qa.dboyko.rococo.repository.MuseumRepository;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static qa.dboyko.rococo.entity.MuseumEntity.fromGrpcMuseum;

@GrpcService
public class MuseumService extends MuseumServiceGrpc.MuseumServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(MuseumService.class);

    private final MuseumRepository museumRepository;

    @Autowired
    public MuseumService(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }

    @Override
    public void getMuseum(GetMuseumRequest request, StreamObserver<GetMuseumResponse> responseObserver) {
        LOG.info("Received getMuseum request for museum: {}", request.getId());
        String id = request.getId();
        MuseumEntity museum = museumRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new MuseumNotFoundException(id));

        GetMuseumResponse response = GetMuseumResponse.newBuilder()
                .setMuseum(museum.toGrpcMuseum())
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void allMuseums(AllMuseumsRequest request, StreamObserver<MuseumsResponse> responseObserver) {
        Page<MuseumEntity> allMuseumsPage;
        final PageInfo pageInfo = request.getPageInfo();
        if (request.hasNameFilter()) {
            allMuseumsPage = museumRepository.findAllByTitleContainsIgnoreCase(
                    request.getNameFilter(),
                    new GrpcPagination(
                            pageInfo.getPage(),
                            pageInfo.getSize()
                    ).pageable()
            );
        }
        else {
            allMuseumsPage = museumRepository.findAll(
                    new GrpcPagination(
                            pageInfo.getPage(),
                            pageInfo.getSize()
                    ).pageable()
            );
        }

        responseObserver.onNext(
                MuseumsResponse.newBuilder()
                        .addAllMuseums(allMuseumsPage.getContent().stream().map(MuseumEntity::toGrpcMuseum).toList())
                        .setTotalElements(allMuseumsPage.getTotalElements())
                        .setTotalPages(allMuseumsPage.getTotalPages())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void createMuseum(CreateMuseumRequest request, StreamObserver<CreateMuseumResponse> responseObserver) {
        MuseumEntity museumEntity = new MuseumEntity();
        museumEntity.setTitle(request.getTitle());
        museumEntity.setDescription(request.getDescription());
        museumEntity.setCity(request.getCity());
        museumEntity.setCountryId(UUID.fromString(request.getCountryId()));
        museumEntity.setPhoto(!request.getPhoto().isEmpty()
                ? request.getPhoto().getBytes(StandardCharsets.UTF_8)
                : null);

        MuseumEntity newMuseum;

        newMuseum = museumRepository.save(museumEntity);

        responseObserver.onNext(
                CreateMuseumResponse.newBuilder()
                        .setMuseum(newMuseum.toGrpcMuseum())
                        .build()
        );
        responseObserver.onCompleted();
    }


    @Override
    public void updateMuseum(UpdateMuseumRequest request, StreamObserver<UpdateMuseumResponse> responseObserver) {
        MuseumEntity museumEntity = museumRepository.findById(UUID.fromString(request.getMuseum().getId()))
                .orElseThrow(() -> new MuseumNotFoundException(request.getMuseum().getId()));

        MuseumEntity newMuseumEntity = fromGrpcMuseum(request.getMuseum());
        MuseumEntity updated = museumRepository.save(newMuseumEntity);
        responseObserver.onNext(
                UpdateMuseumResponse.newBuilder()
                        .setMuseum(updated.toGrpcMuseum())
                        .build()
        );
        responseObserver.onCompleted();
    }

}
