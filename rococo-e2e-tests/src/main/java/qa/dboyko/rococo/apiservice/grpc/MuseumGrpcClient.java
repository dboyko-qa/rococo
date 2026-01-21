package qa.dboyko.rococo.apiservice.grpc;

import com.dboyko.rococo.grpc.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.api.grpc.MuseumGrpc;
import qa.dboyko.rococo.model.MuseumJson;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static qa.dboyko.rococo.apiservice.utils.ApiUtils.toGrpcPageInfo;
import static qa.dboyko.rococo.model.MuseumJson.fromGrpcMessage;

public class MuseumGrpcClient {

    private MuseumGrpc museumGrpc = new MuseumGrpc();
    private GeoGrpcClient geoGrpcClient = new GeoGrpcClient();

    public static final PageInfo DEFAULT_PAGE_INFO = PageInfo.newBuilder()
            .setPage(0)
            .setSize(4)
            .build();

    public MuseumJson getMuseum(@Nonnull String id) {
        Museum museum = museumGrpc.museumStub.getMuseum(GetMuseumRequest.newBuilder().setId(id).build()).getMuseum();
        return fromGrpcMessage(
                museum,
                geoGrpcClient.getCountry(museum.getCountryId()));
    }

    public List<String> allMuseumsTitles() {
        return allMuseums(null, null).stream().map(MuseumJson::title).toList();
    }

    public Page<MuseumJson> allMuseums(@Nullable Pageable pageable,
                                       @Nullable String nameFilter) {

        AllMuseumsRequest.Builder requestBuilder = AllMuseumsRequest.newBuilder();
        if (nameFilter != null) {
            requestBuilder.setNameFilter(nameFilter);
        }

        if (pageable != null) {
            requestBuilder.setPageInfo(toGrpcPageInfo(pageable));
        }

        MuseumsResponse response =
                museumGrpc.museumStub.allMuseums(requestBuilder.build());

        List<MuseumJson> content = response.getMuseumsList().stream()
                .map(m -> fromGrpcMessage(m, geoGrpcClient.getCountry(m.getCountryId())))
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

    public MuseumJson getRandomMuseum() {
        List<MuseumJson> allMuseumsList = allMuseums(null, null).stream().toList();
        return allMuseumsList.get(ThreadLocalRandom.current().nextInt(allMuseumsList.size()));    }

    public MuseumJson createMuseum(@Nonnull MuseumJson museumJson) {
        Museum museum = museumGrpc.museumStub.createMuseum(
                        CreateMuseumRequest.newBuilder()
                                .setTitle(museumJson.title())
                                .setDescription(museumJson.description())
                                .setCity(museumJson.geo().city())
                                .setCountryId(museumJson.geo().country().id())
                                .setPhoto(museumJson.photo())
                                .build())
                .getMuseum();
        return fromGrpcMessage(museum,
                geoGrpcClient.getCountry(museum.getCountryId()));
    }

}
