package qa.dboyko.rococo.apiservice;

import com.dboyko.rococo.grpc.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.api.grpc.MuseumGrpc;
import qa.dboyko.rococo.model.MuseumJson;

import java.util.List;

import static qa.dboyko.rococo.apiservice.utils.ApiUtils.toGrpcPageInfo;
import static qa.dboyko.rococo.model.MuseumJson.fromGrpcMessage;

public class MuseumClient {

    private MuseumGrpc museumGrpc = new MuseumGrpc();
    private GeoClient geoClient = new GeoClient();

    public static final PageInfo DEFAULT_PAGE_INFO = PageInfo.newBuilder()
            .setPage(0)
            .setSize(4)
            .build();

    public MuseumJson getMuseum(@Nonnull String id) {
        Museum museum = museumGrpc.museumStub.getMuseum(GetMuseumRequest.newBuilder().setId(id).build()).getMuseum();
        return fromGrpcMessage(
                museum,
                geoClient.getCountry(museum.getCountryId()));
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
                .map(m -> fromGrpcMessage(m, geoClient.getCountry(m.getCountryId())))
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


    //        @Nonnull
//        public Page<MuseumJson> allMuseums(@Nullable Pageable pageable, @Nullable String nameFilter) {
//            AllMuseumsRequest allMuseumsRequest = AllMuseumsRequest.newBuilder()
//                    .setPageInfo(new GrpcPagination(pageable).pageInfo())
//                    .build();
//            if (nameFilter != null && !nameFilter.isBlank()) {
//                allMuseumsRequest = allMuseumsRequest.toBuilder().setNameFilter(nameFilter).build();
//            }
//            final MuseumsResponse response = museumStub.allMuseums(allMuseumsRequest);
//            return new PageImpl<>(
//                    response.getMuseumsList().stream().map(
//                                    m -> fromGrpcMessage(m, safeGetCountry(m.getCountryId())))
//                            .toList(),
//                    pageable,
//                    response.getTotalElements()
//            );
//
//        }
//
//        @Override
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
                geoClient.getCountry(museum.getCountryId()));
    }
//
//        @Override
//        public MuseumJson updateMuseum(@Nonnull MuseumJson museumJson) {
//            Museum museum = museumStub.updateMuseum(
//                            UpdateMuseumRequest.newBuilder().setMuseum(museumJson.toGrpcMessage()).build())
//                    .getMuseum();
//            return fromGrpcMessage(museum, safeGetCountry(museum.getCountryId()));
//
//        }

}
