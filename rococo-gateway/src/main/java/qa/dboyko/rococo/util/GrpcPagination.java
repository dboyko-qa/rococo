package qa.dboyko.rococo.util;

import com.dboyko.rococo.grpc.PageInfo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;

public class GrpcPagination {
    private final Pageable pageable;

    public GrpcPagination(@Nonnull Pageable pageable) {
        this.pageable = pageable;
    }

    public @Nonnull PageInfo pageInfo() {
        return PageInfo.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .build();
    }
}