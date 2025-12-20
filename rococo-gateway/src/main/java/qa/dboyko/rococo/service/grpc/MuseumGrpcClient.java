package qa.dboyko.rococo.service.grpc;

import com.dboyko.rococo.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import qa.dboyko.rococo.model.MuseumJson;
import qa.dboyko.rococo.service.GeoClient;
import qa.dboyko.rococo.service.MuseumClient;
import qa.dboyko.rococo.util.GrpcPagination;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static qa.dboyko.rococo.model.MuseumJson.fromGrpcMessage;

@Service
@ConditionalOnProperty(prefix = "rococo-museum", name = "client", havingValue = "grpc")
public class MuseumGrpcClient implements MuseumClient {

    @GrpcClient("grpcMuseumClient")
    private MuseumServiceGrpc.MuseumServiceBlockingStub museumStub;

    @Autowired
    private GeoClient geoClient;

    @Override
    public MuseumJson getMuseum(@Nonnull String id) {
        Museum museum = museumStub.getMuseum(GetMuseumRequest.newBuilder().setId(id).build()).getMuseum();
        return fromGrpcMessage(
                museum,
                geoClient.getCountry(museum.getCountryId()).name());
    }

    @Nonnull
    @Override
    public Page<MuseumJson> allMuseums(@Nullable Pageable pageable, @Nullable String nameFilter) {
        AllMuseumsRequest allMuseumsRequest = AllMuseumsRequest.newBuilder()
                .setPageInfo(new GrpcPagination(pageable).pageInfo())
                .build();
        if (nameFilter != null && !nameFilter.isBlank()) {
            allMuseumsRequest = allMuseumsRequest.toBuilder().setNameFilter(nameFilter).build();
        }
        final MuseumsResponse response = museumStub.allMuseums(allMuseumsRequest);
        return new PageImpl<>(
                response.getMuseumsList().stream().map(
                                m -> fromGrpcMessage(m, geoClient.getCountry(m.getCountryId()).name()))
                        .toList(),
                pageable,
                response.getTotalElements()
        );

    }

    @Override
    public MuseumJson createMuseum(@Nonnull MuseumJson museumJson) {
        Museum museum = museumStub.createMuseum(
                        CreateMuseumRequest.newBuilder()
                                .setTitle(museumJson.title())
                                .setDescription(museumJson.description())
                                .setCity(museumJson.geo().city())
                                .setCountryId(museumJson.geo().country().id())
                                .setPhoto(museumJson.photo())
                                .build())
                .getMuseum();
        return fromGrpcMessage(museum, geoClient.getCountry(museum.getCountryId()).name());
    }

    @Override
    public MuseumJson updateMuseum(@Nonnull MuseumJson museumJson) {
        Museum museum = museumStub.updateMuseum(
                        UpdateMuseumRequest.newBuilder().setMuseum(museumJson.toGrpcMessage()).build())
                .getMuseum();
        return fromGrpcMessage(museum, geoClient.getCountry(museum.getCountryId()).name());

    }
}

