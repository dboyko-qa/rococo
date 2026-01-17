package qa.dboyko.rococo.api.grpc;

import com.dboyko.rococo.grpc.UserDataServiceGrpc;
import qa.dboyko.rococo.config.Config;

public class UserdataGrpc extends BaseGrpc {

    private static final Config CFG = Config.getInstance();

    public final UserDataServiceGrpc.UserDataServiceBlockingStub userdataStub;

    public UserdataGrpc() {
        super(CFG.userdataUrl());
        this.userdataStub = UserDataServiceGrpc.newBlockingStub(getChannel());
    }
}

