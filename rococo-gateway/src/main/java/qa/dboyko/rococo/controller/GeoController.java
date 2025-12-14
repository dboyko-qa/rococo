package qa.dboyko.rococo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import qa.dboyko.rococo.model.CountryJson;
import qa.dboyko.rococo.service.GeoClient;

@RestController
@RequestMapping("/api/country")
@Validated
public class GeoController {
    private static final Logger LOG = LoggerFactory.getLogger(GeoController.class);

    @Autowired
    private GeoClient geoClient;

    @GetMapping("/{id}")
    public CountryJson getCountry(@PathVariable String id) {
        LOG.info("!!! call to get country {}", id);
        return geoClient.getCountry(id);
    }

    @GetMapping
    public Page<CountryJson> allCountries(Pageable pageable) {
        LOG.info("!!! call to get countries list {}", pageable.getPageNumber());
        Page<CountryJson> allCountryPage = geoClient.allCountries(pageable);

        return new PageImpl<>(
                allCountryPage.stream().toList(),
                pageable,
                allCountryPage.getTotalElements()
        );
    }
}
