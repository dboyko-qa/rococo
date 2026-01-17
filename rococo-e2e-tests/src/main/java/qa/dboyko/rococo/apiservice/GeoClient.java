package qa.dboyko.rococo.apiservice;

import com.dboyko.rococo.grpc.GetCountryByNameRequest;
import com.dboyko.rococo.grpc.GetCountryRequest;
import qa.dboyko.rococo.api.grpc.GeoGrpc;
import qa.dboyko.rococo.model.CountryJson;

import javax.annotation.Nonnull;

public class GeoClient {

    private GeoGrpc geoGrpc = new GeoGrpc();
    public CountryJson getCountry(@Nonnull String id) {
        return CountryJson.fromGrpcMessage(geoGrpc.geoStub.getCountry(
                GetCountryRequest.newBuilder().setId(id).build()).getCountry());
    }

    public CountryJson getCountryByName(@Nonnull String name) {
        return CountryJson.fromGrpcMessage(geoGrpc.geoStub.getCountryByName(
                GetCountryByNameRequest.newBuilder().setName(name).build()).getCountry());
    }
}
