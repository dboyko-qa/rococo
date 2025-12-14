package qa.dboyko.rococo.service;

import com.dboyko.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import qa.boyko.rococo.util.GrpcPagination;
import qa.dboyko.rococo.entity.CountryEntity;
import qa.dboyko.rococo.ex.CountryNotFoundException;
import qa.dboyko.rococo.repository.CountryRepository;

import java.util.UUID;


@GrpcService
public class GeoService extends GeoServiceGrpc.GeoServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(GeoService.class);

    private final CountryRepository countryRepository;

    @Autowired
    public GeoService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void getCountry(GetCountryRequest request, StreamObserver<GetCountryResponse> responseObserver) {
        LOG.info("!!! call to get country with id {}", request.getId());
        String id = request.getId();
        CountryEntity country = countryRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new CountryNotFoundException(id));

        GetCountryResponse response = GetCountryResponse.newBuilder()
                .setCountry(country.toGrpcCountry())
                .build();
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void getAllCountries(GetAllCountriesRequest request, StreamObserver<GetAllCountriesResponse> responseObserver) {
        LOG.info("!!! call to get all countries");
        final PageInfo pageInfo = request.getPageInfo();
        final Page<CountryEntity> allCountriesPage = countryRepository.findAll(
                new GrpcPagination(
                        pageInfo.getPage(),
                        pageInfo.getSize()
                ).pageable()
        );
        LOG.info("Number of countries - " + allCountriesPage.getSize());

        responseObserver.onNext(
                GetAllCountriesResponse.newBuilder()
                        .addAllCountries(allCountriesPage.getContent().stream().map(CountryEntity::toGrpcCountry).toList())
                        .setTotalElements(allCountriesPage.getTotalElements())
                        .setTotalPages(allCountriesPage.getTotalPages())
                        .build()
        );
        responseObserver.onCompleted();
    }

}
