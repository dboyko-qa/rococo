package qa.dboyko.rococo.enums;

import qa.dboyko.rococo.apiservice.grpc.GeoGrpcClient;
import qa.dboyko.rococo.model.CountryJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CountryJsonCache {

    private static List<CountryJson> countryCache = new ArrayList<>();
    private static GeoGrpcClient geoGrpcClient = new GeoGrpcClient();

    private static List<CountryJson> getCountries() {
        if (countryCache.isEmpty()) {
            countryCache = Arrays.stream(Country.values()).map(
                    country -> geoGrpcClient.getCountryByName(country.getName()))
                    .toList();
        }
        return countryCache;
    }

    public static CountryJson getRandomCountryJson() {
        return getCountries().get(ThreadLocalRandom.current().nextInt(getCountries().size()));
    }
}
