package qa.dboyko.rococo.api.grpc;

import com.dboyko.rococo.grpc.GeoServiceGrpc;
import qa.dboyko.rococo.config.Config;

public class GeoGrpc extends BaseGrpc {
    private static final Config CFG = Config.getInstance();

    public final GeoServiceGrpc.GeoServiceBlockingStub geoStub;

    public GeoGrpc() {
        super(CFG.geoUrl());
        this.geoStub = GeoServiceGrpc.newBlockingStub(getChannel());
    }
}
