package qa.dboyko.rococo.api.grpc;

import com.dboyko.rococo.grpc.ArtistServiceGrpc;
import qa.dboyko.rococo.config.Config;

public class ArtistGrpc extends BaseGrpc {

    private static final Config CFG = Config.getInstance();

    public final ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    public ArtistGrpc() {
        super(CFG.artistUrl());
        this.artistStub = ArtistServiceGrpc.newBlockingStub(getChannel());
    }
}

