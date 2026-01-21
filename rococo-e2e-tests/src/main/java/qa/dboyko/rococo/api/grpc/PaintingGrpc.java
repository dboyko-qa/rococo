package qa.dboyko.rococo.api.grpc;

import com.dboyko.rococo.grpc.PaintingServiceGrpc;
import qa.dboyko.rococo.config.Config;

public class PaintingGrpc extends BaseGrpc {

    private static final Config CFG = Config.getInstance();

    public final PaintingServiceGrpc.PaintingServiceBlockingStub paintingStub;

    public PaintingGrpc() {
        super(CFG.paintingUrl());
        this.paintingStub = PaintingServiceGrpc.newBlockingStub(getChannel());
    }
}

