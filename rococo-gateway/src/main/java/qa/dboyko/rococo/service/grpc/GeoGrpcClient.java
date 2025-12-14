package qa.dboyko.rococo.service.grpc;

import com.dboyko.rococo.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import qa.dboyko.rococo.model.CountryJson;
import qa.dboyko.rococo.service.GeoClient;
import qa.dboyko.rococo.util.GrpcPagination;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service
@ConditionalOnProperty(prefix = "rococo-geo", name = "client", havingValue = "grpc")
public class GeoGrpcClient implements GeoClient {

    @GrpcClient("grpcGeoClient")
    private GeoServiceGrpc.GeoServiceBlockingStub geoStub;

    @Override
    public CountryJson getCountry(@Nonnull String id) {
        GetCountryRequest request = GetCountryRequest.newBuilder()
                .setId(id)
                .build();

        return CountryJson.fromGrpcMessage(geoStub.getCountry(request).getCountry());
    }

    @Nonnull
    @Override
    public Page<CountryJson> allCountries(@Nullable Pageable pageable) {
        final GetAllCountriesResponse response = geoStub.getAllCountries(
                GetAllCountriesRequest.newBuilder()
                        .setPageInfo(new GrpcPagination(pageable).pageInfo())
                        .build()
        );
        return new PageImpl<>(
                response.getCountriesList().stream().map(CountryJson::fromGrpcMessage).toList(),
                pageable,
                response.getTotalElements()
        );
    }
}

