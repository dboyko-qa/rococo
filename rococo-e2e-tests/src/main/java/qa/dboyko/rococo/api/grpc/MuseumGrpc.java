package qa.dboyko.rococo.api.grpc;

import com.dboyko.rococo.grpc.MuseumServiceGrpc;
import qa.dboyko.rococo.config.Config;

public class MuseumGrpc extends BaseGrpc {

    private static final Config CFG = Config.getInstance();

    public final MuseumServiceGrpc.MuseumServiceBlockingStub museumStub;

    public MuseumGrpc() {
        super(CFG.museumUrl());
        this.museumStub = MuseumServiceGrpc.newBlockingStub(getChannel());
    }
}

