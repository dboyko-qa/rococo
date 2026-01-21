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
import qa.dboyko.rococo.entity.PaintingEntity;
import qa.dboyko.rococo.ex.PaintingNotFoundException;
import qa.dboyko.rococo.mapper.PaintingGrpcMapper;
import qa.dboyko.rococo.repository.PaintingRepository;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static qa.dboyko.rococo.mapper.PaintingGrpcMapper.fromGrpcPainting;

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
                .setPainting(PaintingGrpcMapper.toGrpcPainting(painting))
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void allPaintings(AllPaintingsRequest request, StreamObserver<PaintingsResponse> responseObserver) {
        LOG.info("Received getAllPaintings request");
        Page<PaintingEntity> allPaintingsPage;
        Pageable pageable;
        PageInfo pageInfo = request.getPageInfo();

        if (pageInfo.getSize() > 0) {
            pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getSize());
        } else {
            pageable = Pageable.unpaged();
        }

        if (request.hasTitleFilter()) {
            allPaintingsPage = paintingRepository.findAllByTitleContainsIgnoreCase(
                    request.getTitleFilter(),
                    pageable
            );
        } else {
            allPaintingsPage = paintingRepository.findAll(pageable);
        }

        responseObserver.onNext(
                PaintingsResponse.newBuilder()
                        .addAllPaintings(allPaintingsPage.getContent().stream().map(PaintingGrpcMapper::toGrpcPainting).toList())
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
        Pageable pageable;
        PageInfo pageInfo = request.getPageInfo();

        if (pageInfo.getSize() > 0) {
            pageable = PageRequest.of(pageInfo.getPage(), pageInfo.getSize());
        } else {
            pageable = Pageable.unpaged();
        }
        allPaintingsPage = paintingRepository.findAllByArtistId(
                UUID.fromString(request.getArtistId()),
                pageable);


        responseObserver.onNext(
                PaintingsResponse.newBuilder()
                        .addAllPaintings(allPaintingsPage.getContent().stream().map(PaintingGrpcMapper::toGrpcPainting).toList())
                        .setTotalElements(allPaintingsPage.getTotalElements())
                        .setTotalPages(allPaintingsPage.getTotalPages())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void createPainting(CreatePaintingRequest request, StreamObserver<CreatePaintingResponse> responseObserver) {
        LOG.info("Received createPainting request, title: {}, artistId: {}, museumId: {}",
                request.getTitle(), request.getArtistId(), request.getMuseumId());
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
                        .setPainting(PaintingGrpcMapper.toGrpcPainting(newPainting))
                        .build()
        );
        responseObserver.onCompleted();
    }


    @Override
    @Transactional
    public void updatePainting(UpdatePaintingRequest request, StreamObserver<UpdatePaintingResponse> responseObserver) {
        LOG.info("Received updatePainting request for painting id: {}", request.getPainting().getId());
        PaintingEntity paintingEntity = paintingRepository.findById(UUID.fromString(request.getPainting().getId()))
                .orElseThrow(() -> new PaintingNotFoundException(request.getPainting().getId()));

        PaintingEntity newPaintingEntity = fromGrpcPainting(request.getPainting());
        PaintingEntity updated = paintingRepository.save(newPaintingEntity);
        responseObserver.onNext(
                UpdatePaintingResponse.newBuilder()
                        .setPainting(PaintingGrpcMapper.toGrpcPainting(updated))
                        .build()
        );
        responseObserver.onCompleted();
    }

}
