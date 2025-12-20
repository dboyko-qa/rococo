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
import qa.dboyko.rococo.entity.PaintingEntity;
import qa.dboyko.rococo.ex.PaintingNotFoundException;
import qa.dboyko.rococo.repository.PaintingRepository;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static qa.dboyko.rococo.entity.PaintingEntity.fromGrpcPainting;

@GrpcService
public class PaintingService extends PaintingServiceGrpc.PaintingServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(PaintingService.class);

    private final PaintingRepository paintingRepository;

    @Autowired
    public PaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    @Override
    public void getPainting(GetPaintingRequest request, StreamObserver<GetPaintingResponse> responseObserver) {
        LOG.info("Received getPainting request for painting: {}", request.getId());
        String id = request.getId();
        PaintingEntity painting = paintingRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new PaintingNotFoundException(id));

        GetPaintingResponse response = GetPaintingResponse.newBuilder()
                .setPainting(painting.toGrpcPainting())
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void allPaintings(AllPaintingsRequest request, StreamObserver<PaintingsResponse> responseObserver) {
        LOG.info("Received getAllPaintings request");
        Page<PaintingEntity> allPaintingsPage;
        final PageInfo pageInfo = request.getPageInfo();
        if (request.hasTitleFilter()) {
            allPaintingsPage = paintingRepository.findAllByTitleContainsIgnoreCase(
                    request.getTitleFilter(),
                    new GrpcPagination(
                            pageInfo.getPage(),
                            pageInfo.getSize()
                    ).pageable()
            );
        } else {
            allPaintingsPage = paintingRepository.findAll(
                    new GrpcPagination(
                            pageInfo.getPage(),
                            pageInfo.getSize()
                    ).pageable()
            );
        }

        responseObserver.onNext(
                PaintingsResponse.newBuilder()
                        .addAllPaintings(allPaintingsPage.getContent().stream().map(PaintingEntity::toGrpcPainting).toList())
                        .setTotalElements(allPaintingsPage.getTotalElements())
                        .setTotalPages(allPaintingsPage.getTotalPages())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getPaintingsForArtist(GetPaintingsForArtistRequest request, StreamObserver<PaintingsResponse> responseObserver) {
        LOG.info("Received getPaintings request for artist: {}", request.getArtistId());
        Page<PaintingEntity> allPaintingsPage;
        final PageInfo pageInfo = request.getPageInfo();
        allPaintingsPage = paintingRepository.findAllByArtistId(
                UUID.fromString(request.getArtistId()),
                new GrpcPagination(
                        pageInfo.getPage(),
                        pageInfo.getSize()
                ).pageable());


        responseObserver.onNext(
                PaintingsResponse.newBuilder()
                        .addAllPaintings(allPaintingsPage.getContent().stream().map(PaintingEntity::toGrpcPainting).toList())
                        .setTotalElements(allPaintingsPage.getTotalElements())
                        .setTotalPages(allPaintingsPage.getTotalPages())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void createPainting(CreatePaintingRequest request, StreamObserver<CreatePaintingResponse> responseObserver) {
        LOG.info("Received request to create painting: {}",
                request.getTitle() + " " + request.getDescription() + " " + request.getArtistId() + " " + request.getMuseumId());
        PaintingEntity paintingEntity = new PaintingEntity();
        paintingEntity.setTitle(request.getTitle());
        paintingEntity.setDescription(request.getDescription());
        paintingEntity.setContent(!request.getContent().isEmpty()
                ? request.getContent().getBytes(StandardCharsets.UTF_8)
                : null);
        paintingEntity.setArtistId(UUID.fromString(request.getArtistId()));
        paintingEntity.setMuseumId(UUID.fromString(request.getMuseumId()));

        PaintingEntity newPainting;

        newPainting = paintingRepository.save(paintingEntity);

        responseObserver.onNext(
                CreatePaintingResponse.newBuilder()
                        .setPainting(newPainting.toGrpcPainting())
                        .build()
        );
        responseObserver.onCompleted();
    }


    @Override
    @Transactional
    public void updatePainting(UpdatePaintingRequest request, StreamObserver<UpdatePaintingResponse> responseObserver) {
        LOG.info("Received request to update painting: {}", request.toString());
        PaintingEntity paintingEntity = paintingRepository.findById(UUID.fromString(request.getPainting().getId()))
                .orElseThrow(() -> new PaintingNotFoundException(request.getPainting().getId()));

        PaintingEntity newPaintingEntity = fromGrpcPainting(request.getPainting());
        PaintingEntity updated = paintingRepository.save(newPaintingEntity);
        responseObserver.onNext(
                UpdatePaintingResponse.newBuilder()
                        .setPainting(updated.toGrpcPainting())
                        .build()
        );
        responseObserver.onCompleted();
    }

}
