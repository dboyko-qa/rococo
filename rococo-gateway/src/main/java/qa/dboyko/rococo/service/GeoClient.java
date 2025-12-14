package qa.dboyko.rococo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import qa.dboyko.rococo.model.CountryJson;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface GeoClient {
    CountryJson getCountry(String id);

    Page<CountryJson> allCountries(Pageable pageable);
}
